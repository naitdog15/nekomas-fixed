package net.greenjab.nekomasfixed.registry.block.enums;

import net.greenjab.nekomasfixed.registry.block.HollowLogBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public enum HollowLogType {

    OAK(Blocks.OAK_LOG, BlockRegistry.HOLLOW_OAK_LOG),
    SPRUCE(Blocks.SPRUCE_LOG, BlockRegistry.HOLLOW_SPRUCE_LOG),
    BIRCH(Blocks.BIRCH_LOG, BlockRegistry.HOLLOW_BIRCH_LOG),
    JUNGLE(Blocks.JUNGLE_LOG, BlockRegistry.HOLLOW_JUNGLE_LOG),
    ACACIA(Blocks.ACACIA_LOG, BlockRegistry.HOLLOW_ACACIA_LOG),
    DARK_OAK(Blocks.DARK_OAK_LOG, BlockRegistry.HOLLOW_DARK_OAK_LOG),
    MANGROVE(Blocks.MANGROVE_LOG, BlockRegistry.HOLLOW_MANGROVE_LOG),
    CHERRY(Blocks.CHERRY_LOG, BlockRegistry.HOLLOW_CHERRY_LOG),
    BAMBOO(Blocks.BAMBOO_BLOCK, BlockRegistry.HOLLOW_BAMBOO_BLOCK),
    CRIMSON(Blocks.CRIMSON_HYPHAE, BlockRegistry.HOLLOW_CRIMSON_STEM),
    WARPED(Blocks.WARPED_HYPHAE, BlockRegistry.HOLLOW_WARPED_STEM),
    STRIPPED_OAK(Blocks.STRIPPED_OAK_LOG, BlockRegistry.STRIPPED_HOLLOW_OAK_LOG),
    STRIPPED_SPRUCE(Blocks.STRIPPED_SPRUCE_LOG, BlockRegistry.STRIPPED_HOLLOW_SPRUCE_LOG),
    STRIPPED_BIRCH(Blocks.STRIPPED_BIRCH_LOG, BlockRegistry.STRIPPED_HOLLOW_BIRCH_LOG),
    STRIPPED_JUNGLE(Blocks.STRIPPED_JUNGLE_LOG, BlockRegistry.STRIPPED_HOLLOW_JUNGLE_LOG),
    STRIPPED_ACACIA(Blocks.STRIPPED_ACACIA_LOG, BlockRegistry.STRIPPED_HOLLOW_ACACIA_LOG),
    STRIPPED_DARK_OAK(Blocks.STRIPPED_DARK_OAK_LOG, BlockRegistry.STRIPPED_HOLLOW_DARK_OAK_LOG),
    STRIPPED_MANGROVE(Blocks.STRIPPED_MANGROVE_LOG, BlockRegistry.STRIPPED_HOLLOW_MANGROVE_LOG),
    STRIPPED_CHERRY(Blocks.STRIPPED_CHERRY_LOG, BlockRegistry.STRIPPED_HOLLOW_CHERRY_LOG),
    STRIPPED_BAMBOO(Blocks.STRIPPED_BAMBOO_BLOCK, BlockRegistry.STRIPPED_HOLLOW_BAMBOO_BLOCK),
    STRIPPED_CRIMSON(Blocks.STRIPPED_CRIMSON_HYPHAE, BlockRegistry.STRIPPED_HOLLOW_CRIMSON_STEM),
    STRIPPED_WARPED(Blocks.STRIPPED_WARPED_HYPHAE, BlockRegistry.STRIPPED_HOLLOW_WARPED_STEM);

    private final Supplier<Block> baseLog;
    private final Supplier<Block> hollowLog;

    /** Built on first lookup — the mod's own blocks are not resolvable while this enum class-loads. */
    private static Map<Block, Block> baseToHollow;

    HollowLogType(Block baseLog, Supplier<Block> hollowLog) {
        this(() -> baseLog, hollowLog);
    }

    HollowLogType(Supplier<Block> baseLog, Supplier<Block> hollowLog) {
        this.baseLog = baseLog;
        this.hollowLog = hollowLog;
    }

    private static Map<Block, Block> baseToHollow() {
        if (baseToHollow == null) {
            Map<Block, Block> map = new HashMap<>();
            for (HollowLogType type : values()) {
                map.put(type.baseLog.get(), type.hollowLog.get());
            }
            baseToHollow = map;
        }
        return baseToHollow;
    }

    private static Map<Block, Block> hollowToStripped;

    private static Map<Block, Block> hollowToStripped() {
        if (hollowToStripped == null) {
            Map<Block, Block> map = new HashMap<>();
            map.put(BlockRegistry.HOLLOW_OAK_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_OAK_LOG.get());
            map.put(BlockRegistry.HOLLOW_SPRUCE_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_SPRUCE_LOG.get());
            map.put(BlockRegistry.HOLLOW_BIRCH_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_BIRCH_LOG.get());
            map.put(BlockRegistry.HOLLOW_JUNGLE_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_JUNGLE_LOG.get());
            map.put(BlockRegistry.HOLLOW_ACACIA_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_ACACIA_LOG.get());
            map.put(BlockRegistry.HOLLOW_DARK_OAK_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_DARK_OAK_LOG.get());
            map.put(BlockRegistry.HOLLOW_MANGROVE_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_MANGROVE_LOG.get());
            map.put(BlockRegistry.HOLLOW_CHERRY_LOG.get(), BlockRegistry.STRIPPED_HOLLOW_CHERRY_LOG.get());
            map.put(BlockRegistry.HOLLOW_BAMBOO_BLOCK.get(), BlockRegistry.STRIPPED_HOLLOW_BAMBOO_BLOCK.get());
            map.put(BlockRegistry.HOLLOW_CRIMSON_STEM.get(), BlockRegistry.STRIPPED_HOLLOW_CRIMSON_STEM.get());
            map.put(BlockRegistry.HOLLOW_WARPED_STEM.get(), BlockRegistry.STRIPPED_HOLLOW_WARPED_STEM.get());
            hollowToStripped = map;
        }
        return hollowToStripped;
    }

    /** The stripped hollow log a hollow log turns into under an axe, or null when it is not one. */
    @Nullable
    public static Block getStrippedBlock(Block hollow) {
        return hollowToStripped().get(hollow);
    }

    public static Block getHollowBlock(Block baseLog) {
        return baseToHollow().getOrDefault(baseLog, Blocks.AIR);
    }

    public static BlockState getHollowState(BlockState baseLog) {
        BlockState hollowState = getHollowBlock(baseLog.getBlock()).defaultBlockState();
        // The axis value is read off the source state, so the source is the one that has to be asked -
        // a future mapping from a block that is not a rotated pillar would otherwise throw here.
        if (!hollowState.is(Blocks.AIR) && baseLog.hasProperty(RotatedPillarBlock.AXIS)) {
            return hollowState.setValue(HollowLogBlock.AXIS, baseLog.getValue(RotatedPillarBlock.AXIS));
        }
        return hollowState;
    }

    public static boolean hasHollowVariant(BlockState baseLog) {
        return !getHollowState(baseLog).is(Blocks.AIR);
    }
}
