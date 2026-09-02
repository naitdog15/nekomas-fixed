package net.greenjab.nekomasfixed.registry.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.slf4j.Logger;

public class HollowLogBlockEntity extends BlockEntity implements Container {
    private static final Logger LOGGER = LogUtils.getLogger();
    private BlockState storedBlock = Blocks.AIR.defaultBlockState();
    private NonNullList<ItemStack> storedStack = NonNullList.withSize(1, ItemStack.EMPTY);

    public HollowLogBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.HOLLOW_LOG_BLOCK_ENTITY.get(), pos, state);
    }

    public BlockState getStoredBlock() {
        return this.storedBlock;
    }
    public ItemStack getStoredStack() {
        return getHeldStack();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void setStoredBlock(ItemStack stack, BlockState state) {
        this.storedBlock = state;
        setHeldStack(stack);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // A block that will not encode is written as no key at all, so the log loads back hollow
    // rather than holding something the renderer cannot draw.
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        DataResult<Tag> encoded = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, storedBlock);
        encoded.error().ifPresent(error -> LOGGER.error("nekomasfixed: could not save the block stored in the hollow log at {}: {}", this.worldPosition, error.message()));
        encoded.result().ifPresent(value -> tag.put("StoredBlock", value));
        ContainerHelper.saveAllItems(tag, this.storedStack, false);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        storedBlock = tag.contains("StoredBlock", Tag.TAG_COMPOUND)
                ? BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("StoredBlock"))
                        .resultOrPartial(error -> LOGGER.error("nekomasfixed: unreadable block stored in the hollow log at {}: {}", this.worldPosition, error))
                        .orElse(Blocks.AIR.defaultBlockState())
                : Blocks.AIR.defaultBlockState();
        this.storedStack = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.storedStack);
    }
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.getHeldStacks()) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.getHeldStacks().get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = ContainerHelper.removeItem(this.getHeldStacks(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.getHeldStacks(), slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.getHeldStacks().set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.getHeldStacks().clear();
    }
    public NonNullList<ItemStack> getHeldStacks() {
        return this.storedStack;
    }
    public void setHeldStack(ItemStack itemStack) {
        this.storedStack.set(0, itemStack);
    }
    public ItemStack getHeldStack() {
        return this.storedStack.get(0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static boolean canStoreBlock(HollowLogBlockEntity logBE, BlockItem blockItem, boolean vertical){
        BlockState blockItemState = blockItem.getBlock().defaultBlockState();
        if (!logBE.getStoredBlock().isAir()) return false;
        if (blockItem.getBlock().defaultBlockState().is(BlockTags.SHULKER_BOXES)) return false;
        if (blockItemState.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)==Shapes.block()) return true;
        if (!vertical) return blockItemState.is(BlockTags.SMALL_FLOWERS) || blockItemState.is(Blocks.FLOWER_POT)||
                blockItemState.is(Blocks.TORCH) || blockItemState.is(Blocks.SOUL_TORCH) ||
                blockItemState.is(Blocks.LANTERN) || blockItemState.is(Blocks.SOUL_LANTERN);
        return false;
    }


}
