package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.other.AnimalTooltipData;
import net.greenjab.nekomasfixed.registry.other.ContainerTooltipData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// ClientTooltipComponent.create() is a static interface method Forge patches to consult
// ClientTooltipComponentManager, filled from this event - without an entry here, create() throws
// IllegalArgumentException the first time an item hands back one of these payloads.
// lookup is by exact class (both payloads are final records); Dist.CLIENT keeps this off a dedicated server
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
