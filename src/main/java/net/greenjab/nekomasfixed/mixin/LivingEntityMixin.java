package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.CompatMods;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.item.WildfireShieldItem;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Everything this mod adds to the damage pipeline: turtle-chestplate directional blocking, the
 * turtle helmet's mace soak, the turtle boots' underwater fall behaviour, the wildfire shield's
 * retaliation, and the leeching and dismount enchantments.
 *
 * <p>{@code hurt(DamageSource, float)} is one monolithic method, so the injectors below anchor on
 * distinct calls inside it rather than on separate helper methods - order between them matters, and
 * each anchor is noted where it is not obvious.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract void stopRiding();

    // anchored on the isSleeping() check near the top of hurt(), before anything acts on the damage
    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), ordinal = 0, argsOnly = true)
    private float turtleChestplateBlock(float damage, @Local(argsOnly = true) DamageSource source) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get() && LE.getItemBySlot(EquipmentSlot.CHEST).is(ItemRegistry.TURTLE_CHESTPLATE.get())) {
            Vec3 vec3d = source.getSourcePosition();
            double d;
            if (vec3d != null) {
                // Entity#calculateViewVector is protected; the static form gives the same vector
                Vec3 vec3d2 = Vec3.directionFromRotation(0.0F, LE.getYHeadRot());
                Vec3 vec3d3 = vec3d.subtract(LE.position());
                vec3d3 = new Vec3(vec3d3.x, 0.0, vec3d3.z).normalize();
                d = Math.acos(vec3d3.dot(vec3d2));
            } else {
                d = 0;
            }

            float f = getReductionAmount(LE, damage, d);
            if (f > 0.0F && source.getDirectEntity() instanceof LivingEntity) {
                LE.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak((f == damage ? 3 : 1), LE, holder -> holder.broadcastBreakEvent(EquipmentSlot.CHEST));
            }
            if (damage - f <= 0) return 0.00123f;
            return damage - f;
        }
        // the turtle helmet soaks a mace's smash outright, and takes the whole blow in durability
        if (NekomasFixedConfig.MACE_INTERACTIONS.get()
                && LE.getItemBySlot(EquipmentSlot.HEAD).is(Items.TURTLE_HELMET)
                && isMaceSmash(source)) {
            LE.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak((int) damage, LE,
                    holder -> holder.broadcastBreakEvent(EquipmentSlot.HEAD));
            return 0.00123f;
        }

        return damage;
    }

    /**
     * Whether a hit is a falling mace blow. A mace deals ordinary attack damage and raises no damage
     * source of its own, so there is nothing on the hit itself to recognise; what makes a smash a
     * smash is read off the swing instead - a mace in the attacker's hand and a drop behind it. The
     * weapon is matched by id, which is also the presence check: with the mace's mod absent no item
     * carries that id and this is simply never true.
     */
    @Unique
    private static boolean isMaceSmash(DamageSource source) {
        if (!(source.getDirectEntity() instanceof LivingEntity attacker) || attacker.fallDistance <= 1.5F) {
            return false;
        }
        ResourceLocation weapon = ForgeRegistries.ITEMS.getKey(attacker.getMainHandItem().getItem());
        return weapon != null
                && weapon.getNamespace().equals(CompatMods.NEW_TRIALS)
                && weapon.getPath().equals("mace");
    }

    // travel(Vec3) covers air, water and lava in one method here, and still routes falling motion
    // through getFluidFallingAdjustedMovement
    @WrapOperation(method = "travel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 noFallInWaterWithTurtleBoots(LivingEntity instance, double baseGravity, boolean isFalling, Vec3 movement,
                                              Operation<Vec3> original) {
        if (NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get() && instance.isUnderWater()&&instance.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.TURTLE_BOOTS.get())) baseGravity = 0.0;
        return original.call(instance, baseGravity, isFalling, movement);
    }

    // blockUsingShield only runs once hurt() has established the hit was blocked and the attacker is
    // a living entity, which is exactly when the wildfire shield should bite back
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

    // sits after the isSleeping() anchor above (so it sees the already-reduced damage) and before
    // hurt() acts on it - the sentinel means the chestplate absorbed the whole hit
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"), cancellable = true)
    private void cancel0Damage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (damage == 0.00123f) cir.setReturnValue(true);
    }

    // fires once the hit has actually landed - hurt() calls actuallyHurt from both the
    // invulnerability-cooldown branch and the normal one, and leeching should heal off either
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER))
    private void leechingEnchant(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!NekomasFixedConfig.LEECHING_ENCHANTMENT.get()) return;
        if (source.getEntity() instanceof Player PE) {
            int i = NekomasFixed.enchantLevel(PE.getMainHandItem(), "leeching");
            if (i != 0) PE.heal((i * 0.0125f + 0.0125f) * damage);
        }
    }

    // shares the isSleeping() anchor with the chestplate rewrite above rather than sitting at the
    // head of hurt(): everything before that point still runs on the client and still runs for hits
    // the game is about to throw away, and yanking a rider off a mount is not something to do on a
    // hit that never happened
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
    private void dismountEnchant(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!NekomasFixedConfig.DISMOUNT_ENCHANTMENT.get()) return;
        LivingEntity entity = (LivingEntity)(Object)this;
        if (source.getEntity() instanceof Player PE) {
            // no weapon stack hangs off the damage source here - the hand that swung is the weapon
            ItemStack weapon = PE.getMainHandItem();
            int i = NekomasFixed.enchantLevel(weapon, "dismount");
            if(!weapon.isEmpty() && i==1){
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
