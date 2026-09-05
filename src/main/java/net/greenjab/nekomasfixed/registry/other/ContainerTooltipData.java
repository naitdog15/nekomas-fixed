package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// stands in for ItemContainerContents (a 1.21+ component absent here); shape matches how vanilla
// shulker-box items already store their inventory. non-empty stacks only, already grid-truncated
public record ContainerTooltipData(List<ItemStack> contents) implements TooltipComponent {
}
