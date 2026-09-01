package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.registry.registries.LootTableAdditions;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The common (both-dist) Forge-bus event holder.
 * {@code @Mod.EventBusSubscriber(bus = FORGE)} performs the registration by annotation when FML
 * constructs the mod, which is why {@link NekomasFixed}'s constructor does not also call
 * {@code MinecraftForge.EVENT_BUS.register(...)} by hand — doing both would fire every handler here
 * twice (confirmed no class in this mod does both).
 * <p>
 * This class carries the Forge-side replacement for the Fabric {@code LootTableEvents.MODIFY} hook.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeBusEvents {
    private ForgeBusEvents() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        LootTableAdditions.modify(event);
    }
}
