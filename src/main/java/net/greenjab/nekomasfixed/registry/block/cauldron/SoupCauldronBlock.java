package net.greenjab.nekomasfixed.registry.block.cauldron;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.greenjab.nekomasfixed.registry.block.entity.SoupCauldronBlockEntity;
import net.greenjab.nekomasfixed.registry.item.SpecialSoupItem;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class SoupCauldronBlock extends BaseEntityBlock implements EntityBlock {

    private static final VoxelShape RAYCAST_SHAPE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = Util.make(
             () -> Shapes.join(
                     Shapes.block(),
                     Shapes.or(
                             Block.box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                             Block.box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                             Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                             RAYCAST_SHAPE
                     ),
                     BooleanOp.ONLY_FIRST
             )
    );

    public SoupCauldronBlock(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        Random random = new Random();
        if (!(level.getBlockEntity(pos) instanceof SoupCauldronBlockEntity be)) {
            return InteractionResult.FAIL;
        } else if (stack.is(Items.STICK) && (level.getBlockState(pos.below()).is(BlockTags.FIRE) || level.getBlockState(pos.below()).is(BlockTags.CAMPFIRES))) {
            if(be.hasStirred){return InteractionResult.FAIL;}
            be.setStirred(level);
            if (level.isClientSide()) for (int i = 0; i < 4; i++) level.addAlwaysVisibleParticle(ParticleTypes.POOF, true, pos.getX()+(0.5 + (random.nextDouble())*(random.nextBoolean()?1:-1)), pos.getY() + 1.0 , pos.getZ()+0.5+(random.nextDouble() * (random.nextBoolean()?1:-1)), 0.001  * (random.nextBoolean()?1:-1), 0.0001, 0.001 *  (random.nextBoolean()?1:-1));
            return InteractionResult.SUCCESS;
        } else if ((FOOD_COLORS.containsKey(stack.getItem())) && (level.getBlockState(pos.below()).is(BlockTags.FIRE) || level.getBlockState(pos.below()).is(BlockTags.CAMPFIRES)) ) {
            if(be.hasStirred){return InteractionResult.FAIL;}
            if(be.getInputs().size()>=4){return InteractionResult.FAIL;}
            if (!level.isClientSide()) {
                if (be.addInput(stack) && !player.getAbilities().instabuild) stack.shrink(1);
            }
            level.updateNeighbourForOutputSignal(pos, this);
            return InteractionResult.SUCCESS;
        } else if(stack.is(Items.BOWL)){
            if(!be.hasStirred){return InteractionResult.FAIL;}
            ItemStack soup = new ItemStack(ItemRegistry.SPECIAL_STEW.get());
            List<ItemStack> copiedInputs = be.getInputs().stream().map(ItemStack::copy).toList();
            StackData.writeOrRemove(soup, SpecialSoupItem.KEY_INGREDIENTS, SpecialSoupItem.INGREDIENTS_CODEC, copiedInputs, List.of());
            // vanilla's dyed-item tag shape, so any tint layer can read the blend straight off the stack
            soup.getOrCreateTagElement("display").putInt("color", blendFoodColors(copiedInputs));
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(stack, player, soup));
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            for (ItemStack ingredient : copiedInputs) {
                ItemStack remainder = ingredient.getCraftingRemainingItem();
                if (!remainder.isEmpty()) Block.popResource(level, pos, remainder);
            }
            return InteractionResult.SUCCESS;
        } else if(stack.is(Items.AIR)){
            if(be.hasStirred){return InteractionResult.FAIL;}
            player.setItemInHand(hand, be.removeInput());
        }
        return InteractionResult.FAIL;
    }

    /** The cauldron plus its tinted broth is the baked model; the block entity only animates it. */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoupCauldronBlockEntity(pos, state);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? createTickerHelper(type, BlockEntityTypeRegistry.SOUP_CAULDRON_BLOCK_ENTITY.get(), SoupCauldronBlockEntity::clientTick) : null;
    }

    public static SoupCauldronBlock.PropertyRetriever< Float2FloatFunction> getAnimationProgressRetriever(LidBlockEntity progress) {
        return () -> progress::getOpenNess;
    }
    public interface PropertyRetriever<T> {
        T getFallback();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int hunger = 0;
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof SoupCauldronBlockEntity soupCauldronBlockEntity) {
            for (ItemStack item : soupCauldronBlockEntity.getInputs()) {
                SimpleContainer container = new SimpleContainer(item);
                Optional<SmeltingRecipe> optional = level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, container, level);
                if (optional.isPresent() && !item.is(Items.CHORUS_FRUIT)) {
                    ItemStack itemStack = optional.get().assemble(container, level.registryAccess());
                    if (!itemStack.isEmpty()) item=itemStack;
                }
                FoodProperties food = item.getFoodProperties(null);
                if (food != null) hunger += Mth.ceil(food.getNutrition()/2f);
            }
        }
        return hunger;
    }

    public static final Map<Item, Integer> FOOD_COLORS = new HashMap<>(Map.ofEntries(
            Map.entry(Items.POTION, 0x385DC6),
            Map.entry(Items.APPLE, 0xFC1C2A),
            Map.entry(Items.GOLDEN_APPLE, 0xE7EB56),
            Map.entry(Items.ENCHANTED_GOLDEN_APPLE, 0xE7EB56),
            Map.entry(Items.MELON_SLICE, 0xBD3023),
            Map.entry(Items.SWEET_BERRIES, 0xA30700),
            Map.entry(Items.GLOW_BERRIES, 0xF4DF6A),
            Map.entry(Items.CHORUS_FRUIT, 0x8C668B),
            Map.entry(Items.CARROT, 0xFC8C09),
            Map.entry(Items.GOLDEN_CARROT, 0xE7EB56),
            Map.entry(Items.POTATO, 0xD6A850),
            Map.entry(Items.BAKED_POTATO, 0xD6A850),
            Map.entry(Items.BEETROOT, 0xA2272B),
            Map.entry(Items.KELP, 0x3B3224),
            Map.entry(Items.DRIED_KELP, 0x3B3224),
            Map.entry(Items.BEEF, 0x703E2C),
            Map.entry(Items.COOKED_BEEF, 0x703E2C),
            Map.entry(Items.PORKCHOP, 0xD0BE86),
            Map.entry(Items.COOKED_PORKCHOP, 0xD0BE86),
            Map.entry(Items.MUTTON, 0x804739),
            Map.entry(Items.COOKED_MUTTON, 0x804739),
            Map.entry(Items.CHICKEN, 0xCB7C49),
            Map.entry(Items.COOKED_CHICKEN, 0xCB7C49),
            Map.entry(Items.RABBIT, 0xCF8C61),
            Map.entry(Items.COOKED_RABBIT, 0xCF8C61),
            Map.entry(Items.COD, 0xD3C3AB),
            Map.entry(Items.COOKED_COD, 0xD3C3AB),
            Map.entry(Items.SALMON, 0xB84E23),
            Map.entry(Items.COOKED_SALMON, 0xB84E23),
            Map.entry(Items.TROPICAL_FISH, 0xF16E20),
            Map.entry(Items.MILK_BUCKET, 0xFCFCFC),
            Map.entry(Items.HONEY_BOTTLE, 0xFC8F16)
    ));

    public static Optional<Integer> getFoodColor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Optional.empty();

        if (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
            return Optional.of(PotionUtils.getColor(stack));
        }

        Integer foodColor = FOOD_COLORS.get(stack.getItem());
        if (foodColor != null) return Optional.of(foodColor);

        return Optional.empty();
    }

    public static int blendFoodColors(List<ItemStack> items) {
        int totalR = 0;
        int totalG = 0;
        int totalB = 0;
        int totalWeight = 0;

        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;
            Optional<Integer> colorOpt = getFoodColor(stack);
            if (colorOpt.isEmpty()) continue;
            int color = colorOpt.get();
            totalR += color >> 16 & 255;
            totalG += color >> 8 & 255;
            totalB += color & 255;
            totalWeight++;
        }
        if (totalWeight == 0) return 0x385DC6;

        int r = totalR / totalWeight;
        int g = totalG / totalWeight;
        int b = totalB / totalWeight;

        return (r << 16) | (g << 8) | b;
    }
}
