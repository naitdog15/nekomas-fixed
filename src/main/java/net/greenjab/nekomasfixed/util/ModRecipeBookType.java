package net.greenjab.nekomasfixed.util;

import net.minecraft.world.inventory.RecipeBookType;

/**
 * Restores the Kiln's 3
 * recipe-book tabs via Forge's extensible {@code RecipeBookType}/{@code RecipeBookCategories} +
 * {@code RegisterRecipeBookCategoriesEvent}, rather than dropping them.
 * <p>
 * A NEW {@code RecipeBookType} is required here rather than reusing vanilla's {@code
 * RecipeBookType.FURNACE}: {@code RecipeBookCategories.getCategories(RecipeBookType)} is a hard
 * switch over the 4 vanilla types with a {@code default -> RecipeBookManager.getCustomCategoriesOrEmpty}
 * branch for Forge's extensible ones (verified against forge-1.20.1-mapped-src/net/minecraft/client/
 * RecipeBookCategories.java) - reusing {@code FURNACE} would take the vanilla case every time and
 * silently make any custom {@code registerBookCategories(FURNACE, ...)} call dead code, and would
 * additionally cross-contaminate the vanilla Furnace/Smoker/BlastFurnace screens' own tab sets. This
 * is a documented, minimal, necessary deviation from "pristine" KilnMenu, keeping
 * KilnMenu as close to pristine as the Forge API allows while documenting the deviation.
 * <p>
 * {@code RecipeBookType} is common code (referenced from {@code KilnMenu}, which both dists load) -
 * unlike the 3 new {@code RecipeBookCategories} constants and the event handler, which are
 * {@code @OnlyIn(Dist.CLIENT)} and live in {@code screen/KilnRecipeBookClient.java} instead.
 */
public final class ModRecipeBookType {
    private ModRecipeBookType() {
    }

    public static final RecipeBookType KILN = RecipeBookType.create("KILN");
}
