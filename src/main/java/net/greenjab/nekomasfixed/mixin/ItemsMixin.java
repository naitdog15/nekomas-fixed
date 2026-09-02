package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

/**
 * A &lt;clinit&gt; hijack on {@code Items.CLOCK}'s field assignment is architecturally impossible on
 * Forge: {@code Items.<clinit>} runs inside {@code Bootstrap.bootStrap()}, strictly before any
 * registration event fires, and {@code Items.CLOCK} is one direct field initializer with nothing to
 * hook into. The clock-placement hijack instead lives as an {@code @Inject(at = @At("HEAD"))} on
 * {@code Item#useOn}, gated on identity against {@code Items.CLOCK} — see
 * {@link ItemMixin#nekomasfixed$clockPlacesBlock} and {@link ClockPlacement}.
 * <p>
 * This class is intentionally left empty rather than deleted: {@code nekomasfixed.mixins.json}
 * still names {@code ItemsMixin}. An empty {@code @Mixin} class with zero injectors is valid and
 * contributes nothing at apply time, which is exactly the right behaviour now that its one piece of
 * logic has moved.
 */
@Mixin(Items.class)
public abstract class ItemsMixin {
}
