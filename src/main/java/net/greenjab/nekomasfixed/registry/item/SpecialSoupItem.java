package net.greenjab.nekomasfixed.registry.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

public class SpecialSoupItem extends Item {

    public SpecialSoupItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide() && user instanceof Player player) {
            ItemContainerContents c = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of()));
            List<ItemStack> ingredients = c.allItemsCopyStream().toList();
            for (ItemStack ingredient : ingredients) {
                PotionContents potions = ingredient.get(DataComponents.POTION_CONTENTS);
                if (potions != null) potions.getAllEffects().forEach(effect -> player.addEffect(new MobEffectInstance(effect.withScaledDuration(0.5f))));
                FoodProperties food = ingredient.get(DataComponents.FOOD);
                if (food != null) player.getFoodData().eat(Mth.ceil(food.nutrition()/2f), food.saturation()/2f);
                Consumable consume = ingredient.get(DataComponents.CONSUMABLE);
                if (consume != null) consume.onConsume(level, user, stack.copy());
            }
        }
        Consumable consumableComponent = stack.get(DataComponents.CONSUMABLE);
        return consumableComponent != null ? consumableComponent.onConsume(level, user, stack) : stack;
    }
}
