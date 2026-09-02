package net.greenjab.nekomasfixed.util;

import net.minecraft.world.inventory.RecipeBookType;

/**
 * The kiln's own recipe-book type, so the kiln screen gets its own set of tabs.
 *
 * <p>It has to be a new type rather than a reuse of {@code RecipeBookType.FURNACE}:
 * {@code RecipeBookCategories.getCategories(RecipeBookType)} switches over the four built-in types
 * and only falls through to the registered custom ones in its default branch, so borrowing
 * {@code FURNACE} would take the built-in case every time - the kiln's tabs would never be asked
 * for, and the furnace, smoker and blast-furnace screens would inherit them if they were.
 *
 * <p>Kept apart from the tabs themselves ({@code screen/KilnRecipeBookClient}) because those are
 * client-only and this is not: {@code KilnMenu} names it on both sides.
 *
 * <p>{@link #init()} exists because a player's recipe-book settings are sized from the list of
 * types the moment that player is created. Touching this class during mod loading makes sure
 * {@link #KILN} is on that list before the first player ever is.
 */
public final class ModRecipeBookType {
    private ModRecipeBookType() {
    }

    public static final RecipeBookType KILN = RecipeBookType.create("KILN");

    /** Forces {@link #KILN} into existence. Call once while the mod is loading; does nothing else. */
    public static void init() {
    }
}
