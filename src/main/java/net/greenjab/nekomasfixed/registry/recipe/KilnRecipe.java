package net.greenjab.nekomasfixed.registry.recipe;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * PORT: 1.20.1's {@code AbstractCookingRecipe} is not codec-based (that's the 1.21+ Recipe-as-data
 * system) - it takes a raw constructor {@code (RecipeType<?>, ResourceLocation id, String group,
 * CookingBookCategory, Ingredient, ItemStack result, float xp, int cookTime)}, matching vanilla's own
 * {@code SmeltingRecipe} exactly (verified against forge-1.20.1-mapped-src). {@code getSerializer()}
 * must be implemented directly (the {@code Recipe} interface declares it abstract on 1.20.1, no base
 * default): {@code RecipeRegistry.KILN_SERIALIZER} is expected as a
 * {@code RegistryObject<RecipeSerializer<KilnRecipe>>}, built from a {@code SimpleCookingSerializer<
 * KilnRecipe>}-shaped factory taking this class's exact constructor signature (mirrors vanilla's own
 * {@code RecipeSerializer.SMELTING_RECIPE} construction), since {@code RecipeRegistry.java} lives
 * outside this file. {@code recipeBookCategory()} (a
 * per-recipe override) has no 1.20.1 hook at all; the category-to-{@code RecipeBookCategories} mapping
 * that used to live here is now a CLIENT-only Forge event handler (see {@code
 * screen/KilnRecipeBookClient.java}, which restores the kiln's 3 recipe-book tabs).
 */
public class KilnRecipe extends AbstractCookingRecipe {

    public KilnRecipe(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeRegistry.KILN.get(), id, group, category, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemRegistry.KILN.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.KILN_SERIALIZER.get();
    }
}
