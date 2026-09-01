package net.greenjab.nekomasfixed.screen;

import com.google.common.collect.ImmutableList;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.util.ModRecipeBookType;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

/**
 * Restores the Kiln's 3 recipe-book tabs
 * (search / blocks / misc, mirroring 26.2's own {@code KilnScreen.TABS} icons) via Forge's real
 * extensible-enum API, rather than dropping them outright. Client-only: {@code
 * RecipeBookCategories} and {@code RegisterRecipeBookCategoriesEvent} are both {@code
 * @OnlyIn(Dist.CLIENT)} in Forge itself (verified against forge-1.20.1-mapped-src), so both the 3 new
 * category constants and the event handler live in this one file, kept separate from {@code
 * ModRecipeBookType.KILN} (common, referenced from the common {@code KilnMenu}).
 */
@OnlyIn(Dist.CLIENT)
public final class KilnRecipeBookClient {
    private KilnRecipeBookClient() {
    }

    public static final RecipeBookCategories KILN_SEARCH = RecipeBookCategories.create("NEKOMASFIXED_KILN_SEARCH", new ItemStack(Items.COMPASS));
    public static final RecipeBookCategories KILN_BLOCKS = RecipeBookCategories.create("NEKOMASFIXED_KILN_BLOCKS", new ItemStack(Items.SAND));
    public static final RecipeBookCategories KILN_MISC = RecipeBookCategories.create("NEKOMASFIXED_KILN_MISC", new ItemStack(Items.CLAY_BALL));

    @Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            event.registerBookCategories(ModRecipeBookType.KILN, ImmutableList.of(KILN_SEARCH, KILN_BLOCKS, KILN_MISC));
            event.registerRecipeCategoryFinder(RecipeRegistry.KILN.get(), recipe -> {
                if (recipe instanceof net.greenjab.nekomasfixed.registry.recipe.KilnRecipe kilnRecipe) {
                    return kilnRecipe.category() == CookingBookCategory.BLOCKS ? KILN_BLOCKS : KILN_MISC;
                }
                return KILN_MISC;
            });
        }
    }

    /** {@code AbstractFurnaceRecipeBookComponent} has exactly one abstract method on 1.20.1. */
    public static final class KilnRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
        @Override
        protected Set<Item> getFuelItems() {
            return AbstractFurnaceBlockEntity.getFuel().keySet();
        }
    }
}
