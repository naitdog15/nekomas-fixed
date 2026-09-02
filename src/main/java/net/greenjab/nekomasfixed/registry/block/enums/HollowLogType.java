package net.greenjab.nekomasfixed.registry.block.enums;

import net.greenjab.nekomasfixed.registry.block.HollowLogBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
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
    MANGROVE(Blocks.CHERRY_LOG, BlockRegistry.HOLLOW_MANGROVE_LOG),
    CHERRY(Blocks.CHERRY_LOG, BlockRegistry.HOLLOW_CHERRY_LOG),
    BAMBOO(Blocks.BAMBOO_BLOCK, BlockRegistry.HOLLOW_BAMBOO_BLOCK),
    CRIMSON(Blocks.CRIMSON_HYPHAE, BlockRegistry.HOLLOW_CRIMSON_STEM),
    WARPED(Blocks.WARPED_HYPHAE, BlockRegistry.HOLLOW_WARPED_STEM);

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

    public static Block getHollowBlock(Block baseLog) {
        return baseToHollow().getOrDefault(baseLog, Blocks.AIR);
    }

    public static BlockState getHollowState(BlockState baseLog) {
        BlockState hollowState = getHollowBlock(baseLog.getBlock()).defaultBlockState();
        if (!hollowState.is(Blocks.AIR) && hollowState.hasProperty(RotatedPillarBlock.AXIS)) {
            return hollowState.setValue(HollowLogBlock.AXIS, baseLog.getValue(RotatedPillarBlock.AXIS));
        }
        return hollowState;
    }
}
