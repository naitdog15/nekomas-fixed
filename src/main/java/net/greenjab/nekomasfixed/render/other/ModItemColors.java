package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Colours the items whose look depends on what went into them.
 *
 * <p>So far that is the special stew: the soup cauldron blends its ingredients' colours and writes
 * the result onto the bowl the same way a dyed item carries its colour, and this paints the stew
 * layer of the item model with it. The bowl underneath is left alone.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModItemColors {

    /** What a stew looks like when nothing has been cooked into it yet. */
    private static final int UNSEASONED = -65535;

    private ModItemColors() {
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) return -1;
            CompoundTag display = stack.getTagElement("display");
            return display != null && display.contains("color", Tag.TAG_INT)
                    ? display.getInt("color")
                    : UNSEASONED;
        }, ItemRegistry.SPECIAL_STEW.get());
    }
}
