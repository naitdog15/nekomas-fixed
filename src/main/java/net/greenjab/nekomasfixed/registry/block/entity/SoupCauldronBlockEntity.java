package net.greenjab.nekomasfixed.registry.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.util.SoupCauldronAnimator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoupCauldronBlockEntity extends BlockEntity implements LidBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ItemStack> inputs = new ArrayList<>();
    public boolean hasStirred = false;
    private final SoupCauldronAnimator CookingAnimator = new SoupCauldronAnimator();


    public SoupCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.SOUP_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean addInput(ItemStack stack) {
        if (stack.is(Items.AIR)) return false;
        setChanged();

        if (!hasPotionContents(stack)) for (ItemStack existing : inputs) if (ItemStack.isSameItem(existing, stack)) return false;

        if (inputs.size() < 4) {
            inputs.add(stack.copyWithCount(1));
            if(level != null && !level.isClientSide()) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        return true;
    }

    /** Two potions are the same item but a different brew, so they are allowed to stack up in the pot. */
    private static boolean hasPotionContents(ItemStack stack) {
        return PotionUtils.getPotion(stack) != Potions.EMPTY || !PotionUtils.getCustomEffects(stack).isEmpty();
    }

    public ItemStack removeInput() {
        if (inputs.isEmpty()) return Items.AIR.getDefaultInstance();
        setChanged();

        ItemStack removed = inputs.remove(inputs.size() - 1);
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            // Emptied of ingredients it is no longer a soup cauldron, just a cauldron of water.
            if (inputs.isEmpty()) {
                level.setBlockAndUpdate(worldPosition, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
            }
        }
        return removed;
    }

    // hasStirred is written every time: absent reads back as false, which is what a fresh pot is
    // anyway. A failed ingredient encode writes no list at all rather than a partial one, so the
    // pot loads back empty instead of holding items nobody can identify.
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("HasStirred", hasStirred);
        DataResult<Tag> encoded = ItemStack.CODEC.listOf().encodeStart(NbtOps.INSTANCE, inputs);
        encoded.error().ifPresent(error -> LOGGER.error("nekomasfixed: could not save the soup cauldron's ingredients at {}: {}", this.worldPosition, error.message()));
        encoded.result().ifPresent(value -> tag.put("inputs", value));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        hasStirred = tag.getBoolean("HasStirred");
        inputs.clear();
        if (tag.contains("inputs", Tag.TAG_LIST)) {
            ItemStack.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("inputs"))
                    .resultOrPartial(error -> LOGGER.error("nekomasfixed: unreadable soup cauldron ingredients at {}: {}", this.worldPosition, error))
                    .ifPresent(inputs::addAll);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void setStirred(Level level) {
        this.hasStirred = true;
        if (level instanceof ServerLevel serverLevel) {
            List<ItemStack> updatedInputs = new ArrayList<>();
            for (ItemStack item : inputs) {
                SimpleContainer singleStackRecipeInput = new SimpleContainer(item);
                Optional<SmeltingRecipe> optional = serverLevel
                        .getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, singleStackRecipeInput, level);
                if (optional.isPresent() && !item.is(Items.CHORUS_FRUIT)) {
                    ItemStack itemStack = optional.get().assemble(singleStackRecipeInput, serverLevel.registryAccess());
                    if (!itemStack.isEmpty()) updatedInputs.add(itemStack);
                    else updatedInputs.add(item);
                } else updatedInputs.add(item);
            }
            inputs.clear();
            inputs.addAll(updatedInputs);
        }
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public List<ItemStack> getInputs() {return inputs;}


    public static void clientTick(Level level, BlockPos pos, BlockState state, SoupCauldronBlockEntity blockEntity) {
        blockEntity.CookingAnimator.setStarted(blockEntity.hasStirred);
        blockEntity.CookingAnimator.step();
        float progress = blockEntity.CookingAnimator.getProgress(0);
        if (progress>0&&progress<1){
            level.addParticle(ParticleTypes.BUBBLE_POP, pos.getX()+0.5+ level.getRandom().nextGaussian()*0.25, pos.getY()+1, pos.getZ()+0.5+ level.getRandom().nextGaussian()*0.25, 0.0, 0.0, 0.0);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }

    public float getOpenNess(float tickProgress) {
        return this.CookingAnimator.getProgress(tickProgress);
    }
}
