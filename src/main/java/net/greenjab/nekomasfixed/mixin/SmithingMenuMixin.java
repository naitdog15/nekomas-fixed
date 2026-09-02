package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Turtle armour cannot be taken to the smithing table - no netherite upgrade, no trim.
 *
 * <p>The result slot is filled imperatively here rather than through an optional, so the check has
 * to sit at the head of createResult and clear the slot itself the way the no-recipe branch does;
 * leaving it alone would keep whatever the previous ingredients produced on screen.
 *
 * <p>The two containers are declared on {@link ItemCombinerMenu} rather than on SmithingMenu, which
 * is why this extends it instead of shadowing them.
 */
@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    protected SmithingMenuMixin(MenuType<?> type, int syncId, Inventory inventory, ContainerLevelAccess access) {
        super(type, syncId, inventory, access);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void nekomasfixed$noTurtleUpgrade(CallbackInfo ci) {
        ItemStack gear = this.inputSlots.getItem(1);
        if (gear.is(ItemRegistry.TURTLE_CHESTPLATE.get()) || gear.is(ItemRegistry.TURTLE_LEGGINGS.get())
                || gear.is(ItemRegistry.TURTLE_BOOTS.get())) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            ci.cancel();
        }
    }
}
