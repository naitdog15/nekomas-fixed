package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;

/**
 * the four extra dyes must be real DyeItems - DyeItemMixin, the firework-star recipe and every
 * PyrotechnicsMenu slot recognise a dye by instanceof DyeItem. DyeColor is a closed enum, so each
 * one borrows the vanilla colour its wool/carpet/terracotta family already uses.
 * DyeItem's constructor also files every dye into a static colour-to-item map, which would let
 * these shadow yellow/light blue/magenta/red in DyeItem.byColor(...); DyeItemMixin skips that write
 * for this class so vanilla keeps those four entries.
 */
public class ModDyeItems extends DyeItem {
    public ModDyeItems(DyeColor nearestVanillaColor, Item.Properties settings) {
        super(nearestVanillaColor, settings);
    }
}
