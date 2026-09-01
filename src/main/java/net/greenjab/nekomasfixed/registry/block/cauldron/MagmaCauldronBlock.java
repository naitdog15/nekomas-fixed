package net.greenjab.nekomasfixed.registry.block.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Map;

public class MagmaCauldronBlock extends AbstractCauldronBlock {

    public static final IntegerProperty MAGMA_LEVEL = IntegerProperty.create("magma_level", 1, 4);
    public static final int MAX_LEVEL = 4;

    public MagmaCauldronBlock(Properties settings) {
        super(settings, createBehaviorMap());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MAGMA_LEVEL, MAX_LEVEL));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultInstance();
    }

    /** {@code lavaHurt} already ignites and burns; the thaw is what the lava layer adds on top. */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.setTicksFrozen(0);
            entity.lavaHurt();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MAGMA_LEVEL);
    }

    // See HoneyCauldronBlock.java's javadoc on this exact pattern.
    public static final Map<Item, CauldronInteraction> MAGMA = CauldronInteraction.newInteractionMap();

    private static Map<Item, CauldronInteraction> createBehaviorMap() {
        return MAGMA;
    }

    /** See HoneyCauldronBlock.registerInteractions()'s javadoc. */
    public static void registerInteractions() {
        MAGMA.put(Items.AIR, (state, level, pos, player, hand, stack) -> {
            if(state.getValue(MAGMA_LEVEL) == MAX_LEVEL) {
                if (!level.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.MAGMA_BLOCK)));
                    level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });

        MAGMA.put(Items.MAGMA_CREAM, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(MAGMA_LEVEL) < MAX_LEVEL) {
                if (!level.isClientSide()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.setBlockAndUpdate(pos, state.setValue(MAGMA_LEVEL, state.getValue(MAGMA_LEVEL) + 1));
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) {
                if (state.getValue(MAGMA_LEVEL) < MAX_LEVEL) {
                    level.setBlockAndUpdate(pos, state.setValue(MAGMA_LEVEL, state.getValue(MAGMA_LEVEL) + 1));
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                }
        }
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (4.0 + state.getValue(MAGMA_LEVEL) * 3.0) / 16.0;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(MAGMA_LEVEL) == MAX_LEVEL;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(MAGMA_LEVEL);
    }

}
