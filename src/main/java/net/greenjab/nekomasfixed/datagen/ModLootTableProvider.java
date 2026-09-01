package net.greenjab.nekomasfixed.datagen;

import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Stream;

/**
 * {@code FabricBlockLootSubProvider} → vanilla {@code BlockLootSubProvider},
 * wired into Forge's {@code LootTableProvider.SubProviderEntry} list by
 * {@code NekomasFixedDataGenerator}. Vanilla's constructor needs the explosion-resistant item set
 * (empty here — none of these blocks are explosion-resistant) and a {@code FeatureFlagSet}; it also
 * requires {@link #getKnownBlocks()} for its own dangling-loot-table validation, which 26.2's Fabric
 * wrapper didn't need.
 */
public class ModLootTableProvider extends BlockLootSubProvider {
    public ModLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        BlockDyeMap.BRICKS.values().forEach(this::dropSelf);
        BlockDyeMap.BRICK_SLAB.values().forEach(this::dropSelf);
        BlockDyeMap.BRICK_STAIRS.values().forEach(this::dropSelf);
        BlockDyeMap.BRICK_WALL.values().forEach(this::dropSelf);
        BlockDyeMap.SPOTTED_WOOL.values().forEach(this::dropSelf);
        BlockDyeMap.SPOTTED_CARPET.values().forEach(this::dropSelf);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.of(BlockDyeMap.BRICKS, BlockDyeMap.BRICK_SLAB, BlockDyeMap.BRICK_STAIRS,
                        BlockDyeMap.BRICK_WALL, BlockDyeMap.SPOTTED_WOOL, BlockDyeMap.SPOTTED_CARPET)
                .flatMap(map -> map.values().stream())
                .toList();
    }
}
