package net.greenjab.nekomasfixed.screen;

import com.google.common.collect.ImmutableList;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.recipe.KilnRecipe;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.util.ModRecipeBookType;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * The kiln's recipe book: its three tabs (search / blocks / misc), the rule that sorts a kiln recipe
 * into one of them, and the book component the screen hangs off.
 *
 * <p>Everything here is client-only - both {@link RecipeBookCategories} and the event that registers
 * them are - which is why the tabs live in this file and {@link ModRecipeBookType#KILN}, which the
 * common {@link KilnMenu} has to name, lives on its own beside the menus.
 *
 * <p>The search tab holds no recipes of its own; it is declared as an aggregate over the other two,
 * the way vanilla's furnace search tab is, or it opens empty.
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
            event.registerAggregateCategory(KILN_SEARCH, ImmutableList.of(KILN_BLOCKS, KILN_MISC));
            event.registerRecipeCategoryFinder(RecipeRegistry.KILN.get(), recipe -> {
                if (recipe instanceof KilnRecipe kilnRecipe && kilnRecipe.category() == CookingBookCategory.BLOCKS) {
                    return KILN_BLOCKS;
                }
                return KILN_MISC;
            });
        }
    }

    /** The book itself. Fuel is the same list the vanilla furnaces accept - a kiln burns anything they do. */
    public static final class KilnRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
        @Override
        protected Set<Item> getFuelItems() {
            return AbstractFurnaceBlockEntity.getFuel().keySet();
        }
    }
}
