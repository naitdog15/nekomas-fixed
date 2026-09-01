package net.greenjab.nekomasfixed.mixin.wildfire;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Snowball has no .throwableitemprojectile subpackage on 1.20.1. Entity#hurt(DamageSource,float)
// returns boolean here (not void — see the top-level LivingEntityMixin's header), so the @At
// descriptor's trailing return type changes; the wrapped argument index (1, the float amount) is
// unaffected by that.
@Mixin(Snowball.class)
public class SnowballMixin {
    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float snowballWildfireAttack(float amount, @Local Entity entity) {
        if (entity instanceof WildfireEntity) return 3;
        return amount;
    }
}
