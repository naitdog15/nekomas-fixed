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
 * Currently empty, ahead of the rest of the client-side code landing here. Handlers expected to
 * land here eventually include: {@code EntityRenderersEvent.RegisterRenderers} (block-entity +
 * entity renderers), {@code EntityRenderersEvent.RegisterLayerDefinitions}, {@code
 * RegisterColorHandlersEvent.Block} (the soup-cauldron tint), and {@code
 * FMLClientSetupEvent#enqueueWork} for {@code MenuScreens.register(KILN / PYROTECHNICS, ...)} and
 * the Cloth Config screen extension point.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModBusClientEvents {
    private ModBusClientEvents() {
    }
}
