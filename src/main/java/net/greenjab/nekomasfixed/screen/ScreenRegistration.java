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
 * FMLClientSetupEvent#enqueueWork}. A client-only self-registering handler kept beside the screens
 * it registers, for the same reason {@code EntityAttributesAndSpawns} sits beside its entities.
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
