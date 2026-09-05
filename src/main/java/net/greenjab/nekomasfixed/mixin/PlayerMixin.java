package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.compat.CompatMods;
import net.greenjab.nekomasfixed.registry.item.SickleItem;
import net.greenjab.nekomasfixed.registry.other.ComboComponent;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.util.ModData;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * The player half of this mod's combat and gear behaviour: the sickle combo counter, dual-sickle
 * off-hand swings, the feather's harmless shove, the turtle leggings' underwater mining, and the
 * turtle helmet's mace soak.
 *
 * <p>{@code Player} has a {@code hurt} of its own that difficulty-scales and then calls
 * {@code super.hurt}, so anything player-specific hooks the former while everything in
 * LivingEntityMixin still reaches players through the latter - there is no reason to duplicate a
 * hook here beyond the helmet, which has to catch the blow before the scaling touches it.
 *
 * <p>Two anchors are not the obvious ones. Combo damage rides the first
 * {@code getAttributeValue(ATTACK_DAMAGE)} inside {@code attack} because that is where the base
 * damage exists before attack-strength scaling touches it; the dual-sickle full-damage override
 * rides {@code getAttackStrengthScale}, which is what {@code attack} reads at that point.
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

    // 1.20.1 moved the turtle-helmet check out of tick() into its own turtleHelmetTick(),
    // so that call is the anchor here rather than the isEyeInFluid probe inside it.
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;turtleHelmetTick()V"))
    private void customTickLogics(CallbackInfo ci) {
        Player PE = (Player)(Object)this;

        if (NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get() && PE.onGround() && !PE.isInWater()) {
            if (PE.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.TURTLE_BOOTS.get())) {
                PE.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE, 200, 0, false, false, true));
            }
        }
        if (PE.level().getBiome(PE.blockPosition()).is(BiomeTags.IS_NETHER)) {
            if (!PE.isCreative()&&!PE.isSpectator() && NekomasFixedConfig.NETHER_FOOD_ROTTING.get()){
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
        if (NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get() && PE.isEyeInFluid(FluidTags.WATER)) {
            if (PE.getItemBySlot(EquipmentSlot.LEGS).is(ItemRegistry.TURTLE_LEGGINGS.get())) {
               return true;
            }
        }
        return original;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean preventFeatherDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original) {
        Player PE = (Player)(Object)this;

        if (NekomasFixedConfig.FEATHER_KNOCKBACK.get() && PE.getMainHandItem().is(Items.FEATHER)) {
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(
                        0.4,
                        net.minecraft.util.Mth.sin(PE.getYRot() * ((float)Math.PI / 180F)),
                        (-net.minecraft.util.Mth.cos(PE.getYRot() * ((float)Math.PI / 180F)))
                );
            }
            return true;
        }

        if (NekomasFixedConfig.OFFHAND_ATTACK.get() && PE.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && PE.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) target.invulnerableTime = 10;

        return original.call(target, source, damage);
    }

    @WrapOperation(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult allowOffhandAttack(Entity instance, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (NekomasFixedConfig.OFFHAND_ATTACK.get() && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) return InteractionResult.PASS;
        return original.call(instance, player, hand);
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    private void offHandDamage(float adjustTicks, CallbackInfoReturnable<Float> cir){
        Player player = (Player)(Object)this;
        if (NekomasFixedConfig.OFFHAND_ATTACK.get() && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) cir.setReturnValue(1f);
    }

    // Player#hurt is reachable on the client, and the combo table is a single shared map - letting a
    // client-side hurt clear it would wipe the count the server is still keeping in single-player
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z"))
    private void cancelCombo(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        Player PE = (Player)(Object)this;
        if (PE.level().isClientSide()) return;
        ModData.combos.remove(PE.getUUID());
    }

    // attack() calls getAttributeValue on itself, so 1.20.1 compiles the owner as Player even though
    // the method is declared up on LivingEntity - LivingEntity as the owner matches nothing here.
    // Ordinal 0 is still the ATTACK_DAMAGE read; 1 is ATTACK_KNOCKBACK and 2 is Forge's entity reach.
    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", ordinal = 0))
    private double comboDamage(double original){
        if (!NekomasFixedConfig.SICKLE_COMBO.get()) return original;
        Player player = (Player)(Object)this;
        ItemStack attackingItemStack = player.getMainHandItem();
        // The multiplier is an item property, not stack state: 26.2 baked it on as a default data
        // component, which 1.20.1 has no equivalent for. A stack that carries its own value still
        // wins; a freshly crafted sickle falls back to what its tier is worth, which is also what
        // its tooltip prints.
        ComboComponent combo = StackData.read(attackingItemStack, StackData.KEY_COMBO_MULTIPLIER,
                ComboComponent.CODEC,
                attackingItemStack.getItem() instanceof SickleItem sickle
                        ? new ComboComponent(sickle.comboMultiplier())
                        : new ComboComponent(0));
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

    // The player's copy of the turtle helmet's mace soak. Player#hurt scales the blow by difficulty
    // and only then hands on to LivingEntity#hurt, so the helmet has to catch it here as well to soak
    // what was actually swung; the anchor is the first point inside hurt() where the hit is known to
    // be real. The sentinel is LivingEntityMixin's - it means "wholly absorbed".
    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"), ordinal = 0, argsOnly = true)
    private float turtleHelmetMaceBlock(float damage, @Local(argsOnly = true) DamageSource source) {
        Player PE = (Player)(Object)this;
        if (NekomasFixedConfig.MACE_INTERACTIONS.get()
                && PE.getItemBySlot(EquipmentSlot.HEAD).is(Items.TURTLE_HELMET)
                && isMaceSmash(source)) {
            PE.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak((int) damage, PE,
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
}
