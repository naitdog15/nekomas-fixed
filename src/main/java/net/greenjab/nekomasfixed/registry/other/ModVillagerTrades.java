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

// runs once per profession at world start, well after registries are filled; the null check below
// only guards against another mod rewriting the trade-tier map
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModVillagerTrades {

    /** Journeyman — the third of the five trade tiers. */
    private static final int JOURNEYMAN = 3;

    private static final int PEARLS_PER_EMERALD = 3;
    private static final int MAX_USES = 12;
    private static final int VILLAGER_XP = 30;

    // reputation discount magnitude - matches every other bulk "sell a stack" trade in vanilla
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
