package net.greenjab.nekomasfixed.mixin.wildfire;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// second phase piggybacks on the totem check: killing it once only strips its plates and it comes
// back soul-lit at full health, keeping the death from resolving. (byte) 35 is the totem's own
// client-side flash, which is exactly the effect wanted here.
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean wildFireSecondPhase(boolean original, @Local(argsOnly = true) DamageSource source) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof WildfireEntity wildFireEntity) {
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                if (!wildFireEntity.isSoulActive()) {
                    wildFireEntity.setSoulActive(true);
                    wildFireEntity.setShieldsActive(4);
                    wildFireEntity.setHealth(wildFireEntity.getMaxHealth());
                    wildFireEntity.level().broadcastEntityEvent(wildFireEntity, (byte) 35);
                    return true;
                }
            }
        }
        return original;
    }
}
