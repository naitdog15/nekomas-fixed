package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.mixins.json still names
 * {@code ItemContainerContentsMixin}).
 * {@code net.minecraft.world.item.component.ItemContainerContents} (the 1.20.5+ data component this
 * mixin's own name and target class are both about) does not exist on 1.20.1 — there is no
 * component-level {@code addToTooltip} hook to cancel at all, so the "suppress the normal tooltip
 * text, the mod supplies its own" purpose this mixin served is moot rather than portable: per
 * ItemStackMixin's header, the mod's own replacement tooltip text/image for containers is itself a
 * later workstream, not implemented yet.
 */
@Mixin(ItemStack.class)
public class ItemContainerContentsMixin {
}
