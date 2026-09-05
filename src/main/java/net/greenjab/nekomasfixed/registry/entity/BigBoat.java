package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Chest support mirrors vanilla's {@code ChestBoat} ({@code ContainerEntity} + {@code HasCustomInventoryScreen})
 * but as a flag on this entity rather than a separate entity type - every entry point must check {@link #hasChest()}.
 */
public class BigBoat extends Boat implements HasCustomInventoryScreen, ContainerEntity {

	private static final int CONTAINER_SIZE = 27;

	protected static final EntityDataAccessor<Boolean> CHEST = SynchedEntityData.defineId(BigBoat.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<ItemStack> BANNER = SynchedEntityData.defineId(BigBoat.class, EntityDataSerializers.ITEM_STACK);

	private FakeBoat front;
	private FakeBoat back;
	private final Supplier<Item> dropItemSupplier;

	private NonNullList<ItemStack> itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	@Nullable
	private ResourceLocation lootTable;
	private long lootTableSeed;

	public BigBoat(EntityType<? extends Boat> entityType, Level level, Supplier<Item> supplier) {
		super(entityType, level);
		this.dropItemSupplier = supplier;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CHEST, false);
		this.entityData.define(BANNER, ItemStack.EMPTY);
	}

	@Override
	protected int getMaxPassengers() {
		return hasChest()?2:3;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Chest", hasChest());
		if (!getBanner().isEmpty()) {
			tag.put("Banner", getBanner().save(new CompoundTag()));
		}
		this.addChestVehicleSaveData(tag);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setHasChest(tag.getBoolean("Chest"));
		setBanner(tag.contains("Banner") ? ItemStack.of(tag.getCompound("Banner")) : ItemStack.EMPTY);
		this.readChestVehicleSaveData(tag);
	}

	@Override
	public void tick() {
		super.tick();

		if (front==null || !front.isAlive()) {
			front = EntityTypeRegistry.FAKE_BOAT.get().create(this.level());
			if (front!=null) {
				front.owner = this;
				this.level().addFreshEntity(front);
			}
		}
		if (back==null || !back.isAlive()) {
			back = EntityTypeRegistry.FAKE_BOAT.get().create(this.level());
			if (back!=null) {
				back.owner = this;
				this.level().addFreshEntity(back);
			}
		}

		double dx = fakeOffset() * Math.cos((getYRot()+90f) * Math.PI / 180f);
		double dz = fakeOffset() * Math.sin((getYRot()+90f) * Math.PI / 180f);
		front.setPos(this.getX() + dx, this.getY(), this.getZ() + dz);
		back.setPos(this.getX() - dx, this.getY(), this.getZ() - dz);
		front.resetCounter();
		back.resetCounter();
	}

	public float fakeOffset() {
		return 1.15f;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.is(Items.CHEST)) {
			if (!hasChest() && getPassengers().size()<4) {
				setHasChest(true);
				itemStack.shrink(1);
				player.level().playSound(null, this, SoundEvents.DONKEY_CHEST, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.SUCCESS;
		} else if (itemStack.is(ItemTags.BANNERS)) {
			if (getBanner().isEmpty()) {
				setBanner(itemStack.copyWithCount(1));
				itemStack.shrink(1);
				player.level().playSound(null, this, SoundEvents.DONKEY_CHEST, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.SUCCESS;
		} else if (itemStack.is(Items.SHEARS)) {
			if (!getBanner().isEmpty()) {
				player.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
				ItemStack banner = getBanner().copy();
				setBanner(ItemStack.EMPTY);
				if (player.level() instanceof ServerLevel) {
					this.spawnAtLocation(banner, 1.5F);
					itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				}
			}
			return InteractionResult.SUCCESS;
		} else if (hasChest() && (player.isSecondaryUseActive() || !this.canAddPassenger(player))) {
			InteractionResult result = this.interactWithContainerVehicle(player);
			if (result.consumesAction()) {
				this.gameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinAi.angerNearbyPiglins(player, true);
			}
			return result;
		} else {
			return super.interact(player, hand);
		}
	}

	@Override
	public InteractionResult interactWithContainerVehicle(Player player) {
		return hasChest() ? ContainerEntity.super.interactWithContainerVehicle(player) : InteractionResult.PASS;
	}

	@Override
	public void openCustomInventoryScreen(Player player) {
		if (!hasChest()) return;
		player.openMenu(this);
		if (!player.level().isClientSide()) {
			this.gameEvent(GameEvent.CONTAINER_OPEN, player);
			PiglinAi.angerNearbyPiglins(player, true);
		}
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		if (this.lootTable != null && player.isSpectator()) return null;
		this.unpackChestVehicleLootTable(inventory.player);
		return ChestMenu.threeRows(containerId, inventory, this);
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.getChestVehicleItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return this.removeChestVehicleItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return this.removeChestVehicleItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.setChestVehicleItem(slot, stack);
	}

	@Override
	public SlotAccess getSlot(int slot) {
		return hasChest() ? this.getChestVehicleSlot(slot) : SlotAccess.NULL;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return this.isChestVehicleStillValid(player);
	}

	@Override
	public void stopOpen(Player player) {
		this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(player));
	}

	@Override
	public void clearContent() {
		this.clearChestVehicleContent();
	}

	@Nullable
	@Override
	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	@Override
	public void setLootTable(@Nullable ResourceLocation lootTable) {
		this.lootTable = lootTable;
	}

	@Override
	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	@Override
	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}

	@Override
	public NonNullList<ItemStack> getItemStacks() {
		return this.itemStacks;
	}

	@Override
	public void clearItemStacks() {
		this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
	}

	public void setHasChest(boolean hasChest) {
		this.entityData.set(CHEST, hasChest);
	}
	public boolean hasChest() { return this.entityData.get(CHEST);}

	public void setBanner(ItemStack banner) {
		this.entityData.set(BANNER, banner);
	}
	public ItemStack getBanner() {return this.entityData.get(BANNER);}


	public float getSpeed() {
		float s = 0.4f+countRowable()*0.1f+(!getBanner().isEmpty()?0.15f:0f);
		return getFirstPassenger() instanceof Raider ? Math.min(s, 0.6f) : s;
	}

	public float getRotationSpeed() {
		return 0.6f;
	}

	public int countRowable() {
		int i = 0;
		Iterator<Entity> iter = getPassengers().stream().iterator();
		while (iter.hasNext()){
			Entity e = iter.next();
			if (e instanceof Player || e instanceof AbstractVillager || e instanceof Raider) {
				i++;
			}
		}
		return i;
	}

	/** {@code Boat#destroy(DamageSource)} calls this internally (spawnAtLocation) - overriding it also
	 * fixes getPickResult()'s drop for free, since vanilla builds that from the same hook. */
	@Override
	public Item getDropItem() {
		return this.dropItemSupplier.get();
	}

	@Override
	protected void destroy(DamageSource damageSource) {
		if (front!=null) this.front.remove(RemovalReason.DISCARDED);
		if (back!=null) this.back.remove(RemovalReason.DISCARDED);
		super.destroy(damageSource);
		if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			if (hasChest()) Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), Items.CHEST.getDefaultInstance());
			Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), getBanner());
		}
		this.chestVehicleDestroyed(damageSource, this.level(), this);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (front!=null) this.front.remove(RemovalReason.DISCARDED);
		if (back!=null) this.back.remove(RemovalReason.DISCARDED);
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
			if (hasChest()) Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), Items.CHEST.getDefaultInstance());
			Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), getBanner());
			Containers.dropContents(this.level(), this, this);
		}

		super.remove(reason);
	}

	public boolean canHaveALeashAttachedTo(Entity entity) {
		return false;
	}

	@Override
	public boolean canCollideWith(Entity other) {
		return !(other==front||other==back) && super.canCollideWith(other);
	}

	/** A bigger hull soaks 20% of every hit before Boat's own damage/wobble bookkeeping sees it. */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		return super.hurt(source, amount*0.8f);
	}

	/**
	 * Automation that goes through the item-handler capability rather than {@link net.minecraft.world.Container}
	 * only sees the boat while the chest is actually on it - dropping the capability when there is no
	 * chest keeps a plain boat from advertising 27 invisible slots.
	 */
	private LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER && this.isAlive() && this.hasChest()) {
			return this.itemHandler.cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		this.itemHandler.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.itemHandler = LazyOptional.of(() -> new InvWrapper(this));
	}
}
