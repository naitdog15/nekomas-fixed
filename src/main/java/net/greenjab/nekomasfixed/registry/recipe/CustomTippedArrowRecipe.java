package net.greenjab.nekomasfixed.registry.recipe;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

// one arrow stack plus any number of effect potions; the whole arrow stack converts, each potion
// is spent for a glass bottle. getRemainingItems empties the arrow slot itself so ResultSlot.onTake
// doesn't also remove one from it.
public class CustomTippedArrowRecipe extends CustomRecipe {

    public CustomTippedArrowRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int arrows = 0;
        List<Potion> potions = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.ARROW)) {
                if (++arrows > 1) return false;
            } else if (stack.is(Items.POTION)) {
                Potion potion = PotionUtils.getPotion(stack);
                if (potion.getEffects().isEmpty()) return false;
                potions.add(potion);
            } else {
                return false;
            }
        }
        if (arrows != 1 || potions.isEmpty()) return false;

        for (int i = 0; i < potions.size(); i++) {
            for (int j = i + 1; j < potions.size(); j++) {
                if (!checkPotions(potions.get(i), potions.get(j))) return false;
            }
        }
        return true;
    }

    private static boolean checkPotions(Potion pot1, Potion pot2) {
        List<MobEffectInstance> a = pot1.getEffects();
        List<MobEffectInstance> b = pot2.getEffects();
        if (a.size() != b.size()) return true;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getEffect().equals(b.get(i).getEffect())) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        int count = 0;
        List<Potion> potions = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(Items.ARROW)) {
                count = stack.getCount();
            } else if (stack.is(Items.POTION)) {
                potions.add(PotionUtils.getPotion(stack));
            }
        }
        ItemStack output = new ItemStack(ItemRegistry.TIPPED_ARROW_CUSTOM.get(), count);
        StackData.writeTippedPotions(output, potions);
        return output;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(Items.ARROW)) {
                container.setItem(i, ItemStack.EMPTY);
            } else if (stack.is(Items.POTION)) {
                remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CUSTOM_TIPPED_ARROW.get();
    }
}
