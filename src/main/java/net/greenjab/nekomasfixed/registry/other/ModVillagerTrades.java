package net.greenjab.nekomasfixed.registry.other;

import java.util.List;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The trades this mod adds to villagers who already have a profession.
 *
 * <p>There is one: a journeyman fisherman buys pearls. Pearls are what the clams along the ocean
 * floor give up, so the fisherman is the villager who would know what one is worth — three of them
 * for an emerald, twelve times over, and it is a well-paid piece of work for him.
 *
 * <p>The offer is gathered once per profession as a world starts, so this runs long after the
 * registries are filled and the item behind {@code PEARL} is always there by then. The level key is
 * the trade tier a villager has to have reached, counting novice as 1, and the game guarantees a list
 * exists for each of the five; the check below is only there so a mod that rewrote the map cannot
 * take this one down with it.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModVillagerTrades {

    /** Journeyman — the third of the five trade tiers. */
    private static final int JOURNEYMAN = 3;

    private static final int PEARLS_PER_EMERALD = 3;
    private static final int MAX_USES = 12;
    private static final int VILLAGER_XP = 30;

    /**
     * How much cheaper the trade gets for a player the village likes, and dearer for one it does not.
     * The same figure every "sell me a stack of something" trade in the game uses.
     */
    private static final float REPUTATION_DISCOUNT = 0.05F;

    private ModVillagerTrades() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.FISHERMAN) return;

        List<VillagerTrades.ItemListing> journeyman = event.getTrades().get(JOURNEYMAN);
        if (journeyman == null) return;

        journeyman.add(new BasicItemListing(
                new ItemStack(ItemRegistry.PEARL.get(), PEARLS_PER_EMERALD),
                new ItemStack(Items.EMERALD),
                MAX_USES,
                VILLAGER_XP,
                REPUTATION_DISCOUNT));
    }
}
