package net.greenjab.nekomasfixed.registry.block.entity;

import net.greenjab.nekomasfixed.registry.block.ClamBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ClamBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
	private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	private int state = 0;
	private final ChestLidController lidAnimator = new ChestLidController();

	protected ClamBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public ClamBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityTypeRegistry.CLAM_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.readInventoryNbt(tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.inventory, false);
		}
	}

	public void readInventoryNbt(CompoundTag tag) {
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.inventory);
		}
	}

	/**
	 * The clam has no screen of its own - {@link #createMenu} returns null and nothing opens it -
	 * but {@code getName()} is still reachable from anything that inspects the container, and a null
	 * here would take it down. Borrowing the block's own name costs nothing and keeps that safe
	 * whatever inspects the clam next.
	 */
	@Override
	protected Component getDefaultName() {
		return this.getBlockState().getBlock().getName();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		// Force the Items list even when empty, otherwise taking the pearl out leaves the client
		// rendering the old one until the chunk reloads.
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, this.inventory, true);
		return tag;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, ClamBlockEntity blockEntity) {
		blockEntity.lidAnimator.shouldBeOpen(state.getValue(ClamBlock.OPEN));
		blockEntity.lidAnimator.tickLid();
		if (state.getValue(ClamBlock.OPEN) && state.getValue(ClamBlock.WATERLOGGED) && blockEntity.lidAnimator.getOpenness(0)<1){
			level.addParticle(ParticleTypes.BUBBLE, pos.getX()+0.5+ level.getRandom().nextGaussian()*0.15, pos.getY()+0.2, pos.getZ()+0.5+ level.getRandom().nextGaussian()*0.15, 0.0, 0.75, 0.0);
		}

	}

	@Override
	public boolean triggerEvent(int type, int data) {
		return super.triggerEvent(type, data);
	}

	@Override
	public float getOpenNess(float tickProgress) {
		return this.lidAnimator.getOpenness(tickProgress);
	}


	@Override
	public NonNullList<ItemStack> getItems() {
		return this.inventory;
	}

	public void setHeldStack(ItemStack itemStack) {
		this.inventory.set(0, itemStack);
	}


	@Override
	protected void setItems(NonNullList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return null;
	}

	public ItemStack swapStack(int slot, ItemStack stack) {
		ItemStack itemStack = this.removeItemNoUpdate(slot);
		this.setItem(slot, stack);
		return itemStack;
	}

	public void markDirty(GameEvent gameEvent) {
		super.setChanged();
		if (this.level != null) {
			this.level.gameEvent(gameEvent, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	/**
	 * Which of the three item models the clam drops as: closed, open, or open-with-a-pearl. Derived
	 * from the block state and contents by the block itself just before the drop is built, so it is
	 * deliberately not part of the saved tag.
	 */
	public void setState(int cstate) {
		state = cstate;
	}

	public int getState() {
		return state;
	}
}
