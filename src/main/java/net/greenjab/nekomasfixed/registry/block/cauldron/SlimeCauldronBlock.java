package net.greenjab.nekomasfixed.registry.block.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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

public class SlimeCauldronBlock extends AbstractCauldronBlock {

    public static final IntegerProperty SLIME_LEVEL = IntegerProperty.create("slime_level", 1, 4);
    public static final int MAX_LEVEL = 4;

    public SlimeCauldronBlock(Properties settings) {
        super(settings, createBehaviorMap());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SLIME_LEVEL, MAX_LEVEL));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLIME_LEVEL);
    }

    // See HoneyCauldronBlock.java's javadoc on this exact pattern.
    public static final Map<Item, CauldronInteraction> SLIME = CauldronInteraction.newInteractionMap();

    private static Map<Item, CauldronInteraction> createBehaviorMap() {
        return SLIME;
    }

    /** See HoneyCauldronBlock.registerInteractions()'s javadoc. */
    public static void registerInteractions() {
        SLIME.put(Items.AIR, (state, level, pos, player, hand, stack) -> {
            if(state.getValue(SLIME_LEVEL) == MAX_LEVEL) {
                if (!level.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.SLIME_BLOCK)));
                    level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });

        SLIME.put(Items.SLIME_BALL, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(SLIME_LEVEL) < MAX_LEVEL) {
                if (!level.isClientSide()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.setBlockAndUpdate(pos, state.setValue(SLIME_LEVEL, state.getValue(SLIME_LEVEL) + 1));
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
            if (state.getValue(SLIME_LEVEL) < MAX_LEVEL) {
                level.setBlockAndUpdate(pos, state.setValue(SLIME_LEVEL, state.getValue(SLIME_LEVEL) + 1));
                level.playSound(null, pos, SoundEvents.SLIME_BLOCK_BREAK,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (4.0 + state.getValue(SLIME_LEVEL) * 3.0) / 16.0;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(SLIME_LEVEL) == MAX_LEVEL;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(SLIME_LEVEL);
    }
}