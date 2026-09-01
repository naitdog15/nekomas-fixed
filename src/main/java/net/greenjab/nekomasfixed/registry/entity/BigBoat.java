package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * PORT (best-effort, documented gap): 1.20.1
 * has no {@code AbstractChestBoat}/{@code ChestVehicle} hierarchy at all - only two fixed, separate
 * top-level classes, {@code Boat} and {@code ChestBoat extends Boat} (verified: forge-1.20.1-mapped-
 * src/net/minecraft/world/entity/vehicle/ has exactly those two, no shared chest-capable base, no
 * per-passenger {@code getPassengerAttachmentPoint}/{@code EntityDimensions} hook on {@code Boat} at
 * all). BigBoat's whole design - one entity type that TOGGLES a chest on/off plus a banner, unlike
 * vanilla's fixed dual-entity-type split - has no clean 1.20.1 analogue to retarget onto, so this is
 * a genuine redesign, not a port: BigBoat now extends {@code Boat} directly. The toggleable-chest
 * *data* (the {@code CHEST} boolean + banner sync) is kept, since it's just a flag; the chest
 * *inventory UI* ({@code ContainerEntity}/{@code HasCustomInventoryScreen}, opening a real chest
 * screen) is NOT reimplemented here - it would need a new common inventory-screen contract this
 * package doesn't own the pieces for (a MenuType is registry/item territory owned elsewhere). Passenger seating
 * uses whatever default vanilla {@code Boat} positioning provides (no custom per-passenger offset
 * hook exists to override on 1.20.1) - a graceful degradation, not a crash.
 */
public class BigBoat extends Boat {

	protected static final EntityDataAccessor<Boolean> CHEST = SynchedEntityData.defineId(BigBoat.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<ItemStack> BANNER = SynchedEntityData.defineId(BigBoat.class, EntityDataSerializers.ITEM_STACK);

	private FakeBoat front;
	private FakeBoat back;
	private final Supplier<Item> dropItemSupplier;

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
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setHasChest(tag.getBoolean("Chest"));
		setBanner(tag.contains("Banner") ? ItemStack.of(tag.getCompound("Banner")) : ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		super.tick();

		if (front==null || !front.isAlive()) {
			front = EntityTypeRegistry.FAKE_BOAT.get().create(this.level(), MobSpawnType.MOB_SUMMONED);
			if (front!=null) {
				front.owner = this;
				this.level().addFreshEntity(front);
			}
		}
		if (back==null || !back.isAlive()) {
			back = EntityTypeRegistry.FAKE_BOAT.get().create(this.level(), MobSpawnType.MOB_SUMMONED);
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
				player.level().playSound(null, this, SoundEvents.COPPER_GOLEM_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
				ItemStack banner = getBanner().copy();
				setBanner(ItemStack.EMPTY);
				if (player.level() instanceof ServerLevel) {
					this.spawnAtLocation(banner, 1.5F);
					itemStack.hurtAndBreak(1, player, hand);
				}
			}
			return InteractionResult.SUCCESS;
		} else {
			return super.interact(player, hand);
		}
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
	}

	@Override
	public void remove(RemovalReason reason) {
		if (front!=null) this.front.remove(RemovalReason.DISCARDED);
		if (back!=null) this.back.remove(RemovalReason.DISCARDED);
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
			if (hasChest()) Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), Items.CHEST.getDefaultInstance());
			Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), getBanner());
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

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return super.hurtServer(level, source, amount*0.8f);
	}
}
