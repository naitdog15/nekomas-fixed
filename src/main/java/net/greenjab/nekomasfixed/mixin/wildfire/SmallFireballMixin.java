package net.greenjab.nekomasfixed.mixin.wildfire;

import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SmallFireball has no .hurtingprojectile subpackage on 1.20.1. hurtServer(ServerLevel,...) -> hurt
// (see the top-level LivingEntityMixin's header) — Entity#hurt(DamageSource,float) still returns
// boolean here. EnchantmentHelper.doPostAttackEffects does not exist; doPostDamageEffects(LivingEntity
// attacker, Entity victim) is 1.20.1's matching post-attack hook (VERIFIED
// forge-1.20.1-mapped-src EnchantmentHelper.java:192).
@Mixin(SmallFireball.class)
public class SmallFireballMixin {

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/SmallFireball;damageSources()Lnet/minecraft/world/damagesource/DamageSources;"))
    private void wildFireProjectileDamage(EntityHitResult hitResult, CallbackInfo ci) {
        SmallFireball SFE = (SmallFireball)(Object)this;
        LivingEntity ownerEntity = (SFE.getOwner() instanceof LivingEntity livingEntity) ? livingEntity:null;
        if (ownerEntity instanceof WildfireEntity wildFireEntity) {
            Entity hitEntity = hitResult.getEntity();
            DamageSource damageSource = SFE.damageSources().mobProjectile(SFE, ownerEntity);
            if (SFE.level() instanceof ServerLevel serverWorld && hitEntity.hurt(damageSource, wildFireEntity.isSoulActive()?3.0F:2.0F)) {
                EnchantmentHelper.doPostDamageEffects(ownerEntity, hitEntity);
            }
        }
    }
}
