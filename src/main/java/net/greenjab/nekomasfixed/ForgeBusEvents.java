package net.greenjab.nekomasfixed;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.greenjab.nekomasfixed.util.HarnessHelper;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.registries.LootTableAdditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The common (both-dist) Forge-bus event holder.
 * {@code @Mod.EventBusSubscriber(bus = FORGE)} performs the registration by annotation when FML
 * constructs the mod, which is why {@link NekomasFixed}'s constructor does not also call
 * {@code MinecraftForge.EVENT_BUS.register(...)} by hand — doing both would fire every handler here
 * twice (confirmed no class in this mod does both).
 * <p>
 * This class carries the loot-table modifications this mod makes to vanilla tables, and the switch
 * that stops its own mobs spawning on their own.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeBusEvents {
    private ForgeBusEvents() {
    }

    // The ghast's own mod handles wearing a harness, so switching them off here means refusing the
    // interaction rather than unregistering anything. Both interact events fire for a mob, hence two.
    @SubscribeEvent
    public static void onHarnessInteract(PlayerInteractEvent.EntityInteract event) {
        vetoHarness(event, event.getItemStack());
    }

    @SubscribeEvent
    public static void onHarnessInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        vetoHarness(event, event.getItemStack());
    }

    private static void vetoHarness(PlayerInteractEvent event, net.minecraft.world.item.ItemStack stack) {
        if (NekomasFixedConfig.HARNESSES.get()) return;
        if (HarnessHelper.isModHarness(stack)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        LootTableAdditions.modify(event);
    }

    /**
     * The natural-spawn switch. Only mobs trying to spawn on their own are stopped - spawn eggs,
     * spawners, reinforcements and {@code /summon} all take other paths and are left alone, which is
     * what a player expects the switch to mean.
     */
    @SubscribeEvent
    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) return;
        if (NekomasFixedConfig.NATURAL_MOB_SPAWNS.get()) return;

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntityType());
        if (id != null && NekomasFixed.NAMESPACE.equals(id.getNamespace())) {
            event.setResult(Event.Result.DENY);
        }
    }
}
