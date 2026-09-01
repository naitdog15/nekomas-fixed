package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.other.ComboComponent;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.screen.config.ModConfigValues;
import net.greenjab.nekomasfixed.util.ModData;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * See LivingEntityMixin's header for the shared 26.2-vs-1.20.1 damage-pipeline background.
 * Player-specific findings (all VERIFIED against forge-1.20.1-mapped-src Player.java):
 * <ul>
 * <li>Unlike LivingEntity, {@code Player} DOES override {@code hurt(DamageSource,float):boolean}
 *     directly (Player.java:811) — difficulty scaling, then {@code super.hurt(...)} into
 *     LivingEntity's own (mixed-into) {@code hurt}. So player-specific injectors below retarget onto
 *     {@code Player#hurt}, while the entity-generic ones LivingEntityMixin already installs on
 *     {@code LivingEntity#hurt} still fire for players too, via that {@code super} call — no
 *     duplication needed or possible.</li>
 * <li>{@code getDestroySpeed(BlockState)} is a two-line delegator to {@code getDigSpeed(BlockState,
 *     BlockPos)} (Player.java:675-679); the {@code onGround()} check the mixin needs lives inside
 *     {@code getDigSpeed} (Player.java:717), not {@code getDestroySpeed} itself.</li>
 * <li>{@code Item#getAttackDamageBonus(Entity,float,DamageSource)} and
 *     {@code baseDamageScaleFactor} do not exist on 1.20.1 (VERIFIED: zero matches). The combo-damage
 *     hook is retargeted onto {@code this.getAttributeValue(Attributes.ATTACK_DAMAGE)} — the first
 *     {@code getAttributeValue} call inside {@code attack(Entity)} (Player.java:1092), representing
 *     the same pre-attack-strength-scaling base damage the original captured as a local — and the
 *     off-hand-sickle full-damage override is retargeted onto {@code getAttackStrengthScale(float)}
 *     (Player.java:1941), which is what {@code attack(Entity)} uses in its place (line 1100).</li>
 * <li>{@code Entity#hurtOrSimulate} does not exist; {@code attack(Entity)} deals damage via the plain
 *     {@code target.hurt(DamageSource,float):boolean} (Player.java:1142).</li>
 * <li>{@code DamageTypes.MACE_SMASH} does not exist on 1.20.1 (the Mace is a 1.21+ item — VERIFIED:
 *     zero matches in DamageTypes.java). NAMED GAP, not a feature cut: the injection point survives
 *     (Player#hurt does call removeEntitiesOnShoulder(), matching the original anchor exactly), but
 *     the condition it guards can never be true until this mod ships its own mace-like weapon or the
 *     game does. See LivingEntityMixin's matching helmet-branch note.</li>
 * </ul>
 */
@Mixin(Player.class)
public class PlayerMixin {

    @Unique
    private void checkForEdibles(Player PE){
        if (PE.level().getRandom().nextInt(15*20) > 0) return;
        Random random = new Random();
        Container inventory = PE.getInventory();
        int i = random.nextInt(inventory.getContainerSize());
        ItemStack food = inventory.getItem(i);
        if (!food.isEmpty() && food.is(ModTags.FOOD_ITEMS)) {
            food.shrink(1);
            ItemStack rotten = new ItemStack(Items.ROTTEN_FLESH, 1);
            if (!PE.getInventory().add(rotten.copy())) {
                PE.drop(rotten, false);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private void customTickLogics(CallbackInfo ci) {
        Player PE = (Player)(Object)this;

        if (PE.onGround() && !PE.isInWater()) {
            if (PE.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.TURTLE_BOOTS.get())) {
                PE.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE, 200, 0, false, false, true));
            }
        }
        if (PE.level().getBiome(PE.blockPosition()).is(BiomeTags.IS_NETHER)) {
            if (!PE.isCreative()&&!PE.isSpectator() && ModConfigValues.netherFoodRotting){
                this.checkForEdibles(PE);
            }
        }
        if (ModData.combos.containsKey(PE.getUUID())){
            int comboTimer = ModData.combos.get(PE.getUUID())-1;
            if (comboTimer<=0) ModData.combos.remove(PE.getUUID());
            else ModData.combos.put(PE.getUUID(), comboTimer);
        }
    }

    @ModifyExpressionValue(method = "getDigSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
    private boolean turtleLeggingsMining(boolean original) {
        Player PE = (Player)(Object)this;
        if (PE.isEyeInFluid(FluidTags.WATER)) {
            if (PE.getItemBySlot(EquipmentSlot.LEGS).is(ItemRegistry.TURTLE_LEGGINGS.get())) {
               return true;
            }
        }
        return original;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean preventFeatherDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original) {
        Player PE = (Player)(Object)this;

        if (PE.getMainHandItem().is(Items.FEATHER)) {
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(
                        0.4,
                        net.minecraft.util.Mth.sin(PE.getYRot() * ((float)Math.PI / 180F)),
                        (-net.minecraft.util.Mth.cos(PE.getYRot() * ((float)Math.PI / 180F)))
                );
            }
            return true;
        }

        if (PE.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && PE.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) target.invulnerableTime = 10;

        return original.call(target, source, damage);
    }

    @WrapOperation(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult allowOffhandAttack(Entity instance, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) return InteractionResult.PASS;
        return original.call(instance, player, hand);
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    private void offHandDamage(float adjustTicks, CallbackInfoReturnable<Float> cir){
        Player player = (Player)(Object)this;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) cir.setReturnValue(1f);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z"))
    private void cancelCombo(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        Player PE = (Player)(Object)this;
        ModData.combos.remove(PE.getUUID());
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", ordinal = 0))
    private double comboDamage(double original){
        Player player = (Player)(Object)this;
        ItemStack attackingItemStack = player.getMainHandItem();
        ComboComponent combo = StackData.readCombo(attackingItemStack);
        if (combo.multiplier() != 0) {
            int comboTimer = ModData.combos.getOrDefault(player.getUUID(), 0);
            int comboSec = ceilDiv(comboTimer, 30);
            int multiplier = combo.multiplier();

            if (!player.level().isClientSide()) ModData.combos.put(player.getUUID(), Math.min((comboSec+1)*30, 10*30));

            return original + original * comboSec * multiplier * 0.01;
        }
        return original;
    }

    @Unique
    private static int ceilDiv(int x, int y) {
        final int q = x / y;
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }

    // turtleHelmetMaceBlock (originally @ModifyVariable on hurtServer at the removeEntitiesOnShoulder
    // INVOKE — which does still exist verbatim in Player's own hurt(), Player.java:823) is REMOVED,
    // not ported: its sole purpose was gating on DamageTypes.MACE_SMASH, which does not exist on
    // 1.20.1 (the Mace is a 1.21+ item — NAMED GAP, see class header). An @ModifyVariable that can
    // never do anything but return its input unchanged is dead weight, not a faithful port — unlike
    // LivingEntityMixin's matching helmet branch, which survives because it shares a method with
    // other real chestplate-blocking logic. Restore this method (same anchor, same shape) once the
    // mod or the game ships a mace-like weapon on this branch.
}
