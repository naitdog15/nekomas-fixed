package net.greenjab.nekomasfixed.mixin.target_dummy;

import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.mixins.json still names {@code
 * target_dummy.ServerExplosionMixin}).
 * <p>
 * {@code ServerExplosion} does not exist on 1.20.1 — there is one {@code Explosion} class with no
 * client/server split (matches the copy-count-independent pattern of every other split-class delta
 * in this port). More importantly, neither the target method ({@code hurtEntities}) nor the wrapped
 * call ({@code ExplosionDamageCalculator#getKnockbackMultiplier(Entity)}) exist on 1.20.1's
 * {@code Explosion}/{@code ExplosionDamageCalculator} at all (VERIFIED: zero matches for either name
 * in forge-1.20.1-mapped-src) — entity damage/knockback there is computed inline inside
 * {@code Explosion#explode()} with no per-entity multiplier hook to intercept. Making TargetDummy
 * immune to explosion knockback on 1.20.1 needs a different mechanism entirely (e.g. overriding
 * {@code Entity#isPushable()}/knockback application on {@code TargetDummy} itself) — out of scope
 * here.
 */
@Mixin(Explosion.class)
public class ServerExplosionMixin {
}
