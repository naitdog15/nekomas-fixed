package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

/** See {@link FlammableBlock}. Used for {@code baobab_fence_gate}. */
public class FlammableFenceGateBlock extends FenceGateBlock {
    private final int igniteOdds;
    private final int burnOdds;

    public FlammableFenceGateBlock(WoodType woodType, BlockBehaviour.Properties settings, int igniteOdds, int burnOdds) {
        super(woodType, settings);
        this.igniteOdds = igniteOdds;
        this.burnOdds = burnOdds;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return igniteOdds;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return burnOdds;
    }
}
