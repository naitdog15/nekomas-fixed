package net.greenjab.nekomasfixed.registry.registries;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * {@code LootTableEvents.MODIFY} -&gt; Forge's
 * {@link LootTableLoadEvent} (forge bus, server-side only). {@code ForgeBusEvents} is what calls
 * {@link #modify(LootTableLoadEvent)} from its own {@code @SubscribeEvent} handler.
 * <p>
 * 26.2 -&gt; 1.20.1 API deltas exercised here: {@code
 * EnchantRandomlyFunction.randomApplicableEnchantment(holder).withEnchantment(...)} (which no
 * longer takes a holder argument at all on 1.20.1 and expresses "any applicable enchantment", not
 * "exactly this one") -&gt; {@code EnchantRandomlyFunction.randomEnchantment().withEnchantment(Enchantment)}
 * (the actual "restrict to exactly one enchantment" builder); loot-table ids are plain
 * {@code ResourceLocation}s here, since {@code ResourceKey<LootTable>} does not exist yet; and
 * Fabric's own mutable {@code tableBuilder} callback argument -&gt; Forge's
 * {@code LootTable#addPool(LootPool)}, one of Forge's own vanilla patches (marked
 * {@code FORGE END} in LootTable.java, guarded by the same {@code checkFrozen()} the table is still
 * under while this event fires) - {@code LootTableLoadEvent} hands you the real, still-mutable table
 * directly; there is no {@code LootTable#toBuilder()} to rebuild one with.
 * <p>
 * The third addition - a charged creeper that kills an enderman dropping from
 * {@code LootTableRegistry.SUPER_CHARGED_CREEPER_ENDERMAN_LOOT_TABLE} - has nothing to hook here.
 * 1.20.1 has no charged-creeper loot table at all: mob-head drops are hard-coded per victim in
 * {@code Skeleton}/{@code Zombie}/{@code Creeper}'s own {@code dropCustomDeathLoot}, gated on
 * {@code Creeper#canDropMobsSkull()}, and {@code EnderMan} has no such branch to extend. Re-homing
 * it means a {@code LivingDropsEvent} handler that checks the killing blow for a powered creeper,
 * not a loot-table modification, so the table JSON stays in the datapack unreferenced until that
 * handler exists.
 */
public class LootTableAdditions {

    public static void modify(LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.SHIPWRECK_TREASURE)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ItemRegistry.BOAT_UPGRADE_TEMPLATE.get()))
                    .add(LootItem.lootTableItem(Items.AIR));
            event.getTable().addPool(poolBuilder.build());
        } else if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment().withEnchantment(EnchantmentRegistry.LEECHING.get())))
                    .add(LootItem.lootTableItem(Items.AIR));
            event.getTable().addPool(poolBuilder.build());
        }
    }
}
