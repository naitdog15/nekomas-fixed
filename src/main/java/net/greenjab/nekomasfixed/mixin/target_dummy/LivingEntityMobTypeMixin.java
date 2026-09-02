package net.greenjab.nekomasfixed.mixin.target_dummy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * A target dummy dressed up as a zombie counts as undead, so Smite bites into it exactly as hard as
 * it would into the mob the dummy is standing in for.
 *
 * <p>An enchantment's damage bonus is worked out from the target's mob type alone - the enchantment
 * never sees the entity it is hitting - so the flag has to be answered here, at the one point that
 * does.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMobTypeMixin {

    @ModifyReturnValue(method = "getMobType", at = @At("RETURN"))
    private MobType dummyCountsAsUndead(MobType original) {
        if (!NekomasFixedConfig.TARGET_DUMMY_COUNTS_AS_UNDEAD.get()) return original;
        if ((Object) this instanceof TargetDummy dummy && dummy.isZombie()) return MobType.UNDEAD;
        return original;
    }
}
