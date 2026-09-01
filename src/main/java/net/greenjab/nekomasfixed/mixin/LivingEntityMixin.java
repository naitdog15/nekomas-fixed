package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.item.WildfireShieldItem;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * One of the widest single retargets in the port.
 * <p>
 * 26.2's damage pipeline is {@code hurtServer(ServerLevel, DamageSource, float)} plus two extracted
 * helpers, {@code blockUsingItem(...)} (fires when a shield blocks a hit from a living attacker) and
 * {@code resolveMobResponsibleForDamage(...)} (attributes the hit for anger/stat tracking). None of
 * the three exist on 1.20.1 (VERIFIED: zero matches for any of the three names anywhere in
 * forge-1.20.1-mapped-src's LivingEntity.java). 1.20.1 has one monolithic
 * {@code hurt(DamageSource, float)} (LivingEntity.java:1058) with all of that logic inlined — every
 * injector below is retargeted onto the closest equivalent point inside it, not a like-for-like
 * method rename. Budget extra testing time on this file — every anchor here was moved, not verified
 * unchanged.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract void stopRiding();

    // isSleeping() is called at the exact same early point in 1.20.1's hurt() (VERIFIED
    // LivingEntity.java:1069) as it was in hurtServer(), so this @ModifyVariable's anchor survives
    // unchanged; only the target method name and the dropped ServerLevel parameter change.
    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), ordinal = 0, argsOnly = true)
    private float turtleChestplateBlock(float damage, @Local(argsOnly = true) DamageSource source) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE.getItemBySlot(EquipmentSlot.CHEST).is(ItemRegistry.TURTLE_CHESTPLATE.get())) {
            Vec3 vec3d = source.getSourcePosition();
            double d;
            if (vec3d != null) {
                Vec3 vec3d2 = LE.calculateViewVector(0.0F, LE.getYHeadRot());
                Vec3 vec3d3 = vec3d.subtract(LE.position());
                vec3d3 = new Vec3(vec3d3.x, 0.0, vec3d3.z).normalize();
                d = Math.acos(vec3d3.dot(vec3d2));
            } else {
                d = 0;
            }

            float f = getReductionAmount(LE, damage, d);
            if (f > 0.0F && source.getDirectEntity() instanceof LivingEntity) {
                LE.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak((f == damage ? 3 : 1), LE, EquipmentSlot.CHEST);
            }
            if (damage - f <= 0) return 0.00123f;
            return damage - f;
        }
        // NAMED GAP (not a feature cut — see class header and PlayerMixin's matching note): the Mace
        // and DamageTypes.MACE_SMASH are 1.21+ (VERIFIED: zero matches in forge-1.20.1-mapped-src
        // DamageTypes.java), so this branch is permanently unreachable until this mod or the game
        // ships a mace-like weapon. Kept structurally (rather than deleted) because it shares this
        // method with the chestplate-blocking logic above, which is fully live.
        if (LE.getItemBySlot(EquipmentSlot.HEAD).is(Items.TURTLE_HELMET)) {
            if (false) {
                LE.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak((int) damage, LE, EquipmentSlot.CHEST);
                return 0.00123f;
            }
        }

        return damage;
    }

    // 1.20.1 has one unified travel(Vec3) covering air/water/lava (no travelInWater split — VERIFIED
    // LivingEntity.java:2017); getFluidFallingAdjustedMovement is still called from inside it at the
    // identical call shape.
    @WrapOperation(method = "travel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 noFallInWaterWithTurtleBoots(LivingEntity instance, double baseGravity, boolean isFalling, Vec3 movement,
                                              Operation<Vec3> original) {
        if (instance.isUnderWater()&&instance.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.TURTLE_BOOTS.get())) baseGravity = 0.0;
        return original.call(instance, baseGravity, isFalling, movement);
    }

    // Retargeted from the no-longer-existing blockUsingItem(ServerLevel, LivingEntity, DamageSource,
    // float) onto protected void blockUsingShield(LivingEntity attacker) (VERIFIED
    // LivingEntity.java:1209) — the closest 1.20.1 equivalent: it fires exactly when hurt() has
    // already established the hit was shield-blocked and the attacker is a LivingEntity (the same
    // two preconditions blockUsingItem's caller enforced). knockback() has only the 3-double overload
    // on 1.20.1 (no DamageSource/float variant), and igniteForTicks(int ticks) is
    // setSecondsOnFire(int seconds) here.
    @Inject(method = "blockUsingShield", at = @At("HEAD"))
    private void onShieldHit(LivingEntity attacker, CallbackInfo ci) {
        LivingEntity defender = (LivingEntity)(Object)this;
        ItemStack activeItem = defender.getUseItem();

        if (activeItem.getItem() instanceof WildfireShieldItem) {
            if (defender instanceof Player player) {
                if (player.getHealth() <= 6.0f) {
                    attacker.setSecondsOnFire(3);
                    attacker.knockback(1.0,
                            attacker.getX() + player.getX(),
                            attacker.getZ() + player.getZ());
                } else {
                    attacker.setSecondsOnFire(1);
                }
            }

        }
    }

    // Retargeted from an @At("INVOKE") on the no-longer-existing getUseItem() call inside hurtServer
    // onto isDamageSourceBlocked(DamageSource)'s INVOKE inside hurt() — textually after the
    // isSleeping() point above (so it observes turtleChestplateBlock's already-modified damage) and
    // before any of hurt()'s consequential side effects (shield block, knockback, actuallyHurt).
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"), cancellable = true)
    private void cancel0Damage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (damage == 0.00123f) cir.setReturnValue(true);
    }

    // Retargeted from an @At("INVOKE") on the no-longer-existing resolveMobResponsibleForDamage(...)
    // call: on 1.20.1 that attribution logic is inlined directly after actuallyHurt(...) inside
    // hurt() (VERIFIED LivingEntity.java:1106-1147), so this fires right after either of that
    // method's two call sites (the invulnerability-cooldown branch and the normal branch) return.
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER))
    private void leechingEnchant(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player PE) {
            int i = NekomasFixed.enchantLevel(PE.getMainHandItem(), "leeching");
            if (i != 0) PE.heal((i * 0.0125f + 0.0125f) * damage);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void dismountEnchant(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (source.getEntity() instanceof Player PE) {
            int i = NekomasFixed.enchantLevel(PE.getMainHandItem(), "dismount");
            if(source.getWeaponItem()!=null && !source.getWeaponItem().isEmpty() && i==1){
                this.stopRiding();
                entity.getPassengers().forEach(Entity::stopRiding);
            }
        }
    }

    @Unique
    public float getReductionAmount(LivingEntity LE, float damage, double angle) {
        if (angle > (float) (Math.PI / 180.0) * 90f) {
            if (LE instanceof Player player && !player.isShiftKeyDown()) return damage/2f;
            return damage;
        } else {
            return 0.0F;
        }
    }
}
