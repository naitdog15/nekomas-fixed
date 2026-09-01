package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * DESIGN (see {@link AnimalTooltipData}): the original record wrapped {@code
 * ItemContainerContents}, a 1.21+ data component absent from 1.20.1. 1.20.1's equivalent shape for
 * "a list of item stacks carried in an item's NBT" is a plain {@code List<ItemStack>}, matching how
 * vanilla shulker-box items store their inventory (a {@code BlockEntityTag} + an {@code Items} NBT
 * list). That vanilla-component consumer wiring is a concern for whichever package owns the item
 * class; this record only carries the shape.
 */
public record ContainerTooltipData(List<ItemStack> contents) {
}
