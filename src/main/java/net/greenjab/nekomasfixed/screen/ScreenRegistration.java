package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@code MenuScreens.register(KILN / PYROTECHNICS, ...)}, from {@code
 * FMLClientSetupEvent#enqueueWork}. Client-only self-registering handler, in this package's own
 * {@code screen/**} package (see EntityAttributesAndSpawns.java's javadoc for why a dedicated
 * {@code @Mod.EventBusSubscriber} class is used instead of editing {@code
 * ModBusClientEvents.java} directly - same reasoning applies here).
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ScreenRegistration {
    private ScreenRegistration() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ScreenHandlerRegistry.KILN.get(), KilnScreen::new);
            MenuScreens.register(ScreenHandlerRegistry.PYROTECHNICS.get(), PyrotechnicsTableScreen::new);
        });
    }
}
