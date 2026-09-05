package net.greenjab.nekomasfixed.registry.registries;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraftforge.event.LootTableLoadEvent;

// randomApplicableEnchantment(holder) doesn't exist on 1.20.1 - use randomEnchantment()
// .withEnchantment(Enchantment) to restrict to exactly one. LootTableLoadEvent hands you the
// real still-mutable table directly, no toBuilder() needed.
//
// the charged-creeper-kills-enderman addition has nothing to hook here: 1.20.1 hard-codes
// mob-head drops per victim with no branch for EnderMan, so that table's JSON stays unreferenced.
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
