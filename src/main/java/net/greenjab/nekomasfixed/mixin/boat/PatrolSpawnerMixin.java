package net.greenjab.nekomasfixed.mixin.boat;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.BigBoat;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
 * Turns the vanilla pillager patrol roll into a pirate patrol when it lands on an ocean biome: a
 * captain's big boat (a huge one on hard) with a banner and a chest, escorted by a few crewed boats.
 *
 * <p>The injector rides the {@code Math.ceil} the vanilla spawner uses to size a patrol, since both
 * the chosen player and the candidate position are already settled there. Cancelling has to hand
 * back a count, and 0 is right: these boats are added directly rather than through vanilla's counted
 * budget.
 *
 * <p>The captain's chest is seeded from {@code nekomasfixed:chests/patrol_boat} and only rolled when
 * a player actually opens it, which is how every other loot-table container works.
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
                        int boatType = random.nextInt(EntityTypeRegistry.bigBoats().size());
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
                EntityTypeRegistry.hugeBoats().get(boatType).get().create(level):
                EntityTypeRegistry.bigBoats().get(boatType).get().create(level);
        if (bigBoat == null) return false;
        bigBoat.setBanner(Raid.getLeaderBannerInstance());
        bigBoat.setHasChest(true);
        bigBoat.setLootTable(NekomasFixed.id("chests/patrol_boat"));
        bigBoat.setLootTableSeed(random.nextLong());
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
        Boat boatEntity = EntityTypeRegistry.vanillaBoats().get(boatType).create(level);
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
