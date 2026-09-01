package net.greenjab.nekomasfixed.util;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The state a bed is left in after someone sleeps in it, until it is made again.
 *
 * <p>It is state only here, with no look of its own: a bed is drawn by {@code BedRenderer} off
 * {@code ModelLayers.BED_HEAD}/{@code BED_FOOT} with a {@code Sheets.BED_SHEET} material picked by
 * {@code DyeColor}, and the per-colour block models only ever supply a particle - so there is no
 * second appearance for a block model to select. The rumpled-sheet textures are still in the pack,
 * unused, waiting for whichever renderer eventually gets to choose between them.
 */
public interface MessyBedAccessor {
    BooleanProperty MESSY = BooleanProperty.create("messy");
}
