package net.greenjab.nekomasfixed.registry.item;

import com.mojang.serialization.Codec;

import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpecialSoupItem extends Item {

    /**
     * The bowl-cauldron's brewed ingredients, stashed under this key via StackData instead of
     * a 1.21 CONTAINER component. SoupCauldronBlock writes the copied input stacks here when it
     * ladles the stew out; this class only ever reads them back.
     */
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final Codec<List<ItemStack>> INGREDIENTS_CODEC = ItemStack.CODEC.listOf();

    public SpecialSoupItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        // always drinkable regardless of hunger - the payoff here is the brewed-in
        // effects, not the (zero) nutrition
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(user.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack result = super.finishUsingItem(stack, level, user);
        if (!level.isClientSide && user instanceof Player player) {
            List<ItemStack> ingredients = StackData.read(stack, KEY_INGREDIENTS, INGREDIENTS_CODEC, List.of());
            for (ItemStack ingredient : ingredients) {
                if (ingredient.isEmpty()) continue;
                for (MobEffectInstance effect : PotionUtils.getMobEffects(ingredient)) {
                    player.addEffect(new MobEffectInstance(effect.getEffect(), Math.max(1, effect.getDuration() / 2), effect.getAmplifier()));
                }
                FoodProperties food = ingredient.getFoodProperties(player);
                if (food != null) player.getFoodData().eat(Mth.ceil(food.getNutrition() / 2F), food.getSaturationModifier() / 2F);
            }
        }
        return user instanceof Player player && player.getAbilities().instabuild ? result : new ItemStack(Items.BOWL);
    }
}
