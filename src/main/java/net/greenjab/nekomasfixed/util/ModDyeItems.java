package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;

/**
 * The four extra dyes have to be real {@link DyeItem}s - {@code DyeItemMixin}, the firework-star
 * recipe and every {@code PyrotechnicsMenu} slot all recognise a dye by {@code instanceof DyeItem}.
 * On 1.20.1 that constructor demands a {@link DyeColor}, and {@code DyeColor} is a closed enum, so
 * each one borrows the vanilla colour its wool/carpet/terracotta family already uses.
 * <p>
 * {@code DyeItem}'s constructor also files every dye in a static colour-to-item map, which would let
 * these four shadow yellow/light blue/magenta/red in {@code DyeItem.byColor(...)}; {@code
 * DyeItemMixin} skips that one write for this class so vanilla keeps those four entries.
 */
public class ModDyeItems extends DyeItem {
    public ModDyeItems(DyeColor nearestVanillaColor, Item.Properties settings) {
        super(nearestVanillaColor, settings);
    }
}
