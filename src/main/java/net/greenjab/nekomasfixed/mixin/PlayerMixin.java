package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.network.ServerFlags;
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

// Player has a hurt of its own that difficulty-scales then calls super.hurt, so player-specific
// hooks go on the former while LivingEntityMixin still reaches players through the latter - no
// reason to duplicate a hook here beyond the helmet, which must catch the blow before scaling touches it.
// combo damage rides the first getAttributeValue(ATTACK_DAMAGE) inside attack, where base damage
// exists before attack-strength scaling touches it; the dual-sickle override rides getAttackStrengthScale,
// what attack reads at that point.
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

    // 1.20.1 moved the turtle-helmet check out of tick() into its own turtleHelmetTick(), so that
    // call is the anchor here rather than the isEyeInFluid probe inside it.
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;turtleHelmetTick()V"))
    private void customTickLogics(CallbackInfo ci) {
        Player PE = (Player)(Object)this;

        if (!PE.level().isClientSide() && ServerFlags.turtleArmourAbilities() && PE.onGround() && !PE.isInWater()) {
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
        if (ServerFlags.turtleArmourAbilities() && PE.isEyeInFluid(FluidTags.WATER)) {
            if (PE.getItemBySlot(EquipmentSlot.LEGS).is(ItemRegistry.TURTLE_LEGGINGS.get())) {
               return true;
            }
        }
        return original;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean preventFeatherDamage(Entity target, DamageSource source, float damage, Operation<Boolean> original) {
        Player PE = (Player)(Object)this;

        if (ServerFlags.featherKnockback() && PE.getMainHandItem().is(Items.FEATHER)) {
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(
                        0.4,
                        net.minecraft.util.Mth.sin(PE.getYRot() * ((float)Math.PI / 180F)),
                        (-net.minecraft.util.Mth.cos(PE.getYRot() * ((float)Math.PI / 180F)))
                );
            }
            return true;
        }

        if (ServerFlags.offhandAttack() && PE.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && PE.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) target.invulnerableTime = 10;

        return original.call(target, source, damage);
    }

    @WrapOperation(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult allowOffhandAttack(Entity instance, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (ServerFlags.offhandAttack() && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) return InteractionResult.PASS;
        return original.call(instance, player, hand);
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    private void offHandDamage(float adjustTicks, CallbackInfoReturnable<Float> cir){
        Player player = (Player)(Object)this;
        if (ServerFlags.offhandAttack() && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES) && player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.SICKLES)) cir.setReturnValue(1f);
    }

    // Player#hurt is reachable on the client, and the combo table is a single shared map - a
    // client-side hurt clearing it would wipe the count the server is still keeping in single-player.
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
        if (!ServerFlags.sickleCombo()) return original;
        Player player = (Player)(Object)this;
        ItemStack attackingItemStack = player.getMainHandItem();
        // 1.20.1 has no default data component for this, so it's an item property not stack state -
        // a stack with its own value wins, a freshly crafted sickle falls back to its tier's value.
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

    // Player#hurt scales the blow by difficulty before handing on to LivingEntity#hurt, so the helmet
    // must catch it here too to soak what was actually swung. sentinel is LivingEntityMixin's - means
    // "wholly absorbed".
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

    // a mace raises no damage source of its own, so a smash is recognised off the swing instead - a
    // mace in hand and a drop behind it. matched by id, which doubles as the presence check: with
    // the mace's mod absent no item carries that id and this is simply never true.
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
