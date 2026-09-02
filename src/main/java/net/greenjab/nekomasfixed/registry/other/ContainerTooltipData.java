package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The payload half of the container tooltip (see {@link AnimalTooltipData} for the dispatch route,
 * which is the same one). Produced in {@code mixin/ItemStackMixin} for any container item carrying
 * something, drawn by {@code render/other/ContainerTooltipComponent}.
 * <p>
 * The original record wrapped {@code ItemContainerContents}, a 1.21+ data component absent from
 * 1.20.1; the equivalent shape for "a list of item stacks carried in an item's NBT" here is a plain
 * {@code List<ItemStack>}, matching how vanilla shulker-box items store their inventory (a
 * {@code BlockEntityTag} + an {@code Items} NBT list). Non-empty stacks only, already truncated to
 * what the grid can draw.
 */
public record ContainerTooltipData(List<ItemStack> contents) implements TooltipComponent {
}
