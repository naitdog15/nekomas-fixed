package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import javax.annotation.Nullable;

/**
 * Every 1.20.1 {@code Item} constructor calls {@code BuiltInRegistries.ITEM.createIntrusiveHolder(this)},
 * so a lazy, never-registered {@code StandingAndWallBlockItem} for clock placement would crash at
 * registry freeze - either immediately ("Registry is already frozen") if constructed after freeze,
 * or at freeze time ("Some intrusive holders were not registered") if constructed before it and
 * never registered. NO {@code Item} of any kind is instantiated anywhere in this class.
 * <p>
 * Reproduces {@code StandingAndWallBlockItem#getPlacementState}'s logic (attachmentDirection
 * = DOWN, the vanilla clock's own hardwired orientation) directly against the two clock {@link Block}
 * instances instead of through an {@code Item} subclass - the per-block placement logic itself
 * ({@code FloorClockBlock#getStateForPlacement}, {@code WallClockBlock#getStateForPlacement}/
 * {@code #canSurvive}) already lives on the block classes (both this package's own files) and needed
 * no Item involvement to begin with. The rest ({@link BlockItem#placeBlock}'s default body,
 * {@link BlockItem#updateCustomBlockEntityTag}) is called via BlockItem's own public STATIC helpers -
 * still zero Item instantiation.
 * <p>
 * Called from an {@code @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)} on
 * {@code Item} (gated on {@code (Object) this == Items.CLOCK}) - see
 * {@code ClientSyncHandler}/{@code SyncHandler} for the sibling networking piece; this class does not
 * touch that mixin file itself.
 */
public final class ClockPlacement {
    private ClockPlacement() {
    }

    public static InteractionResult place(UseOnContext ctx) {
        BlockPlaceContext context = new BlockPlaceContext(ctx);
        Block floorBlock = BlockRegistry.CLOCK.get();
        Block wallBlock = BlockRegistry.WALL_CLOCK.get();

        if (!floorBlock.isEnabled(context.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        }
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockState state = getPlacementState(context, floorBlock, wallBlock);
        if (state == null) {
            return InteractionResult.FAIL;
        }
        if (!placeBlock(context, state)) {
            return InteractionResult.FAIL;
        }

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState placedState = level.getBlockState(pos);
        if (placedState.is(state.getBlock())) {
            placedState = updateBlockStateFromTag(pos, level, stack, placedState);
            BlockItem.updateCustomBlockEntityTag(level, player, pos, stack);
            placedState.getBlock().setPlacedBy(level, pos, placedState, player, stack);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
            }
        }

        SoundType soundType = placedState.getSoundType(level, pos, player);
        level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * {@code StandingAndWallBlockItem#getPlacementState}'s exact loop (attachmentDirection = DOWN,
     * the vanilla clock's own hardwired orientation), reproduced against the two Block instances
     * directly.
     */
    @Nullable
    private static BlockState getPlacementState(BlockPlaceContext context, Block floorBlock, Block wallBlock) {
        BlockState wallState = wallBlock.getStateForPlacement(context);
        BlockState result = null;
        BlockPos pos = context.getClickedPos();
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction != Direction.UP) {
                BlockState candidate = direction == Direction.DOWN ? floorBlock.getStateForPlacement(context) : wallState;
                if (candidate != null && candidate.canSurvive(context.getLevel(), pos)) {
                    result = candidate;
                    break;
                }
            }
        }
        return result != null && context.getLevel().isUnobstructed(result, pos, CollisionContext.empty()) ? result : null;
    }

    /** {@code BlockItem#placeBlock}'s default body. */
    private static boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    /**
     * Vanilla {@code BlockItem#place} calls
     * {@code updateBlockStateFromTag} between placement and the follow-up block-entity NBT /
     * {@code setPlacedBy} / sound / game-event actions, applying any {@code BlockStateTag}
     * compound from the item's NBT (e.g. from {@code /give ... {BlockStateTag:{...}}}) to the
     * freshly placed state. This helper reproduces {@code BlockItem#updateBlockStateFromTag}
     * exactly (forge-1.20.1-mapped-src BlockItem.java:123-144) rather than exposing it via a
     * mixin invoker, since it needs no access to any private field — only the public
     * {@code StateDefinition}/{@code Property} API.
     */
    private static BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState placedState) {
        BlockState state = placedState;
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag blockStateTag = tag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = placedState.getBlock().getStateDefinition();

            for (String key : blockStateTag.getAllKeys()) {
                Property<?> property = stateDefinition.getProperty(key);
                if (property != null) {
                    String value = blockStateTag.get(key).getAsString();
                    state = updateState(state, property, value);
                }
            }
        }

        if (state != placedState) {
            level.setBlock(pos, state, 2);
        }

        return state;
    }

    /** {@code BlockItem#updateState}'s exact body. */
    private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }
}
