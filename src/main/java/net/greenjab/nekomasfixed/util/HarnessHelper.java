package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

/**
 * ghast harness wearing/removal/slot is owned by whatever supplies the happy ghast; this only gates
 * the creative tab listing and the equip interaction for this mod's own four.
 */
public final class HarnessHelper {

    private HarnessHelper() {
    }

    public static boolean isModHarness(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return matches(stack, ItemRegistry.AMBER_HARNESS)
                || matches(stack, ItemRegistry.AQUA_HARNESS)
                || matches(stack, ItemRegistry.INDIGO_HARNESS)
                || matches(stack, ItemRegistry.MAROON_HARNESS);
    }

    private static boolean matches(ItemStack stack, RegistryObject<Item> handle) {
        return handle.isPresent() && stack.is(handle.get());
    }
}
