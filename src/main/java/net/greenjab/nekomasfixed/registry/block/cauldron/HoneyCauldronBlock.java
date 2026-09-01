package net.greenjab.nekomasfixed.registry.block.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.item.Item;

import java.util.Map;

public class HoneyCauldronBlock extends AbstractCauldronBlock {

    public static final IntegerProperty HONEY_LEVEL = IntegerProperty.create("honey_level", 1, 4);
    public static final int MAX_LEVEL = 4;

    public HoneyCauldronBlock(Properties settings) {
        super(settings, createBehaviorMap());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HONEY_LEVEL, MAX_LEVEL));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL);
    }

    // CauldronInteraction.Dispatcher/CauldronInteractions.ID_MAPPER (26.x) have
    // no 1.20.1 equivalent - CauldronInteraction.newInteractionMap() (the exact static factory
    // vanilla's own EMPTY/WATER/LAVA/POWDER_SNOW maps use) replaces both. Content is populated by
    // registerInteractions(), not here - see its own javadoc.
    public static final Map<Item, CauldronInteraction> HONEY = CauldronInteraction.newInteractionMap();

    private static Map<Item, CauldronInteraction> createBehaviorMap() {
        return HONEY;
    }

    /**
     * Called from {@code FMLCommonSetupEvent#enqueueWork} by {@code CauldronBehaviour.register()},
     * not eagerly at class-load/field-initializer time.
     */
    public static void registerInteractions() {
        HONEY.put(Items.AIR, (state, level, pos, player, hand, stack) -> {
            if(state.getValue(HONEY_LEVEL) == MAX_LEVEL) {
                if (!level.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.HONEY_BLOCK)));
                    level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        });

        HONEY.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.HONEY_BOTTLE)));
                if (state.getValue(HONEY_LEVEL) > 1) level.setBlockAndUpdate(pos, state.setValue(HONEY_LEVEL, state.getValue(HONEY_LEVEL) - 1));
                else  level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());

                level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });

        HONEY.put(Items.HONEY_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(HONEY_LEVEL) < MAX_LEVEL) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(HONEY_LEVEL, state.getValue(HONEY_LEVEL) + 1));
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return InteractionResult.SUCCESS;
        });

    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity) && entity instanceof LivingEntity living) {
            living.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3*20), living);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) {
            boolean hasBeehive = isBeeHiveAbove(pos, level);
            if (hasBeehive) {
                int currentLevel = state.getValue(HONEY_LEVEL);
                if (currentLevel < MAX_LEVEL) {
                    level.setBlockAndUpdate(pos, state.setValue(HONEY_LEVEL, currentLevel + 1));
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    private boolean isBeeHiveAbove(BlockPos pos, Level level) {
        BlockPos abovePos = new BlockPos(pos.getX(), pos.getY() + 2, pos.getZ());
        Block block = level.getBlockState(abovePos).getBlock();
        return block == Blocks.BEEHIVE || block == Blocks.BEE_NEST;
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return (4.0 + state.getValue(HONEY_LEVEL) * 3.0) / 16.0;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(HONEY_LEVEL) == MAX_LEVEL;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(HONEY_LEVEL);
    }
}