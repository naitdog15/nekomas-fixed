package net.greenjab.nekomasfixed.mixin.target_dummy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// enchantment damage bonus is worked out from mob type alone (it never sees the entity itself), so
// the undead flag for a zombie-dressed dummy has to be answered here.
@Mixin(LivingEntity.class)
public class LivingEntityMobTypeMixin {

    @ModifyReturnValue(method = "getMobType", at = @At("RETURN"))
    private MobType dummyCountsAsUndead(MobType original) {
        if (!NekomasFixedConfig.TARGET_DUMMY_COUNTS_AS_UNDEAD.get()) return original;
        if ((Object) this instanceof TargetDummy dummy && dummy.isZombie()) return MobType.UNDEAD;
        return original;
    }
}
