package net.greenjab.nekomasfixed.registry.block;

import com.mojang.serialization.MapCodec;
import net.greenjab.nekomasfixed.registry.block.entity.StackedCakeBlockEntity;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.ScheduledTickAccess;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackedCakeBlock extends AbstractCandleBlock implements EntityBlock {
    public static final IntegerProperty SLICES = IntegerProperty.create("slices", 1, 21);
    public static final BooleanProperty CANDLE = BooleanProperty.create("candle");
    public static final MapCodec<StackedCakeBlock> CODEC = simpleCodec(StackedCakeBlock::new);
    private static final Map<Integer, VoxelShape[]> SHAPES_BY_BITES_AND_LAYER = new HashMap<>();
    private static final Map<Integer, VoxelShape> CANDLE_SHAPES = new HashMap<>();

    public StackedCakeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(SLICES, 7).setValue(CANDLE, false).setValue(LIT, false));
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        int HEIGHT = ((state.getValue(SLICES)-1)/7)+1;
        return List.of((new Vec3(8.0F, 8f + 8f * HEIGHT - ((2 * (HEIGHT-1)) - (HEIGHT >= 3 ? -2 : 0)), 8.0F)).scale(0.0625F));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLICES, CANDLE, LIT);
    }

    @Override
    public MapCodec<StackedCakeBlock> codec(){return CODEC;}

    static {
        for (int layer = 0; layer < 3; layer++) {
            double scale = 1-0.2*layer;
            double yMinT = 0;
            if (layer == 1) yMinT = 8;
            if (layer == 2) yMinT = 8 + 8*(1-0.2);
            final double yMin = yMinT;
            final double yMax = yMin + 8 * scale;
            SHAPES_BY_BITES_AND_LAYER.put(layer, Block.boxes(6, slices -> Block.box(8+(7 - (slices+1) * 2)*scale, yMin, 8-7*scale, 8+7*scale, yMax, 8+7*scale)));
            CANDLE_SHAPES.put(layer, Block.box(7, yMax, 7, 9, yMax+6, 9));
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    protected InteractionResult tryEat(Level world, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            if (state.getValue(CANDLE)) {
                if (world.getBlockEntity(pos) instanceof StackedCakeBlockEntity blockEntity) {
                    popResource(world, pos.above(), blockEntity.CANDLE_STATE.getBlock().asItem().getDefaultInstance());
                    blockEntity.CANDLE_STATE = Blocks.AIR.defaultBlockState();
                    blockEntity.setChanged();
                }
                state = state.setValue(CANDLE, false).setValue(LIT, false);
            }

            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(2, 0.1F);
            world.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if(world.getBlockEntity(pos) instanceof StackedCakeBlockEntity blockEntity){
                int i = state.getValue(SLICES)-1;
                int h = i/7;
                BlockState currentState = state;
                if (h==1) currentState = blockEntity.LAYER_2_STATE;
                if (h==2) currentState = blockEntity.LAYER_3_STATE;

                if(!currentState.hasProperty(StackedCakeBlock.SLICES)) return InteractionResult.FAIL;
                world.gameEvent(player, GameEvent.EAT, pos);
                if (i%7==0) {
                    if (h==1) blockEntity.LAYER_2_STATE=Blocks.AIR.defaultBlockState();
                    else if (h==2) blockEntity.LAYER_3_STATE=Blocks.AIR.defaultBlockState();
                    if (i==0) world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    else world.setBlock(pos, state.setValue(SLICES,i), 3);
                } else {
                    if (h==1) blockEntity.LAYER_2_STATE=currentState.setValue(SLICES,i%7);
                    else if (h==2) blockEntity.LAYER_3_STATE=currentState.setValue(SLICES,i%7);
                    world.setBlock(pos, state.setValue(SLICES,i), 3);
                }
                blockEntity.setChanged();
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int h = (state.getValue(SLICES)-1)/7;
        int s = (state.getValue(SLICES)-1)%7;
        if (state.getValue(CANDLE)){
            if (h==0) return Shapes.or(SHAPES_BY_BITES_AND_LAYER.get(0)[s], CANDLE_SHAPES.get(0));
            else if (h==1) return Shapes.or(SHAPES_BY_BITES_AND_LAYER.get(0)[6], SHAPES_BY_BITES_AND_LAYER.get(1)[s], CANDLE_SHAPES.get(1));
            else if (h==2) return Shapes.or(SHAPES_BY_BITES_AND_LAYER.get(0)[6], SHAPES_BY_BITES_AND_LAYER.get(1)[6], SHAPES_BY_BITES_AND_LAYER.get(2)[s], CANDLE_SHAPES.get(2));
        } else {
            if (h == 0) return SHAPES_BY_BITES_AND_LAYER.get(0)[s];
            else if (h == 1) return Shapes.or(SHAPES_BY_BITES_AND_LAYER.get(0)[6], SHAPES_BY_BITES_AND_LAYER.get(1)[s]);
            else if (h == 2) return Shapes.or(SHAPES_BY_BITES_AND_LAYER.get(0)[6], SHAPES_BY_BITES_AND_LAYER.get(1)[6], SHAPES_BY_BITES_AND_LAYER.get(2)[s]);
        }
        return SHAPES_BY_BITES_AND_LAYER.get(0)[s];
    }

    public void addCakeLayer(ItemStack stack, StackedCakeBlockEntity entity, BlockState state){
        int h = (state.getValue(SLICES)-1)/7;
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (h==0) entity.LAYER_2_STATE=blockItem.getBlock().defaultBlockState();
            else entity.LAYER_3_STATE=blockItem.getBlock().defaultBlockState();

            entity.setChanged();
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        else if (world.getBlockEntity(pos) instanceof StackedCakeBlockEntity stackedCakeBlockEntity){
            if (state.getValue(SLICES) == 7 || state.getValue(SLICES) == 14 || state.getValue(SLICES) == 21) {
                if (player.getMainHandItem().is(ModTags.STACKED_CAKES) && state.getValue(SLICES) != 21) {
                    this.addCakeLayer(stack, stackedCakeBlockEntity, state);
                    world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(SLICES, state.getValue(SLICES)+7));
                    world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    stack.consume(1, player);
                    return InteractionResult.SUCCESS;
                } else if (player.getMainHandItem().is(ItemTags.CANDLES)) {
                    if (!state.getValue(CANDLE)) {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            BlockState candleState = blockItem.getBlock().defaultBlockState();
                            stackedCakeBlockEntity.CANDLE_STATE = candleState.setValue(CandleBlock.LIT, false);
                            world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(CANDLE, true).setValue(CandleBlock.LIT, false));
                            world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                            stack.consume(1, player);
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else if (stack.is(Items.FLINT_AND_STEEL)) {
                    if (state.getValue(CANDLE)) {
                        BlockState candleState = stackedCakeBlockEntity.CANDLE_STATE;
                        if (!candleState.getValue(CandleBlock.LIT)) {
                            stackedCakeBlockEntity.CANDLE_STATE = candleState.setValue(CandleBlock.LIT, true);
                            stackedCakeBlockEntity.setChanged();
                            world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(LIT, true));
                            stack.hurtAndBreak(1, player, hand);
                            world.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return tryEat(world, pos, state, player);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StackedCakeBlockEntity(pos, state);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return state.getValue(SLICES);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
}
