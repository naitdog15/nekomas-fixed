package net.greenjab.nekomasfixed.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.nekomasfixed.registry.block.entity.EndermanHeadBlockEntity;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import javax.annotation.Nullable;

public abstract class AbstractEndermanHeadBlock extends BaseEntityBlock {
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	@Override
	public abstract MapCodec<? extends AbstractEndermanHeadBlock> codec();

	public AbstractEndermanHeadBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(POWER, 0);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EndermanHeadBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityTypeRegistry.ENDERMAN_HEAD_BLOCK_ENTITY, level.isClientSide()? null: EndermanHeadBlockEntity::tick);
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getSignal(level, pos, direction) : 0;
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	public void setPower(Level level, BlockPos pos, BlockState state, int power) {
		state = state.setValue(AbstractEndermanHeadBlock.POWER, power);
		level.setBlock(pos, state, Block.UPDATE_ALL);
		updateNeighbors(state, level, pos);
	}
	public void updateNeighbors(BlockState state, Level level, BlockPos pos) {
		Direction direction = Direction.DOWN;
		Orientation wireOrientation = ExperimentalRedstoneUtils.initialOrientation(
				level, direction, Direction.UP
		);
		level.updateNeighborsAt(pos, this, wireOrientation);
		level.updateNeighborsAt(pos.relative(direction), this, wireOrientation);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
		if (state.getValue(POWER)>0) {
			this.updateNeighbors(state.setValue(POWER, 0), level, pos);
		}
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}
}
