package net.greenjab.nekomasfixed.util;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The state a bed is left in after someone sleeps in it, until it is made again.
 *
 * <p>A bed's block model only ever supplies a particle - the covers are drawn by
 * {@code BedRenderer} off the bed sheet - so the rumpled look is not something a blockstate file can
 * select. {@code mixin/client/BedRendererMixin} reads this property and swaps the bedding texture
 * instead.
 */
public interface MessyBedAccessor {
    BooleanProperty MESSY = BooleanProperty.create("messy");
}
