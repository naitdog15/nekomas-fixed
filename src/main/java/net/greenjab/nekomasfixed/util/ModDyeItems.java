package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public class ModDyeItems extends DyeItem {
    private static final Map<ModColors, ModDyeItems> DYES = new HashMap<>();
    private final ModColors color;

    public ModDyeItems(ModColors color, Item.Properties settings) {
        super(DyeColor.WHITE, settings);
        this.color = color;
        DYES.put(color, this);
    }

    public ModColors getModColor() {
        return color;
    }

    public static ModDyeItems byColor(ModColors color) {
        return DYES.get(color);
    }
}
