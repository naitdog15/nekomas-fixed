package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** See {@link FlammableBlock}. Used for {@code baobab_stairs}. */
public class FlammableStairBlock extends StairBlock {
    private final int igniteOdds;
    private final int burnOdds;

    public FlammableStairBlock(BlockState baseState, BlockBehaviour.Properties settings, int igniteOdds, int burnOdds) {
        super(baseState, settings);
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
