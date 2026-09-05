package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// tintIndex 0 is the bowl (left alone); other indices are the stew layer, tinted from the display
// color tag the same way a dyed item stores its color
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
