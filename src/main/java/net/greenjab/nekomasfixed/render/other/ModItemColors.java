package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.alchemy.Potion;
import net.greenjab.nekomasfixed.util.ColorMerger;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.world.item.alchemy.PotionUtils;
import java.util.List;
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
        // layer1 (the head) takes tintindex 1; layer0 is the plain shaft
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) return -1;
            List<Potion> potions = StackData.readTippedPotions(stack);
            int[] colors = new int[potions.size()];
            for (int i = 0; i < colors.length; i++) colors[i] = PotionUtils.getColor(potions.get(i));
            return 0xFF000000 | ColorMerger.mix(colors);
        }, ItemRegistry.TIPPED_ARROW_CUSTOM.get());
    }
}
