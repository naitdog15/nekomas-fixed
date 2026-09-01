package net.greenjab.nekomasfixed.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import javax.annotation.Nullable;

public class BaobabFruitBlock extends Block implements BonemealableBlock {
    public static final MapCodec<BaobabFruitBlock> CODEC = simpleCodec(BaobabFruitBlock::new);
    private static final VoxelShape SHAPE_AGE_0 = Block.box(7, 6, 7, 9, 9, 9);
    private static final VoxelShape SHAPE_AGE_1 = Block.box(5, 0, 5, 11, 9, 11);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 1);

    public BaobabFruitBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AGE) == 0 ? SHAPE_AGE_0 : SHAPE_AGE_1;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 1;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRandom().nextInt(2) == 0) {
            int rope = 0;
            for (; rope < 8; rope++) {
                if (!level.getBlockState(pos.above(rope + 1)).is(BlockRegistry.ROPE)) break;
            }
            if (level.getBlockState(pos.above(rope + 1)).is(BlockTags.LEAVES)) {
                if (level.getBlockState(pos.below()).is(BlockTags.REPLACEABLE)) {
                    if (rope > 3) {
                        if (level.getRandom().nextInt(9 - rope) == 0)
                            level.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_CLIENTS);
                        else {
                            level.setBlock(pos, BlockRegistry.ROPE.defaultBlockState().setValue(RopeBlock.ATTACHED, true), Block.UPDATE_CLIENTS);
                            level.setBlock(pos.below(), state.setValue(AGE, 0), Block.UPDATE_CLIENTS);
                        }
                    } else {
                        level.setBlock(pos, BlockRegistry.ROPE.defaultBlockState().setValue(RopeBlock.ATTACHED, true), Block.UPDATE_CLIENTS);
                        level.setBlock(pos.below(), state.setValue(AGE, 0), Block.UPDATE_CLIENTS);
                    }
                } else level.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockRegistry.ROPE) || level.getBlockState(pos.above()).is(BlockTags.LEAVES) ;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState();
        LevelReader level = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        if(canSurvive(blockState, level, blockPos)) {
            return this.defaultBlockState().setValue(AGE, 1);
        }

        return null;
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 1;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if(this.defaultBlockState().getValue(AGE) < 1){
            level.setBlock(pos, state.setValue(AGE, 1), 2);
        }
    }

    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return ItemRegistry.BAOBAB_FRUIT.getDefaultInstance();
    }
}
