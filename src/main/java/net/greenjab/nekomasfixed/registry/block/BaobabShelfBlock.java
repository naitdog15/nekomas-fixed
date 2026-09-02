package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.compat.vanillabackport.BackportedContent;
import net.greenjab.nekomasfixed.registry.block.entity.BaobabShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A shelf of baobab planks that puts three items on show. Right-clicking a slot swaps whatever is in
 * that slot for what the hand is holding; feed the shelf redstone and it swaps a whole row of the
 * hotbar at once, spilling across up to three shelves standing side by side and facing the same way.
 *
 * <p>Neighbouring shelves record where they sit in that row through {@link ChainPart}, which is only
 * ever anything but {@link ChainPart#UNCONNECTED} while the shelf is powered - that is what lets the
 * front face be drawn as one continuous run rather than three separate boards.
 */
public class BaobabShelfBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ChainPart> SIDE_CHAIN = EnumProperty.create("side_chain", ChainPart.class);

    /** One row of three slots, and at most three shelves swapping a hotbar row between them. */
    private static final int COLUMNS = 3;
    private static final int MAX_CHAIN = 3;

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 11.0, 16.0, 16.0, 13.0),
            Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 11.0, 16.0, 4.0, 13.0));
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(3.0, 12.0, 0.0, 5.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0),
            Block.box(3.0, 0.0, 0.0, 5.0, 4.0, 16.0));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 3.0, 16.0, 16.0, 5.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0),
            Block.box(0.0, 0.0, 3.0, 16.0, 4.0, 5.0));
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(11.0, 12.0, 0.0, 13.0, 16.0, 16.0),
            Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(11.0, 0.0, 0.0, 13.0, 4.0, 16.0));

    public BaobabShelfBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(SIDE_CHAIN, ChainPart.UNCONNECTED)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, SIDE_CHAIN, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaobabShelfBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(pos))
                .setValue(WATERLOGGED, context.getLevel().getFluidState(pos).is(Fluids.WATER));
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
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.getValue(POWERED)) {
            this.joinRow(level, pos, state, oldState);
        } else {
            this.leaveRow(level, pos, state);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        if (level.getBlockEntity(pos) instanceof Container container) {
            Containers.dropContents(level, pos, container);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
        this.leaveRow(level, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide()) return;
        boolean signal = level.hasNeighborSignal(pos);
        if (state.getValue(POWERED) == signal) return;

        BlockState newState = state.setValue(POWERED, signal);
        if (!signal) newState = newState.setValue(SIDE_CHAIN, ChainPart.UNCONNECTED);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        playShelfSound(level, pos, signal
                ? BackportedContent.SHELF_ACTIVATE.orElse(SoundEvents.WOODEN_BUTTON_CLICK_ON)
                : BackportedContent.SHELF_DEACTIVATE.orElse(SoundEvents.WOODEN_BUTTON_CLICK_OFF));
        level.gameEvent(signal ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos,
                GameEvent.Context.of(newState));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof BaobabShelfBlockEntity shelf)) return InteractionResult.PASS;

        OptionalInt slot = hitSlot(hit, state.getValue(FACING));
        if (slot.isEmpty()) return InteractionResult.SUCCESS;

        Inventory inventory = player.getInventory();
        if (level.isClientSide()) {
            return inventory.getSelected().isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }

        if (!state.getValue(POWERED)) {
            ItemStack held = player.getItemInHand(hand);
            boolean tookSomething = swapOne(held, player, shelf, slot.getAsInt(), inventory);
            if (!tookSomething && held.isEmpty()) return InteractionResult.PASS;
            playShelfSound(level, pos, tookSomething
                    ? (held.isEmpty()
                            ? BackportedContent.SHELF_TAKE_ITEM.orElse(SoundEvents.ITEM_FRAME_REMOVE_ITEM)
                            : BackportedContent.SHELF_SINGLE_SWAP.orElse(SoundEvents.ITEM_FRAME_ROTATE_ITEM))
                    : BackportedContent.SHELF_PLACE_ITEM.orElse(SoundEvents.ITEM_FRAME_ADD_ITEM));
            return InteractionResult.SUCCESS;
        }

        if (!swapHotbar(level, pos, inventory)) return InteractionResult.CONSUME;
        playShelfSound(level, pos, BackportedContent.SHELF_MULTI_SWAP.orElse(SoundEvents.ITEM_FRAME_ROTATE_ITEM));
        return InteractionResult.SUCCESS;
    }

    /** Swaps the held stack for whatever sits in one slot. True when the shelf gave something back. */
    private boolean swapOne(ItemStack held, Player player, BaobabShelfBlockEntity shelf, int slot, Inventory inventory) {
        ItemStack taken = shelf.swapItem(slot, held);
        // In creative the hand keeps its stack rather than being emptied by a slot that had nothing.
        ItemStack replacement = player.getAbilities().instabuild && taken.isEmpty() ? held.copy() : taken;
        inventory.setItem(inventory.selected, replacement);
        inventory.setChanged();
        shelf.setChanged(GameEvent.ITEM_INTERACT_FINISH);
        return !taken.isEmpty();
    }

    /**
     * Trades the right-hand end of the hotbar against every shelf in the row, so a three-shelf run
     * swaps nine slots at once and the order on the wall matches the order in the bar.
     */
    private boolean swapHotbar(Level level, BlockPos pos, Inventory inventory) {
        List<BlockPos> row = this.rowAt(level, pos);
        if (row.isEmpty()) return false;

        boolean swapped = false;
        for (int shelfIndex = 0; shelfIndex < row.size(); shelfIndex++) {
            if (!(level.getBlockEntity(row.get(shelfIndex)) instanceof BaobabShelfBlockEntity shelf)) continue;
            for (int slot = 0; slot < shelf.getContainerSize(); slot++) {
                int hotbarSlot = 9 - (row.size() - shelfIndex) * shelf.getContainerSize() + slot;
                if (hotbarSlot < 0 || hotbarSlot >= inventory.getContainerSize()) continue;
                ItemStack fromHotbar = inventory.removeItemNoUpdate(hotbarSlot);
                ItemStack fromShelf = shelf.swapItem(slot, fromHotbar);
                if (!fromHotbar.isEmpty() || !fromShelf.isEmpty()) {
                    inventory.setItem(hotbarSlot, fromShelf);
                    swapped = true;
                }
            }
            inventory.setChanged();
            shelf.setChanged(GameEvent.ENTITY_INTERACT);
        }
        return swapped;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) return 0;
        if (!(level.getBlockEntity(pos) instanceof BaobabShelfBlockEntity shelf)) return 0;
        int signal = 0;
        for (int slot = 0; slot < shelf.getContainerSize(); slot++) {
            if (!shelf.getItem(slot).isEmpty()) signal |= 1 << slot;
        }
        return signal;
    }

    /** Which of the three slots the click landed on, or empty when it did not land on the front. */
    private static OptionalInt hitSlot(BlockHitResult hit, Direction facing) {
        if (hit.getDirection() != facing) return OptionalInt.empty();
        BlockPos front = hit.getBlockPos().relative(facing);
        Vec3 local = hit.getLocation().subtract(front.getX(), front.getY(), front.getZ());
        float across = switch (facing) {
            case NORTH -> (float) (1.0 - local.x);
            case SOUTH -> (float) local.x;
            case WEST -> (float) local.z;
            case EAST -> (float) (1.0 - local.z);
            default -> -1.0F;
        };
        if (across < 0.0F) return OptionalInt.empty();
        return OptionalInt.of(Mth.clamp(Mth.floor(across * COLUMNS), 0, COLUMNS - 1));
    }

    private static void playShelfSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    // --- side-by-side rows -------------------------------------------------------------------

    private boolean isConnectable(BlockState state) {
        return state.is(this) && state.getValue(POWERED);
    }

    /** The chain part of a shelf that could join this row, or null if that block cannot. */
    @Nullable
    private ChainPart partAt(LevelAccessor level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        if (!this.isConnectable(state) || state.getValue(FACING) != facing) return null;
        return state.getValue(SIDE_CHAIN);
    }

    /** Every shelf in the row containing {@code pos}, ordered left to right as the row is faced. */
    private List<BlockPos> rowAt(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!this.isConnectable(state)) return List.of();
        Direction facing = state.getValue(FACING);
        LinkedList<BlockPos> row = new LinkedList<>();
        row.add(pos);
        this.walkRow(level, pos, facing, facing.getClockWise(), ChainPart.LEFT, row::addFirst);
        this.walkRow(level, pos, facing, facing.getCounterClockWise(), ChainPart.RIGHT, row::add);
        return new ArrayList<>(row);
    }

    private void walkRow(LevelAccessor level, BlockPos from, Direction facing, Direction step,
                         ChainPart towards, Consumer<BlockPos> collect) {
        for (int distance = 1; distance < MAX_CHAIN; distance++) {
            BlockPos pos = from.relative(step, distance);
            ChainPart part = this.partAt(level, pos, facing);
            if (part == null) return;
            if (part.connectsTowards(towards)) collect.accept(pos);
            if (part.isRowEnd()) return;
        }
    }

    /** Powering up: take the free ends on either side, up to the three-shelf limit. */
    private void joinRow(LevelAccessor level, BlockPos pos, BlockState state, BlockState oldState) {
        if (!this.isConnectable(state)) return;
        boolean alreadyJoined = state.getValue(SIDE_CHAIN).isConnected();
        boolean wasJoined = this.isConnectable(oldState) && oldState.getValue(SIDE_CHAIN).isConnected();
        // A neighbour is already rewriting this row; letting both ends drive it would double up.
        if (alreadyJoined || wasJoined) return;

        Direction facing = state.getValue(FACING);
        BlockPos left = pos.relative(facing.getClockWise());
        BlockPos right = pos.relative(facing.getCounterClockWise());
        int lengthLeft = this.partAt(level, left, facing) != null ? this.rowAt(level, left).size() : 0;
        int lengthRight = this.partAt(level, right, facing) != null ? this.rowAt(level, right).size() : 0;

        ChainPart part = ChainPart.UNCONNECTED;
        int length = 1;
        if (lengthLeft > 0 && length + lengthLeft <= MAX_CHAIN) {
            part = part.whenJoinedOnTheLeft();
            this.updatePart(level, left, facing, ChainPart::whenJoinedOnTheRight);
            length += lengthLeft;
        }
        if (lengthRight > 0 && length + lengthRight <= MAX_CHAIN) {
            part = part.whenJoinedOnTheRight();
            this.updatePart(level, right, facing, ChainPart::whenJoinedOnTheLeft);
        }
        this.setPart(level, pos, part);
    }

    /** Powering down or being broken: tell both sides they have lost a neighbour. */
    private void leaveRow(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        this.updatePart(level, pos.relative(facing.getClockWise()), facing, ChainPart::whenLeftOnTheRight);
        this.updatePart(level, pos.relative(facing.getCounterClockWise()), facing, ChainPart::whenLeftOnTheLeft);
    }

    private void updatePart(LevelAccessor level, BlockPos pos, Direction facing, UnaryOperator<ChainPart> change) {
        ChainPart part = this.partAt(level, pos, facing);
        if (part != null) this.setPart(level, pos, change.apply(part));
    }

    private void setPart(LevelAccessor level, BlockPos pos, ChainPart part) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this) && state.getValue(SIDE_CHAIN) != part) {
            level.setBlock(pos, state.setValue(SIDE_CHAIN, part), Block.UPDATE_ALL);
        }
    }

    /** Where a shelf sits in a powered row of up to three, which is what picks its front face. */
    public enum ChainPart implements StringRepresentable {
        UNCONNECTED("unconnected"),
        RIGHT("right"),
        CENTER("center"),
        LEFT("left");

        private final String name;

        ChainPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public boolean isConnected() {
            return this != UNCONNECTED;
        }

        public boolean connectsTowards(ChainPart end) {
            return this == CENTER || this == end;
        }

        public boolean isRowEnd() {
            return this != CENTER;
        }

        public ChainPart whenJoinedOnTheRight() {
            return switch (this) {
                case UNCONNECTED, LEFT -> LEFT;
                case RIGHT, CENTER -> CENTER;
            };
        }

        public ChainPart whenJoinedOnTheLeft() {
            return switch (this) {
                case UNCONNECTED, RIGHT -> RIGHT;
                case CENTER, LEFT -> CENTER;
            };
        }

        public ChainPart whenLeftOnTheRight() {
            return switch (this) {
                case UNCONNECTED, LEFT -> UNCONNECTED;
                case RIGHT, CENTER -> RIGHT;
            };
        }

        public ChainPart whenLeftOnTheLeft() {
            return switch (this) {
                case UNCONNECTED, RIGHT -> UNCONNECTED;
                case CENTER, LEFT -> LEFT;
            };
        }
    }
}
