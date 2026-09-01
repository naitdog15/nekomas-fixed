package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;

/**
 * Loot tables are not a registry on 1.20.1 - {@code LootTables} is a reload listener keyed by plain
 * {@code ResourceLocation}, and {@code ResourceKey<LootTable>} only exists from 1.21 onwards. These
 * are therefore the ids of the two datapack tables under
 * {@code data/nekomasfixed/loot_tables/gameplay/}, ready to hand straight to
 * {@code LootTables#get(ResourceLocation)} or {@code LootTableReference.lootTableReference(...)}.
 * <p>
 * {@code BuiltInLootTables.LOCATIONS} is a vanilla-datagen completeness set (its only consumer is
 * the vanilla loot-table provider); a resource-pack-defined loot table works without being added to
 * it, so nothing here mutates it.
 */
public class LootTableRegistry {
    public static final ResourceLocation SUPER_CHARGED_CREEPER_ENDERMAN_LOOT_TABLE =
            NekomasFixed.id("gameplay/super_charged_creeper_enderman");
    public static final ResourceLocation CLAM_LOOT_TABLE = NekomasFixed.id("gameplay/clam");
}
