package net.greenjab.nekomasfixed.screen.config;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.CompatMods;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@code cloth_config}
 * is declared {@code mandatory=false} in {@code mods.toml}, so classes that reference Cloth types at
 * the CLASS level (like {@code ConfigTrial}, which imports {@code me.shedaniel.clothconfig2.api.*}
 * directly) must never be classloaded when Cloth is absent. The registration call itself - the only
 * place anything needs to reference {@code ConfigTrial} - is isolated behind an explicit {@code
 * ModList.get().isLoaded(CompatMods.CLOTH_CONFIG)} check, so {@code ConfigTrial::createConfigScreen}
 * (and therefore Cloth's own classes, transitively) is only ever resolved when that branch actually
 * executes. {@code ConfigTrial} keeps exactly the shape that needs:
 * {@code public static Screen createConfigScreen(Screen)}, matching {@code
 * ConfigScreenHandler.ConfigScreenFactory}'s {@code Function<Screen,Screen>} convenience constructor.
 * Client-only ({@code FMLClientSetupEvent} never fires on a dedicated server - no
 * {@code @OnlyIn} needed beyond the event-subscriber's own {@code value = Dist.CLIENT}).
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClothConfigIntegration {
    private ClothConfigIntegration() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded(CompatMods.CLOTH_CONFIG)) {
            registerClothScreen();
        }
    }

    // Kept in its own method so ConfigTrial (and, transitively, Cloth Config's own classes) is never
    // referenced - and therefore never resolved/classloaded - unless that check passed.
    private static void registerClothScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigTrial::createConfigScreen));
    }
}
