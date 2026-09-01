package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * The payload half of an item's tooltip image: {@code Item#getTooltipImage} returns one of these,
 * and {@code ClientTooltipComponent.create(TooltipComponent)} turns it into the drawable half (see
 * {@code mixin/client/ClientTooltipComponentMixin}, which is what teaches that dispatch about this
 * type). {@code TooltipComponent} itself is a bare marker interface on 1.20.1, same as vanilla's own
 * {@code BundleTooltip} uses.
 */
public record AnimalTooltipData(AnimalComponent contents) implements TooltipComponent {
}
