package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class WallClockBlock extends AbstractClockBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION =
			RotatedShapes.horizontal(Block.box(1.0, 1.0, 15.0, 15.0, 15.0, 16.0));

	public WallClockBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void notifyNeighborsOnRemoval(BlockState state, Level level, BlockPos pos) {
		if (state.getValue(POWERED)) {
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

	public void addParticle(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Direction dir = state.getValue(FACING);
		double d = pos.getX() + 0.5 + (dir.getAxis()==Direction.Axis.Z?(random.nextDouble() - 0.5) * 0.4 : -dir.getStepX()*0.4);
		double e = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
		double f = pos.getZ() + 0.5 + (dir.getAxis()==Direction.Axis.X?(random.nextDouble() - 0.5) * 0.4 : -dir.getStepZ()*0.4);
		level.addParticle(DustParticleOptions.REDSTONE, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canPlaceAt(level, pos, state.getValue(FACING));
	}

	@Override
	public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
		return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
	}

	public static boolean canPlaceAt(LevelReader level, BlockPos pos, Direction facing) {
		BlockPos blockPos = pos.relative(facing.getOpposite());
		BlockState blockState = level.getBlockState(blockPos);
		return blockState.isFaceSturdy(level, blockPos, facing);
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
