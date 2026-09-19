package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record ContainerTooltipData(ItemContainerContents contents) implements TooltipComponent {
}
