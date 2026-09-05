package net.greenjab.nekomasfixed.datagen;

import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Stream;

/** vanilla's BlockLootSubProvider constructor needs an explosion-resistant item set (empty, none of
 * these are) and a FeatureFlagSet, and getKnownBlocks() for its dangling-loot-table validation. */
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
