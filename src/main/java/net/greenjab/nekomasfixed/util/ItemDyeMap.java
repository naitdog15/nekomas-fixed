package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.EnumMap;

public class ItemDyeMap {
    public static final EnumMap<AllDyes, Item> DYE = new EnumMap<>(AllDyes.class);
    public static final EnumMap<AllDyes, Item> BRUSH = new EnumMap<>(AllDyes.class);

    static {
        DYE.put(AllDyes.WHITE, Items.WHITE_WOOL);
        DYE.put(AllDyes.ORANGE, Items.ORANGE_WOOL);
        DYE.put(AllDyes.MAGENTA, Items.MAGENTA_WOOL);
        DYE.put(AllDyes.LIGHT_BLUE, Items.LIGHT_BLUE_WOOL);
        DYE.put(AllDyes.YELLOW, Items.YELLOW_WOOL);
        DYE.put(AllDyes.LIME, Items.LIME_WOOL);
        DYE.put(AllDyes.PINK, Items.PINK_WOOL);
        DYE.put(AllDyes.GRAY, Items.GRAY_WOOL);
        DYE.put(AllDyes.LIGHT_GRAY, Items.LIGHT_GRAY_WOOL);
        DYE.put(AllDyes.CYAN, Items.CYAN_WOOL);
        DYE.put(AllDyes.PURPLE, Items.PURPLE_WOOL);
        DYE.put(AllDyes.BLUE, Items.BLUE_WOOL);
        DYE.put(AllDyes.BROWN, Items.BROWN_WOOL);
        DYE.put(AllDyes.GREEN, Items.GREEN_WOOL);
        DYE.put(AllDyes.RED, Items.RED_WOOL);
        DYE.put(AllDyes.BLACK, Items.BLACK_WOOL);
        DYE.put(AllDyes.AMBER, ItemRegistry.AMBER_DYE.get());
        DYE.put(AllDyes.AQUA, ItemRegistry.AQUA_DYE.get());
        DYE.put(AllDyes.INDIGO, ItemRegistry.INDIGO_DYE.get());
        DYE.put(AllDyes.MAROON, ItemRegistry.MAROON_DYE.get());
        
        BRUSH.put(AllDyes.WHITE, ItemRegistry.WHITE_DYED_BRUSH.get());
        BRUSH.put(AllDyes.ORANGE, ItemRegistry.ORANGE_DYED_BRUSH.get());
        BRUSH.put(AllDyes.MAGENTA, ItemRegistry.MAGENTA_DYED_BRUSH.get());
        BRUSH.put(AllDyes.LIGHT_BLUE, ItemRegistry.LIGHT_BLUE_DYED_BRUSH.get());
        BRUSH.put(AllDyes.YELLOW, ItemRegistry.YELLOW_DYED_BRUSH.get());
        BRUSH.put(AllDyes.LIME, ItemRegistry.LIME_DYED_BRUSH.get());
        BRUSH.put(AllDyes.PINK, ItemRegistry.PINK_DYED_BRUSH.get());
        BRUSH.put(AllDyes.GRAY, ItemRegistry.GRAY_DYED_BRUSH.get());
        BRUSH.put(AllDyes.LIGHT_GRAY, ItemRegistry.LIGHT_GRAY_DYED_BRUSH.get());
        BRUSH.put(AllDyes.CYAN, ItemRegistry.CYAN_DYED_BRUSH.get());
        BRUSH.put(AllDyes.PURPLE, ItemRegistry.PURPLE_DYED_BRUSH.get());
        BRUSH.put(AllDyes.BLUE, ItemRegistry.BLUE_DYED_BRUSH.get());
        BRUSH.put(AllDyes.BROWN, ItemRegistry.BROWN_DYED_BRUSH.get());
        BRUSH.put(AllDyes.GREEN, ItemRegistry.GREEN_DYED_BRUSH.get());
        BRUSH.put(AllDyes.RED, ItemRegistry.RED_DYED_BRUSH.get());
        BRUSH.put(AllDyes.BLACK, ItemRegistry.BLACK_DYED_BRUSH.get());
        BRUSH.put(AllDyes.AMBER, ItemRegistry.AMBER_DYED_BRUSH.get());
        BRUSH.put(AllDyes.AQUA, ItemRegistry.AQUA_DYED_BRUSH.get());
        BRUSH.put(AllDyes.INDIGO, ItemRegistry.INDIGO_DYED_BRUSH.get());
        BRUSH.put(AllDyes.MAROON, ItemRegistry.MAROON_DYED_BRUSH.get());
        
    }
}
