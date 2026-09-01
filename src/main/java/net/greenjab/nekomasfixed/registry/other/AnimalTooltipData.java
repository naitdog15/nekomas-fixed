package net.greenjab.nekomasfixed.registry.other;

/**
 * DESIGN: 1.20.1 has no general item->tooltip-image mechanism — the
 * {@code net.minecraft.world.inventory.tooltip.TooltipComponent} marker interface this record used
 * to implement is a 1.21+ data-component-era API with no 1.20.1 equivalent (image-in-tooltip
 * rendering on 1.20.1 was special-cased per feature, e.g. bundles, not general). Kept as a plain
 * data carrier; wiring actual tooltip-image rendering through a client mixin (comparable to
 * {@code ClientTooltipComponentMixin}) is left to whichever package owns the renderer, not invented
 * here.
 */
public record AnimalTooltipData(AnimalComponent contents) {
}
