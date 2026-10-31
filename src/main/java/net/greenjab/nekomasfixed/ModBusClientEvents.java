package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.render.other.TintedGlint;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * {@code value = Dist.CLIENT} means FML never loads this class on a dedicated server, so client
 * rendering/registration events belong here rather than behind a manual {@code DistExecutor} gate.
 * holds only the reload listener that drops the tinted-glint texture cache; every other client handler
 * subscribes from the class that owns it instead:
 * {@code EntityRenderersEvent} handlers under {@code registries/**}, {@code
 * RegisterColorHandlersEvent.Block} from {@link NekomasFixedClient}, {@code
 * MenuScreens.register(...)} from {@code screen/ScreenRegistration}, and the Cloth Config screen
 * from {@code screen/config/ClothConfigIntegration}.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModBusClientEvents {
    private ModBusClientEvents() {
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> TintedGlint.clearCache());
    }
}
