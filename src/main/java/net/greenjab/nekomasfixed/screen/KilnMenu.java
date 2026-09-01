package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.greenjab.nekomasfixed.util.ModRecipeBookType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * {@code RecipeManagerMixin} is deleted, so both constructors take the plain {@code
 * RecipeType<KilnRecipe>} in place of the dropped {@code RecipePropertySet} key - 1.20.1's
 * {@code AbstractFurnaceMenu} matches recipes off the {@code RecipeType} itself. The 3rd constructor
 * argument is {@code ModRecipeBookType.KILN}, not vanilla's {@code RecipeBookType.FURNACE} - see that
 * class's javadoc for why. Everything else about {@code AbstractFurnaceMenu}'s constructor is unchanged.
 */
public class KilnMenu extends AbstractFurnaceMenu {

    public KilnMenu(final int containerId, final Inventory inventory) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory);
    }

    public KilnMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
        super(ScreenHandlerRegistry.KILN.get(), RecipeRegistry.KILN.get(), ModRecipeBookType.KILN, containerId, inventory, container, data);
    }
}
