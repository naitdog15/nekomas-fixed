package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;

/**
 * Where the clam's shell textures live on the chest atlas.
 *
 * <p>Nothing has to be stitched by hand: vanilla's own {@code chests.json} atlas definition lists
 * the whole {@code textures/entity/chest} directory of every namespace, so the clam PNGs under
 * {@code assets/nekomasfixed/textures/entity/chest/} join the chest sheet simply by being there.
 * All that is needed here is the {@link Material} for each shell.
 */
public class TextureRegistry {

    public static final Material CLAM_MATERIAL = chestMaterial("clam");
    public static final Material CLAM_BLUE_MATERIAL = chestMaterial("clam_blue");
    public static final Material CLAM_PINK_MATERIAL = chestMaterial("clam_pink");
    public static final Material CLAM_PURPLE_MATERIAL = chestMaterial("clam_purple");

    private static Material chestMaterial(String name) {
        return new Material(Sheets.CHEST_SHEET, NekomasFixed.id("entity/chest/" + name));
    }

    /** Which shell a clam wears. Kept here, beside the materials it picks between. */
    public enum Variant {
        BLUE,
        PINK,
        PURPLE,
        REGULAR
    }

    public static Material getClamMaterial(Variant variant) {
        return switch (variant) {
            case BLUE -> CLAM_BLUE_MATERIAL;
            case PINK -> CLAM_PINK_MATERIAL;
            case PURPLE -> CLAM_PURPLE_MATERIAL;
            default -> CLAM_MATERIAL;
        };
    }
}
