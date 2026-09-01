package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * {@code BuiltInLootTables.LOCATIONS} is a vanilla-datagen completeness set (its only consumer is
 * {@code VanillaLootTableProvider}); mutating it is neither required for a resource-pack-defined
 * loot table to work nor something {@code HashSet.add} would even throw on if duplicated. These are
 * plain {@code ResourceKey<LootTable>} constants, exactly what {@code LootTableAdditions}' own
 * {@code NestedLootTable.lootTableReference(...)} call needs - no registry mutation of any kind.
 */
public class LootTableRegistry {
    public static final ResourceKey<LootTable> SUPER_CHARGED_CREEPER_ENDERMAN_LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, NekomasFixed.id("gameplay/super_charged_creeper_enderman"));
    public static final ResourceKey<LootTable> CLAM_LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, NekomasFixed.id("gameplay/clam"));
}
