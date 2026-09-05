package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.registry.block.entity.HollowLogBlockEntity;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class HollowLogBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock{
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);
    // two-thick bark on the four faces around the log's own axis, leaving the ends open
    private static final VoxelShape SIDE_DOWN = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    private static final VoxelShape SIDE_UP = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SIDE_WEST = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
    private static final VoxelShape SIDE_EAST = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SIDE_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    private static final VoxelShape SIDE_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    /** what a stored block fills the bore with */
    private static final VoxelShape CORE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);

    private static final Map<Direction.Axis, VoxelShape> SHAPES_BY_AXIS = Maps.newEnumMap(Map.of(
            Direction.Axis.X, Shapes.or(SIDE_DOWN, SIDE_UP, SIDE_NORTH, SIDE_SOUTH),
            Direction.Axis.Y, Shapes.or(SIDE_WEST, SIDE_EAST, SIDE_NORTH, SIDE_SOUTH),
            Direction.Axis.Z, Shapes.or(SIDE_DOWN, SIDE_UP, SIDE_WEST, SIDE_EAST)
    ));
    private static final Map<Direction.Axis, VoxelShape> SHAPES_BY_AXIS_FILLED = Maps.newEnumMap(Map.of(
            Direction.Axis.X, Shapes.or(SHAPES_BY_AXIS.get(Direction.Axis.X), CORE),
            Direction.Axis.Y, Shapes.or(SHAPES_BY_AXIS.get(Direction.Axis.Y), CORE),
            Direction.Axis.Z, Shapes.or(SHAPES_BY_AXIS.get(Direction.Axis.Z), CORE)
    ));
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty SOLID_INSIDE = BooleanProperty.create("filled");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HollowLogBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(SOLID_INSIDE, false).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /** The log itself is a baked model; the block entity only draws whatever is stuffed inside it. */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return changeRotation(state, rotation);
    }
    public static BlockState changeRotation(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS, WATERLOGGED, LIGHT_LEVEL, SOLID_INSIDE);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity logBE) {
            if (!(logBE.getStoredBlock()==Blocks.AIR.defaultBlockState()||isGlassLike(logBE.getStoredStack()))) return false;
        }
        return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity logBE) {
            if (!(logBE.getStoredBlock()==Blocks.AIR.defaultBlockState()||isGlassLike(logBE.getStoredStack()))) return false;
        }
        return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
    }

    /** Water still shows through a pane or a block of glass stuffed in the bore, nothing else. */
    private static boolean isGlassLike(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getPath().contains("glass");
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SOLID_INSIDE)? SHAPES_BY_AXIS_FILLED.get(state.getValue(AXIS)):SHAPES_BY_AXIS.get(state.getValue(AXIS));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
       return new HollowLogBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HollowLogBlockEntity logBE) {
                if (stack.getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock().defaultBlockState().is(BlockTags.FLOWERS) && logBE.getStoredBlock().is(BlockTags.FLOWER_POTS)) {
                        // FLOWERS tag membership doesn't guarantee a FlowerPotBlock mapping (tall flowers are
                        // unpottable) - the null branch below is intentional, never collapse to .get(key).get().
                        Block plant = blockItem.getBlock();
                        java.util.function.Supplier<? extends Block> pottedSupplier =
                                ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().get(ForgeRegistries.BLOCKS.getKey(plant));
                        if (pottedSupplier != null) {
                            Block potted = pottedSupplier.get();
                            logBE.setStoredBlock(stack.copyWithCount(1), potted.defaultBlockState());
                            if (!player.getAbilities().instabuild) stack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    if (HollowLogBlockEntity.canStoreBlock(logBE, blockItem, state.getValue(AXIS)== Direction.Axis.Y)) {
                        logBE.setStoredBlock(stack.copyWithCount(1), blockItem.getBlock().defaultBlockState());
                        if (!player.getAbilities().instabuild) stack.shrink(1);
                        level.sendBlockUpdated(pos, state, state, 3);
                        if (state.getValue(AXIS)== Direction.Axis.Y)
                            state = state.setValue(SOLID_INSIDE, true);
                        if (!isGlassLike(stack))
                            state = state.setValue(WATERLOGGED, false);
                        if (blockItem.getBlock().defaultBlockState().getLightEmission() > 0)
                            state = state.setValue(LIGHT_LEVEL, blockItem.getBlock().defaultBlockState().getLightEmission());
                        level.setBlockAndUpdate(pos, state);

                        return InteractionResult.SUCCESS;
                    }
                } else if (stack.getItem() instanceof Item) {
                    if (stack.is(Items.SHEARS)) {
                        if (logBE.getStoredBlock()!=Blocks.AIR.defaultBlockState())
                            stack.hurtAndBreak(1, player, holder -> holder.broadcastBreakEvent(hand));
                        if (logBE.getStoredBlock().is(BlockTags.FLOWER_POTS)&&!logBE.getStoredStack().is(Items.FLOWER_POT)) popResource(serverLevel, pos, Items.FLOWER_POT.getDefaultInstance());
                        popResource(serverLevel, pos, logBE.getStoredStack());
                        logBE.setStoredBlock(ItemStack.EMPTY, Blocks.AIR.defaultBlockState());
                        level.setBlockAndUpdate(pos, state.setValue(LIGHT_LEVEL, 0).setValue(SOLID_INSIDE, false));
                        level.sendBlockUpdated(pos, state, state, 3);
                        return InteractionResult.SUCCESS;
                    }
                }
                logBE.setChanged();
            }
        } else {
            if (stack.is(Items.BUCKET) || stack.is(Items.WATER_BUCKET))
               return super.use(state, level, pos, player, hand, hit);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> list = super.getDrops(state, builder);
        if (blockEntity instanceof HollowLogBlockEntity hollowLogBlockEntity) {
            if (hollowLogBlockEntity.getStoredBlock().is(BlockTags.FLOWER_POTS) && !hollowLogBlockEntity.getStoredBlock().is(Blocks.FLOWER_POT))
                list.add(Items.FLOWER_POT.getDefaultInstance());
        }
        return list;
    }
}
