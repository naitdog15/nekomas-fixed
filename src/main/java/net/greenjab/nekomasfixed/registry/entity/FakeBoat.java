package net.greenjab.nekomasfixed.registry.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FakeBoat extends Entity {
	public BigBoat owner = null;
	private int counter = 0;

	public FakeBoat(EntityType<FakeBoat> fakeBoatEntityEntityType, Level level) {
        super(fakeBoatEntityEntityType, level);
    }

	// PORT: Entity#defineSynchedData() is abstract - FakeBoat extends Entity directly (like vanilla's
	// own Boat), so there is no concrete super implementation to call (verified: Boat.defineSynchedData()
	// itself does not call super either). Entity's own shared flags register through a separate,
	// unconditional internal mechanism, not through this override.
	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	public ItemStack getPickResult() {
		return (owner!=null)?this.owner.getPickResult():null;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (owner==null || owner.getPassengers().contains(source.getEntity())) return false;
		return owner.hurtServer(level, source, amount);
	}
	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (owner==null || owner.getPassengers().contains(player)) return InteractionResult.PASS;
		return owner.interact(player, hand);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canCollideWith(Entity other) {
		return (other.canBeCollidedWith(this) || other.isPushable()) && !this.isPassengerOfSameVehicle(other) && other!=owner;
	}

	public void resetCounter(){
		counter=0;
	}

	@Override
	public void tick() {
		if (this.level() instanceof ServerLevel) {
			counter++;
			if (counter >= 10) {
				this.discard();
			}
		}
		if (owner==null) return;
		List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(owner));
		if (!list.isEmpty()) {
			boolean bl = !this.level().isClientSide() && !(owner.getControllingPassenger() instanceof Player);

			for (Entity entity : list) {
				if (!entity.hasPassenger(owner) && !owner.getPassengers().contains(entity)) {
					// PORT: EntityTypeTags.CANNOT_BE_PUSHED_ONTO_BOATS doesn't exist on 1.20.1; excluding
					// WaterAnimal directly is the closest available equivalent (matches vanilla Boat's own
					// pre-tag push filter). hasEnoughSpaceFor was part of the removed ChestVehicle
					// interface - getMaxPassengers() already accounts for hasChest(), so the passenger-count
					// check above covers the same "is there room" gate.
					if (bl
							&& owner.getPassengers().size() < owner.getMaxPassengers()
							&& !entity.isPassenger()
							&& entity instanceof LivingEntity
							&& !(entity instanceof WaterAnimal)) {
						entity.startRiding(owner);
					} else {
						this.push(entity);
					}
				}
			}
		}
	}
}