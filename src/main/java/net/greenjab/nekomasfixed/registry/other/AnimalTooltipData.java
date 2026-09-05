package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

// TooltipComponent is a bare marker in common code (net.minecraft.world.inventory.tooltip), not
// client-only, so this record can be produced without pulling in a rendering class
public record AnimalTooltipData(AnimalComponent contents) implements TooltipComponent {
}
