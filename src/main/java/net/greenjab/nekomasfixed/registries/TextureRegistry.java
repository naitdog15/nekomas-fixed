package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;

/** vanilla's chests.json atlas definition lists the whole textures/entity/chest directory of every
 * namespace, so these PNGs join the chest sheet just by being there - nothing to stitch by hand. */
public class TextureRegistry {

    public static final Material CLAM_MATERIAL = chestMaterial("clam");
    public static final Material CLAM_BLUE_MATERIAL = chestMaterial("clam_blue");
    public static final Material CLAM_PINK_MATERIAL = chestMaterial("clam_pink");
    public static final Material CLAM_PURPLE_MATERIAL = chestMaterial("clam_purple");

    private static Material chestMaterial(String name) {
        return new Material(Sheets.CHEST_SHEET, NekomasFixed.id("entity/chest/" + name));
    }

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
