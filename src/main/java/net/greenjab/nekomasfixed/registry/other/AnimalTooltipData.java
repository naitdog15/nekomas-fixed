package net.greenjab.nekomasfixed.registry.other;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * The payload half of an item's tooltip image: a nautilus shell carrying a creature hands one of
 * these back from {@code Item#getTooltipImage} (produced in {@code mixin/ItemStackMixin}), and
 * {@code ClientTooltipComponent.create(TooltipComponent)} turns it into the drawable half — the
 * spinning mob preview in {@code render/other/AnimalTooltipComponent}. The two are wired together in
 * {@code render/other/ModTooltipComponents}.
 * <p>
 * {@code TooltipComponent} is a bare marker interface, the same one vanilla's own
 * {@code BundleTooltip} implements, and it lives in {@code net.minecraft.world.inventory.tooltip} —
 * common code, not client-only. That is what lets this record be produced from a shared code path
 * without dragging a rendering class onto a dedicated server.
 */
public record AnimalTooltipData(AnimalComponent contents) implements TooltipComponent {
}
