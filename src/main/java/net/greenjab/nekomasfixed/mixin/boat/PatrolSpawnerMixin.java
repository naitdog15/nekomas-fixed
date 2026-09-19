package net.greenjab.nekomasfixed.mixin.boat;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.BigBoatEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 5, ordinal = 0))
    private int moreCommon(int constant) {
        return 1;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;ceil(D)D"), cancellable = true)
    private void addMeleeGoalForSpear(CallbackInfo ci, @Local(argsOnly = true)ServerLevel world, @Local Player playerEntity, @Local BlockPos.MutableBlockPos mutable) {
        Holder<Biome> registryEntry = world.getBiome(mutable);
        RandomSource random = world.random;
        if (registryEntry.is(BiomeTags.IS_OCEAN)) {
            if (random.nextInt(3) == 0) {
                if (notNearOtherPatrols(world, playerEntity.blockPosition())) {
                    mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, mutable).getY());
                    if (world.getBlockState(mutable).is(Blocks.AIR) && world.getBlockState(mutable.below()).is(Blocks.WATER)) {
                        for (int bx = mutable.getX() - 8; bx < mutable.getX() + 8; bx++) {
                            for (int by = mutable.getY() - 2; by < mutable.getY() + 4; by++) {
                                for (int bz = mutable.getZ() - 8; bz < mutable.getZ() + 8; bz++) {
                                    BlockState blockState = world.getBlockState(new BlockPos(bx, by, bz));
                                    if (!(blockState.is(Blocks.AIR) || blockState.is(Blocks.WATER))) {
                                        ci.cancel();
                                        return;
                                    }
                                }
                            }
                        }
                        mutable.offset(0, 2, 0);
                        int n = (int) Math.ceil(world.getCurrentDifficultyAt(mutable).getEffectiveDifficulty()) + 1;

                        int boatType = random.nextInt(EntityTypeRegistry.bigBoats.size());
                        for (int o = 0; o < n; o++) {
                            if (o == 0) {
                                if (!this.spawnBoat(world, mutable, random, boatType, true)) {
                                    break;
                                }
                            } else {
                                this.spawnBoat(world, mutable, random, boatType, false);
                            }

                            mutable.setX(mutable.getX() + (3 + random.nextInt(3)) * (int) Math.signum(random.nextInt(2) - 0.5));
                            mutable.setY(mutable.getY() + 2);
                            mutable.setZ(mutable.getZ() + (3 + random.nextInt(3)) * (int) Math.signum(random.nextInt(2) - 0.5));
                        }
                    }
                }
                ci.cancel();
            }
        } else if (random.nextInt(5) != 0) ci.cancel();
    }

    @Unique private boolean notNearOtherPatrols(ServerLevel world, BlockPos blockPos) {
        return world.getEntitiesOfClass(Raider.class, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(blockPos)).inflate(100), EntitySelector.LIVING_ENTITY_STILL_ALIVE).isEmpty();
    }

    @Unique private boolean spawnBoat(ServerLevel world, BlockPos pos, RandomSource random, int boatType, boolean captain) {
        BlockState blockState = world.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), EntityType.PILLAGER)) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, world, EntitySpawnReason.PATROL, pos, random)) {
            return false;
        } else {
            if (captain) {
                return spawnCaptainBoat(world, pos, random, boatType);
            } else {
                return spawnSmallBoat(world, pos, random, boatType);
            }
        }
    }

    @Unique boolean spawnCaptainBoat(ServerLevel world, BlockPos pos, RandomSource random, int boatType){
        BigBoatEntity bigBoatEntity = world.getDifficulty().getId()>2?
                EntityTypeRegistry.hugeBoats.get(boatType).create(world, EntitySpawnReason.PATROL):
                EntityTypeRegistry.bigBoats.get(boatType).create(world, EntitySpawnReason.PATROL);

        if (bigBoatEntity != null) {
            bigBoatEntity.setBanner(Raid.getOminousBannerInstance(world.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN)));
            bigBoatEntity.setHasChest(true);
            bigBoatEntity.setContainerLootTable(ResourceKey.create(Registries.LOOT_TABLE, NekomasFixed.id("chests/patrol_boat")));
            bigBoatEntity.setContainerLootTableSeed(random.nextLong());
            bigBoatEntity.snapTo(pos.getCenter());
            for (int i = 0; i < world.getDifficulty().getId();i++) {
                PatrollingMonster patrolEntity = EntityType.PILLAGER.create(world, EntitySpawnReason.PATROL);
                patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
                patrolEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.PATROL, null);
                patrolEntity.startRiding(bigBoatEntity);
            }

            world.addFreshEntityWithPassengers(bigBoatEntity);
            return true;
        } else {
            return false;
        }
    }

    @Unique boolean spawnSmallBoat(ServerLevel world, BlockPos pos, RandomSource random, int boatType){
        AbstractBoat boatEntity = EntityTypeRegistry.boats.get(boatType).create(world, EntitySpawnReason.PATROL);
        if (boatEntity != null) {
            boatEntity.snapTo(pos.getCenter());
            PatrollingMonster patrolEntity = EntityType.PILLAGER.create(world, EntitySpawnReason.PATROL);
            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.PATROL, null);
            patrolEntity.startRiding(boatEntity);
            world.addFreshEntityWithPassengers(boatEntity);
            return true;
        } else {
            return false;
        }
    }
}