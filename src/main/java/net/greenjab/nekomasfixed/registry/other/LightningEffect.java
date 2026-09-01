package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;

/**
 * 1.20.1 has no separate {@code InstantaneousMobEffect} class (that split is 26.x-only) — a
 * one-shot effect is a plain {@link MobEffect} overriding {@link #isInstantenous()} to return
 * {@code true} (note Mojang's own 1.20.1 spelling, missing the second "a") plus {@link
 * #applyInstantenousEffect}. {@code EntityType.LIGHTNING_BOLT.create(Level)} takes no spawn-reason
 * argument on 1.20.1 ({@code EntitySpawnReason} is 26.x-only).
 */
public class LightningEffect extends MobEffect {
    public LightningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        strike(entity);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity target, int amplifier, double proximity) {
        strike(target);
    }

    private static void strike(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.canSeeSky(target.blockPosition())) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(target.getX(), target.getY(), target.getZ());
                serverLevel.addFreshEntity(lightning);
            }
        }
    }
}
