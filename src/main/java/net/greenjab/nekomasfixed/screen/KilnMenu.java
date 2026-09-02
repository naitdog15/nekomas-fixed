package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.greenjab.nekomasfixed.util.ModRecipeBookType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;

/**
 * The kiln's container. It is a furnace in every mechanical respect - one input, one fuel slot, one
 * output, matched off {@code RecipeRegistry.KILN} - so it just hands {@code AbstractFurnaceMenu} its
 * own recipe type and its own recipe-book type. See {@link ModRecipeBookType} for why the book type
 * cannot be the furnace's.
 */
public class KilnMenu extends AbstractFurnaceMenu {

    public KilnMenu(final int containerId, final Inventory inventory) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory);
    }

    public KilnMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory, container, data);
    }
}
