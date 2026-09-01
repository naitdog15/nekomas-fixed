package net.greenjab.nekomasfixed.registry.block.cauldron;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class IceCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<IceCauldronBlock> CODEC = simpleCodec(IceCauldronBlock::new);
    private static final VoxelShape ICE_SHAPE = Block.column(12.0, 4.0, 15.0);
    private static final VoxelShape INSIDE_COLLISION_SHAPE = Shapes.or(AbstractCauldronBlock.SHAPE, ICE_SHAPE);

    @Override
    public MapCodec<IceCauldronBlock> codec() {
        return CODEC;
    }

    public IceCauldronBlock(BlockBehaviour.Properties settings) {
        super(settings, createBehaviorMap());
    }

    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }

    // See HoneyCauldronBlock.java's javadoc on this exact pattern.
    public static final Map<Item, CauldronInteraction> ICE = CauldronInteraction.newInteractionMap();

    private static Map<Item, CauldronInteraction> createBehaviorMap() {
        return ICE;
    }

    /** See HoneyCauldronBlock.registerInteractions()'s javadoc. */
    public static void registerInteractions() {
        ICE.put(Items.AIR, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.ICE)));
                level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });
    }

    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
       handler.apply(InsideBlockEffectType.FREEZE);
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return INSIDE_COLLISION_SHAPE;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return 3;
    }
}