package net.greenjab.nekomasfixed.util;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * bed covers are drawn by BedRenderer off the bed sheet, not selected via the blockstate file, so
 * BedRendererMixin reads this property to swap in the messy texture instead.
 */
public interface MessyBedAccessor {
    BooleanProperty MESSY = BooleanProperty.create("messy");
}
