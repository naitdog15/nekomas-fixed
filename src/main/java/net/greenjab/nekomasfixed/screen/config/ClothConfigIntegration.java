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
 * cloth_config is mandatory=false in mods.toml, so anything referencing Cloth types at the class
 * level (ConfigTrial imports me.shedaniel.clothconfig2.api.* directly) must never classload when
 * Cloth is absent - registerClothScreen is isolated behind the isLoaded check below so ConfigTrial
 * is only resolved once that branch runs.
 * client-only: FMLClientSetupEvent never fires on a dedicated server.
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

    private static void registerClothScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigTrial::createConfigScreen));
    }
}
