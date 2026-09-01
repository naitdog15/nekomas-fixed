package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

/**
 * ItemsMixin's &lt;clinit&gt; hijack is architecturally impossible on Forge — not a retarget.
 * {@code Items.<clinit>} runs inside {@code Bootstrap.bootStrap()}, strictly before any
 * {@code RegisterEvent}, so there is no point at which a mod can still be intercepting
 * the {@code Items.CLOCK} field assignment the way the 26.2 source did (it wrapped
 * {@code Items.registerItem(ResourceKey)} inside a {@code Slice} keyed to
 * {@code net.minecraft.references.ItemIds} — a data-driven bootstrap indirection that plain 1.20.1
 * does not have at all: {@code Items.CLOCK} there is one direct field initializer,
 * {@code registerItem("clock", new Item(new Item.Properties()))}). The chosen replacement is
 * an {@code @Inject(at = @At("HEAD"))} on {@code Item#useOn}, gated on identity against
 * {@code Items.CLOCK} — needs a mixin into {@code Item.class}, which already exists
 * ({@link ItemMixin}); see {@link ItemMixin#nekomasfixed$clockPlacesBlock} and
 * {@link ClockPlacement}.
 * <p>
 * This class is intentionally left empty rather than deleted: {@code nekomasfixed.mixins.json}
 * still names {@code ItemsMixin}, and it is not one of the mixins slated for deletion. An empty
 * {@code @Mixin} class with zero injectors is valid and contributes nothing at apply time, which is
 * exactly the correct behaviour now that its one piece of logic has moved.
 */
@Mixin(Items.class)
public abstract class ItemsMixin {
}
