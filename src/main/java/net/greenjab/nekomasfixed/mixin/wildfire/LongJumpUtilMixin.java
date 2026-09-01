package net.greenjab.nekomasfixed.mixin.wildfire;

import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.mixins.json still names {@code
 * wildfire.LongJumpUtilMixin}).
 * <p>
 * {@code net.minecraft.world.entity.ai.behavior.LongJumpUtil} does not exist on 1.20.1 (VERIFIED:
 * zero matches) — there is no shared static jump-vector helper; the long-jump behaviours are three
 * separate classes ({@code LongJumpToRandomPos}, {@code LongJumpToPreferredBlock}, {@code
 * LongJumpMidJump}) that each compute their own jump vector inline, and none call
 * {@code Mob#getGravity()} the way this mixin's one anchor needs (VERIFIED: zero matches for either
 * name across all three). The Wildfire glide-jump projectile-gravity override this mixin implements
 * has no single retarget point on 1.20.1 without redesigning which of the three behaviour classes
 * Wildfire's own AI actually uses — that is a broader Wildfire AI/goal-wiring concern, not a mixin
 * retarget.
 */
@Mixin(LongJumpToRandomPos.class)
public class LongJumpUtilMixin {
}
