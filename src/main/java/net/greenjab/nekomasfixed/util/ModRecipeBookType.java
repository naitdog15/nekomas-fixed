package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;

import java.util.List;

public class ModRecipeBookType extends RecipeBookCategory implements ExtendedRecipeBookCategory {
    private final List<RecipeBookCategory> categories;

    public ModRecipeBookType(final RecipeBookCategory... categories) {
        this.categories = List.of(categories);
    }

    public List<RecipeBookCategory> getCategories() {
        return this.categories;
    }
}