package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class WallEndermanHeadHead extends AbstractEndermanHeadBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION =
			RotatedShapes.horizontal(Block.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0));
	private static final Map<Direction, VoxelShape> SHAPES_POWERED_BY_DIRECTION =
			RotatedShapes.horizontal(Block.box(4.0, 1.5, 8.0, 12.0, 14.5, 16.0));

	public WallEndermanHeadHead(Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void notifyNeighborsOnRemoval(BlockState state, Level level, BlockPos pos) {
		if (state.getValue(POWER)>0) {
			this.updateNeighbors(state, level, pos);
		}
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction == state.getValue(FACING) ? state.getSignal(level, pos, direction) : 0;
	}

	@Override
	public void updateNeighbors(BlockState state, Level level, BlockPos pos) {
		Direction direction = state.getValue(FACING).getOpposite();
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(direction), this);
	}


	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(POWER)>0?SHAPES_POWERED_BY_DIRECTION.get(state.getValue(FACING)):SHAPES_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState blockState = super.getStateForPlacement(ctx);
		BlockGetter blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		Direction[] directions = ctx.getNearestLookingDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.setValue(FACING, direction2);
				if (!blockView.getBlockState(blockPos.relative(direction)).canBeReplaced(ctx)) {
					return blockState;
				}
			}
		}
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
}
