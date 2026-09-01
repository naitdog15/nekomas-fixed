package net.greenjab.nekomasfixed;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

/**
 * The client-only mod-bus event holder — {@code value = Dist.CLIENT} means FML
 * never even loads this class on a dedicated server, which is why the client rendering/registration
 * events belong here rather than behind a manual {@code DistExecutor} gate (prefer
 * "unreachable from a server path" over DistExecutor — {@code FMLClientSetupEvent} and the
 * {@code EntityRenderersEvent} family simply never fire server-side).
 * <p>
 * Empty, because every client handler it could hold subscribes from the class that owns it instead:
 * {@code EntityRenderersEvent.RegisterRenderers}/{@code RegisterLayerDefinitions} from the registry
 * holders under {@code registries/**}, {@code RegisterColorHandlersEvent.Block} (the soup-cauldron
 * tint) from {@link NekomasFixedClient}, {@code MenuScreens.register(...)} from
 * {@code screen/ScreenRegistration}, and the Cloth Config screen from
 * {@code screen/config/ClothConfigIntegration}. Kept as the client-side counterpart to
 * {@link ModBusEvents} for anything with nowhere better to live.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModBusClientEvents {
    private ModBusClientEvents() {
    }
}
