package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

// left intentionally empty rather than deleted: nekomasfixed.mixins.json still names ItemsMixin, and
// an empty @Mixin with zero injectors is valid and does nothing - correct now that its one piece of
// logic (the clock-placement hijack) moved to ItemMixin#nekomasfixed$clockPlacesBlock.
@Mixin(Items.class)
public abstract class ItemsMixin {
}
