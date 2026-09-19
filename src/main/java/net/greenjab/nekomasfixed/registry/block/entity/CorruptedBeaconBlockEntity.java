package net.greenjab.nekomasfixed.registry.block.entity;

import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class CorruptedBeaconBlockEntity extends BlockEntity {
    // magenta but lighter - the beam texture darkens it. the renderer tints with floats, this is 0xFF50FF
    public static final float[] BEAM_COLOR = {1.0F, 80.0F / 255.0F, 1.0F};

    // a beacon works this out as levels * 10 + 10, so this is the same as a level 2 one
    public static final double RANGE = 30;
    // shorter than a beacon's, so the effects don't hang around long once it is broken
    public static final int DURATION_TICKS = 100;

    private static final List<MobEffect> EFFECTS = List.of(MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST, MobEffects.ABSORPTION);
    private static final int EFFECT_LEVEL = 1;

    public static final Predicate<LivingEntity> ENTITY_FILTER = entity -> entity.getType().getCategory() == MobCategory.MONSTER;

    public CorruptedBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.CORRUPTED_BEACON_BLOCK_ENTITY.get(), pos, state);
    }

    // the beam runs up to the build limit, so the box the renderer culls against can't be the block's own
    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CorruptedBeaconBlockEntity blockEntity) {
        // every 4 seconds, same as a beacon
        if (level.getGameTime() % 80L == 0L) {
            applyEffects(level, pos);
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.7F);
            level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.2F);
        }
    }

    private static void applyEffects(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            // RANGE all round and then up to the build height, so it covers RANGE blocks below the block too
            AABB area = (new AABB(pos)).inflate(RANGE).expandTowards(0D, level.getHeight(), 0D);

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, ENTITY_FILTER);

            for (LivingEntity entity : entities) {
                for (MobEffect effect : EFFECTS) {
                    entity.addEffect(new MobEffectInstance(effect, DURATION_TICKS, EFFECT_LEVEL, false, true));
                }
                spawnParticleRay(serverLevel, pos, entity);
            }
        }
    }

    private static void spawnParticleRay(ServerLevel level, BlockPos pos, Entity target) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 relative = target.position().subtract(center).add(0, target.getBbHeight() / 2, 0);
        double distance = relative.length();
        if (distance < 1.0E-4) return;
        Vec3 direction = relative.scale(1 / distance);

        double density = 3; // particles per block
        for (int i = 0; i < (int) distance * density; i++) {
            // thins out with distance from the entity, quadratically
            if (level.getRandom().nextInt(i * i + 1) > 30) continue;
            level.sendParticles(ParticleTypes.GLOW,
                    -direction.x * i / density + target.getX(), // reversed, so i = 0 is on the entity
                    -direction.y * i / density + target.getY() + target.getBbHeight() / 2,
                    -direction.z * i / density + target.getZ(),
                    1, 0, 0, 0, 0);
        }
    }
}
