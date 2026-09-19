package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.network.SyncHandler;
import net.greenjab.nekomasfixed.network.ServerFlags;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.greenjab.nekomasfixed.util.HarnessHelper;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.registries.LootTableAdditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.greenjab.nekomasfixed.registry.entity.goal.AvoidTrustingOcelotGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * {@code @Mod.EventBusSubscriber(bus = FORGE)} registers by annotation when FML constructs the mod,
 * so {@link NekomasFixed}'s constructor must not also call
 * {@code MinecraftForge.EVENT_BUS.register(...)} here - doing both would fire every handler twice.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeBusEvents {
    private ForgeBusEvents() {
    }

    // vetoing here refuses the interaction rather than unregistering anything - the ghast's own mod
    // owns the harness equip. both interact events fire for a mob, hence two handlers.
    // a joining client has its own copy of the common config, and forge sends it nothing, so the
    // settings that both sides act on have to come down the wire.
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), ServerFlags.asPayload());
        }
    }

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

    // creepers already keep away from ocelots on their own, so every other monster is given the same goal -
    // but only for the trusting ones, the wild ones still get no respect
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMonsterJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Monster monster && !(monster instanceof Creeper)
                && monster.goalSelector.getAvailableGoals().stream().noneMatch(wrapped -> wrapped.getGoal() instanceof AvoidTrustingOcelotGoal)) {
            monster.goalSelector.addGoal(1, new AvoidTrustingOcelotGoal(monster));
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        LootTableAdditions.modify(event);
    }

    /**
     * only mobs spawning on their own are stopped - spawn eggs, spawners, reinforcements and
     * {@code /summon} all take other paths and are left alone, matching what a player expects.
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
