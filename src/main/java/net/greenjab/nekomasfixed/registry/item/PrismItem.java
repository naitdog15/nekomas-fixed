package net.greenjab.nekomasfixed.registry.item;

import net.greenjab.nekomasfixed.registry.other.PrismGlint;
import net.minecraft.world.item.Item;

/** Smithing template for armour glints; the glint it applies is fixed per item. */
public class PrismItem extends Item {
    private final PrismGlint glint;

    public PrismItem(PrismGlint glint, Properties properties) {
        super(properties);
        this.glint = glint;
    }

    public PrismGlint getGlint() {
        return glint;
    }
}
