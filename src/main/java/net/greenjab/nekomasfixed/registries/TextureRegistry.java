package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;

/**
 * {@code TextureStitchEvent.Pre} does not exist on Forge 47.x (only
 * {@code Post} is live; Forge's own comment says "use atlas info JSON files instead"), so there is
 * <b>no event here at all</b> — vanilla's {@code chests.json} directory lister auto-stitches every
 * PNG under {@code assets/nekomasfixed/textures/entity/chest/} into the shared chest atlas the moment
 * the files exist on disk. This class is reduced to four {@link Material} constants (the
 * {@code Sheets.CHEST_SHEET} atlas id + the per-variant texture path), replacing the old
 * {@code SpriteId}/{@code CHEST_MAPPER} lookup.
 */
public class TextureRegistry {

    public static final Material CLAM_MATERIAL = chestMaterial("clam");
    public static final Material CLAM_BLUE_MATERIAL = chestMaterial("clam_blue");
    public static final Material CLAM_PINK_MATERIAL = chestMaterial("clam_pink");
    public static final Material CLAM_PURPLE_MATERIAL = chestMaterial("clam_purple");

    private static Material chestMaterial(String name) {
        return new Material(Sheets.CHEST_SHEET, NekomasFixed.id("entity/chest/" + name));
    }

    /** Was {@code ClamBlockEntityRenderState.Variant} — the state class it lived on was deleted as
     * part of the render-state consolidation, so the enum moves here with the rest of the clam texture lookup. */
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
