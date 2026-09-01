package net.greenjab.nekomasfixed.registry.registries;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * {@code LootTableEvents.MODIFY} -&gt; Forge's
 * {@link LootTableLoadEvent} (forge bus, server-side only). Wire
 * {@code @SubscribeEvent public static void onLootTableLoad(LootTableLoadEvent event) {
 * LootTableAdditions.modify(event); }} into a forge-bus handler (e.g. ForgeBusEvents.java,
 * outside this package).
 * <p>
 * 26.2 -&gt; 1.20.1 API deltas exercised here: {@code EntityTypePredicate.of(lookup, type)} (Holder-
 * based) -&gt; plain {@code EntityTypePredicate.of(EntityType)}; {@code
 * EnchantRandomlyFunction.randomApplicableEnchantment(holder).withEnchantment(...)} (which no
 * longer takes a holder argument at all on 1.20.1 and expresses "any applicable enchantment", not
 * "exactly this one") -&gt; {@code EnchantRandomlyFunction.randomEnchantment().withEnchantment(Enchantment)}
 * (the actual "restrict to exactly one enchantment" builder); {@code
 * NestedLootTable.lootTableReference(ResourceKey)} -&gt; {@code
 * LootTableReference.lootTableReference(ResourceLocation)} (takes a plain ResourceLocation, hence
 * {@code .location()} on the ResourceKey constant); and Fabric's own mutable {@code tableBuilder}
 * callback argument -&gt; Forge's {@code LootTable#addPool(LootPool)}, one of Forge's own vanilla
 * patches (marked {@code FORGE END} in LootTable.java, guarded by the same {@code checkFrozen()}
 * the table is still under while this event fires) - {@code LootTableLoadEvent} hands you the real,
 * still-mutable table directly; there is no {@code LootTable#toBuilder()} to rebuild one with.
 */
public class LootTableAdditions {

    public static void modify(LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.CHARGED_CREEPER.location())) {
            LootItemCondition.Builder predicate = LootItemEntityPropertyCondition.hasProperties(
                    LootContext.EntityTarget.THIS,
                    EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityType.ENDERMAN)));
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootTableReference.lootTableReference(LootTableRegistry.SUPER_CHARGED_CREEPER_ENDERMAN_LOOT_TABLE.location()).when(predicate));
            event.getTable().addPool(poolBuilder.build());
        } else if (event.getName().equals(BuiltInLootTables.SHIPWRECK_TREASURE.location())) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ItemRegistry.BOAT_UPGRADE_TEMPLATE.get()))
                    .add(LootItem.lootTableItem(Items.AIR));
            event.getTable().addPool(poolBuilder.build());
        } else if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY.location())) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment().withEnchantment(EnchantmentRegistry.LEECHING.get())))
                    .add(LootItem.lootTableItem(Items.AIR));
            event.getTable().addPool(poolBuilder.build());
        }
    }
}
