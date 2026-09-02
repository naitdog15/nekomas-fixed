package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.other.AnimalTooltipData;
import net.greenjab.nekomasfixed.registry.other.ContainerTooltipData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Teaches the tooltip renderer how to draw the mod's two tooltip payloads.
 * <p>
 * {@code ClientTooltipComponent.create(TooltipComponent)} is the dispatch point, and it is a
 * <i>static interface method</i>: it cannot be injected into, so the payload-to-renderer mapping is
 * registered through Forge's own hook instead. Forge patches that method to consult
 * {@code ClientTooltipComponentManager} for anything it does not recognise natively, and the manager
 * is filled from {@link RegisterClientTooltipComponentFactoriesEvent} on the mod event bus. Without
 * an entry here, {@code create} throws {@code IllegalArgumentException("Unknown TooltipComponent")}
 * the first time an item hands back one of these payloads.
 * <p>
 * The lookup is by <b>exact class</b>, not {@code instanceof} — both payloads are records, so they
 * are final and can never be subclassed into a missed entry.
 * <p>
 * {@code value = Dist.CLIENT} keeps this class off a dedicated server entirely: everything it names
 * below the payload records is client-only rendering code, and FML decides from the annotation
 * without loading the class.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModTooltipComponents {

    private ModTooltipComponents() {
    }

    @SubscribeEvent
    public static void registerFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ContainerTooltipData.class, ContainerTooltipComponent::new);
        event.register(AnimalTooltipData.class, data -> new AnimalTooltipComponent(data.contents()));
    }
}
