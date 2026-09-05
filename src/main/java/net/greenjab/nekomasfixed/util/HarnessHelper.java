package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

/**
 * Whatever supplies the happy ghast owns the wearing, the removal and the slot a harness sits in, so
 * the only say this mod has over its own four is whether they are offered at all - the creative tab
 * listing and the interaction that puts one on a ghast. Both read this.
 */
public final class HarnessHelper {

    private HarnessHelper() {
    }

    /** True when the stack is one of this mod's four harnesses; false when they were never registered. */
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
