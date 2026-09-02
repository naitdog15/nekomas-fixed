package net.greenjab.nekomasfixed.registry.item;

import com.google.common.collect.Multimap;
import net.greenjab.nekomasfixed.registry.other.ComboComponent;
import net.greenjab.nekomasfixed.util.ModItemSettings;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SickleItem extends Item {

    public static final float SPEED = -2.4F;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    /**
     * The combo step this sickle's tier is worth. There is no way to bake a starting value onto a
     * stack at craft time, so the item holds it and hands it out as the fallback for any stack
     * that carries none of its own.
     */
    private final int comboMultiplier;

    public SickleItem(Tier material, Item.Properties settings) {
        super(settings);
        this.defaultModifiers = ModItemSettings.sickleAttributeModifiers(material, SPEED);
        this.comboMultiplier = ModItemSettings.sickleDefaultCombo(material);
    }

    /** What a fresh sickle of this tier starts its combo ramp at, in percent per step. */
    public int comboMultiplier() {
        return this.comboMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.addAll(StackData.read(stack, StackData.KEY_COMBO_MULTIPLIER, ComboComponent.CODEC,
                new ComboComponent(this.comboMultiplier)).tooltipLines());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.IRON_INGOT);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (!user.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES))  return InteractionResultHolder.pass(stack);
        if (user.getAttackStrengthScale(0)<0.5) return InteractionResultHolder.pass(stack);
        user.getCooldowns().addCooldown(stack.getItem(), 12);
        if (user.attackStrengthTicker>5) user.attackStrengthTicker = 5;
        return InteractionResultHolder.success(stack);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!user.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SICKLES))  return InteractionResult.PASS;
        if (user.getAttackStrengthScale(0)<0.5) return InteractionResult.PASS;
        if (user.getCooldowns().getCooldownPercent(user.getItemInHand(hand).getItem(), 0)>0) return InteractionResult.PASS;
        user.getCooldowns().addCooldown(stack.getItem(), 12);
        if (user.attackStrengthTicker>5) user.attackStrengthTicker = 5;
        if (user.level().isClientSide()) return InteractionResult.SUCCESS;

        int tt = user.attackStrengthTicker;
        swapHands(user);
        user.detectEquipmentUpdates();
        user.attackStrengthTicker =1000;
        user.attack(entity);
        swapHands(user);
        user.attackStrengthTicker =tt;
        return InteractionResult.SUCCESS;
    }

    private static void swapHands(Player user) {
        ItemStack itemStack = user.getItemInHand(InteractionHand.OFF_HAND);
        user.setItemInHand(InteractionHand.OFF_HAND, user.getItemInHand(InteractionHand.MAIN_HAND));
        user.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
    }
}
