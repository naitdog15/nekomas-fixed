package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;

// loot tables aren't a registry on 1.20.1 - LootTables is a reload listener keyed by plain
// ResourceLocation (ResourceKey<LootTable> only exists from 1.21). BuiltInLootTables.LOCATIONS is
// just a vanilla-datagen completeness set, so nothing here needs to mutate it.
public class LootTableRegistry {
    public static final ResourceLocation SUPER_CHARGED_CREEPER_ENDERMAN_LOOT_TABLE =
            NekomasFixed.id("gameplay/super_charged_creeper_enderman");
    public static final ResourceLocation CLAM_LOOT_TABLE = NekomasFixed.id("gameplay/clam");
}
