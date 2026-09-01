package net.greenjab.nekomasfixed.mixin.boat;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.BigBoat;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Not part of the core "keep and expect pain" set, but the pirate-patrol feature (BigBoat spawning)
 * touches several 26.2-only APIs at once.
 * <ul>
 * <li>{@code EntitySpawnReason} is {@code MobSpawnType} on 1.20.1 (matches boat.MobMixin's note).
 *     {@code EntityTypes.PILLAGER} is {@code EntityType.PILLAGER} — 1.20.1 keeps entity-type
 *     constants directly on {@code EntityType} itself, with no separate plural holder class
 *     (VERIFIED forge-1.20.1-mapped-src EntityType.java:243).</li>
 * <li>{@code PatrolSpawner#tick} is {@code int tick(ServerLevel, boolean, boolean)} on 1.20.1, not
 *     26.2's apparent {@code void tick(ServerLevel, boolean)} — VERIFIED
 *     forge-1.20.1-mapped-src PatrolSpawner.java:23 (a 3rd boolean param, and an int budget-count
 *     return instead of void). The {@code Math.ceil(double)} anchor and the {@code player}/
 *     {@code spawnPos} locals this injector captures both still exist at the identical point inside
 *     it (PatrolSpawner.java:57-58). Cancelling now means supplying a return value: 0, since this
 *     mod's spawns bypass vanilla's counted budget entirely via a direct
 *     {@code addFreshEntityWithPassengers} call, matching what an uncounted spawn should report.</li>
 * <li>{@code EntityType#create} has no 2-arg {@code (Level, MobSpawnType)} convenience overload on
 *     1.20.1 — only the bare {@code create(Level)} and a 7-arg full-context one (VERIFIED
 *     EntityType.java:402,524). Every call site here already calls {@code .setPos(...)} manually
 *     afterward, so {@code create(Level)} is the correct substitute. {@code Mob#finalizeSpawn} gains
 *     a 5th {@code @Nullable CompoundTag} parameter on 1.20.1 (VERIFIED Mob.java:1064) — passed
 *     {@code null} at every call site here, same as the {@code SpawnGroupData} before it.</li>
 * <li>{@code Raid.getOminousBannerInstance(RegistryAccess)} does not exist; 1.20.1's equivalent is
 *     the parameterless {@code Raid.getLeaderBannerInstance()} (VERIFIED Raid.java:608).</li>
 * <li>Loot tables are plain {@code ResourceLocation}s on 1.20.1, not registry-backed
 *     {@code ResourceKey<LootTable>} (VERIFIED: {@code Registries.LOOT_TABLE} does not exist; vanilla's
 *     own {@code ChestBoat} stores a bare {@code ResourceLocation} field). Note for whoever owns
 *     {@code BigBoat}: {@code BigBoat.setContainerLootTable} should take a {@code ResourceLocation},
 *     not a {@code ResourceKey<LootTable>}, on this port.</li>
 * </ul>
 */
@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 5, ordinal = 0))
    private int moreCommon(int constant) {
        return 1;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;ceil(D)D"), cancellable = true)
    private void addMeleeGoalForSpear(ServerLevel level, boolean spawnFriendlies, boolean spawnEnemies, CallbackInfoReturnable<Integer> cir, @Local Player player, @Local BlockPos.MutableBlockPos spawnPos) {
        RandomSource random = level.getRandom();
        Holder<Biome> registryEntry = level.getBiome(spawnPos);
        if (registryEntry.is(BiomeTags.IS_OCEAN)) {
            if (random.nextInt(3) == 0) {
                if (notNearOtherPatrols(level, player.blockPosition())) {
                    spawnPos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, spawnPos).getY());
                    if (level.getBlockState(spawnPos).is(Blocks.AIR) && level.getBlockState(spawnPos.below()).is(Blocks.WATER)) {
                        for (int bx = spawnPos.getX() - 8; bx < spawnPos.getX() + 8; bx++) {
                            for (int by = spawnPos.getY() - 2; by < spawnPos.getY() + 4; by++) {
                                for (int bz = spawnPos.getZ() - 8; bz < spawnPos.getZ() + 8; bz++) {
                                    BlockState blockState = level.getBlockState(new BlockPos(bx, by, bz));
                                    if (!(blockState.is(Blocks.AIR) || blockState.is(Blocks.WATER))) {
                                        cir.setReturnValue(0);
                                        return;
                                    }
                                }
                            }
                        }
                        spawnPos.offset(0, 2, 0);
                        int n = (int) Math.ceil(level.getCurrentDifficultyAt(spawnPos).getEffectiveDifficulty()) + 1;
                        int boatType = random.nextInt(EntityTypeRegistry.bigBoats.size());
                        for (int o = 0; o < n; o++) {
                            if (o == 0) {
                                if (!this.spawnBoat(level, spawnPos, random, boatType, true))  break;
                            } else {
                                this.spawnBoat(level, spawnPos, random, boatType, false);
                            }
                            spawnPos.setX(spawnPos.getX() + (3 + random.nextInt(3)) * (int) Math.signum(random.nextInt(2) - 0.5));
                            spawnPos.setY(spawnPos.getY() + 2);
                            spawnPos.setZ(spawnPos.getZ() + (3 + random.nextInt(3)) * (int) Math.signum(random.nextInt(2) - 0.5));
                        }
                    }
                }
            }
            cir.setReturnValue(0);
        } else if (random.nextInt(5) != 0) cir.setReturnValue(0);
    }

    @Unique private boolean notNearOtherPatrols(ServerLevel level, BlockPos blockPos) {
        return level.getEntitiesOfClass(Raider.class, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(blockPos)).inflate(100), EntitySelector.LIVING_ENTITY_STILL_ALIVE).isEmpty();
    }

    @Unique private boolean spawnBoat(ServerLevel level, BlockPos pos, RandomSource random, int boatType, boolean captain) {
        BlockState blockState = level.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockState, blockState.getFluidState(), EntityType.PILLAGER)) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, level, MobSpawnType.PATROL, pos, random)) {
            return false;
        } else {
            if (captain) return spawnCaptainBoat(level, pos, random, boatType);
            else return spawnSmallBoat(level, pos, random, boatType);
        }
    }

    @Unique boolean spawnCaptainBoat(ServerLevel level, BlockPos pos, RandomSource random, int boatType){
        BigBoat bigBoat = level.getDifficulty().getId()>2?
                EntityTypeRegistry.hugeBoats.get(boatType).create(level):
                EntityTypeRegistry.bigBoats.get(boatType).create(level);
        if (bigBoat == null) return false;
        bigBoat.setBanner(Raid.getLeaderBannerInstance());
        bigBoat.setHasChest(true);
        bigBoat.setContainerLootTable(NekomasFixed.id("chests/patrol_boat"));
        bigBoat.setContainerLootTableSeed(random.nextLong());
        bigBoat.setPos(Vec3.atCenterOf(pos));
        for (int i = 0; i < level.getDifficulty().getId(); i++) {
            PatrollingMonster patrolEntity = (PatrollingMonster) EntityType.PILLAGER.create(level);
            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null, null);
            patrolEntity.startRiding(bigBoat);
        }
         level.addFreshEntityWithPassengers(bigBoat);

        return true;
    }

    @Unique boolean spawnSmallBoat(ServerLevel level, BlockPos pos, RandomSource random, int boatType){
        Boat boatEntity = EntityTypeRegistry.boats.get(boatType).create(level);
        if (boatEntity != null) {
            boatEntity.setPos(Vec3.atCenterOf(pos));
            PatrollingMonster patrolEntity = (PatrollingMonster) EntityType.PILLAGER.create(level);
            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null, null);
            patrolEntity.startRiding(boatEntity);
            level.addFreshEntityWithPassengers(boatEntity);
            return true;
        } else return false;
    }
}
