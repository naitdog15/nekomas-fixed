package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.greenjab.nekomasfixed.util.ModRecipeBookType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;

/**
 * mechanically a furnace - one input, one fuel, one output, matched off RecipeRegistry.KILN - so
 * this just hands AbstractFurnaceMenu its own recipe type and recipe-book type. see
 * {@link ModRecipeBookType} for why the book type can't be the furnace's.
 */
public class KilnMenu extends AbstractFurnaceMenu {

    public KilnMenu(final int containerId, final Inventory inventory) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory);
    }

    public KilnMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory, container, data);
    }
}
