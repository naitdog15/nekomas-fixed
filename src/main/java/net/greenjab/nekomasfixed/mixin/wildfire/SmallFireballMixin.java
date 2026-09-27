package net.greenjab.nekomasfixed.mixin.wildfire;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.SmallFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// A Wildfire's fireballs hit harder than a blaze's, and harder again once it is soul-lit.
@Mixin(SmallFireball.class)
public class SmallFireballMixin {

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float wildFireProjectileDamage(float amount, @Local(ordinal = 1) Entity entity) {
        if (entity instanceof WildfireEntity wildFireEntity) {
            return amount + (wildFireEntity.isSoulActive() ? 3.0F : 2.0F);
        }
        return amount;
    }
}
