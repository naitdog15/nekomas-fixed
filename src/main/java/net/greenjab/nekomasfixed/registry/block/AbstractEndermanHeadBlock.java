package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.registry.block.entity.EndermanHeadBlockEntity;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
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
import javax.annotation.Nullable;

public abstract class AbstractEndermanHeadBlock extends BaseEntityBlock implements Equipable {
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public AbstractEndermanHeadBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	/** Wearable on the head, the same way every vanilla skull block is. */
	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
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
		return createTickerHelper(type, BlockEntityTypeRegistry.ENDERMAN_HEAD_BLOCK_ENTITY.get(), level.isClientSide()? null: EndermanHeadBlockEntity::tick);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getSignal(level, pos, direction) : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	public void setPower(Level level, BlockPos pos, BlockState state, int power) {
		state = state.setValue(AbstractEndermanHeadBlock.POWER, power);
		level.setBlock(pos, state, Block.UPDATE_ALL);
		updateNeighbors(state, level, pos);
	}
	public void updateNeighbors(BlockState state, Level level, BlockPos pos) {
		Direction direction = Direction.DOWN;
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(direction), this);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.is(newState.getBlock())) {
			this.notifyNeighborsOnRemoval(state, level, pos);
		}
		super.onRemove(state, level, pos, newState, moved);
	}

	/** Split out so the wall variant can notify along its own facing instead. */
	protected void notifyNeighborsOnRemoval(BlockState state, Level level, BlockPos pos) {
		if (state.getValue(POWER)>0) {
			this.updateNeighbors(state.setValue(POWER, 0), level, pos);
		}
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}
}
