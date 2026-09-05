package net.greenjab.nekomasfixed.util;

import net.minecraft.world.inventory.RecipeBookType;

/**
 * needs its own type rather than reusing RecipeBookType.FURNACE: RecipeBookCategories.getCategories
 * switches over the four built-in types and only falls through to custom ones by default, so
 * borrowing FURNACE would take the built-in case and the furnace/smoker/blast-furnace screens would
 * inherit the kiln's tabs.
 * kept apart from screen/KilnRecipeBookClient (client-only) since KilnMenu names it on both sides.
 */
public final class ModRecipeBookType {
    private ModRecipeBookType() {
    }

    public static final RecipeBookType KILN = RecipeBookType.create("KILN");

    /** call once during mod loading - a player's recipe-book settings are sized from the type list
     * at player creation, so KILN must exist before the first player does. */
    public static void init() {
    }
}
