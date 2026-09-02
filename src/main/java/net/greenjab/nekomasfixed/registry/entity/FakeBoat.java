package net.greenjab.nekomasfixed.registry.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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

	// Nothing of this entity's own is synced - it is a positional stand-in for the hull it belongs to,
	// and everything worth sending to the client already lives on the owner.
	@Override
	protected void defineSynchedData() {
	}

	// Sits flush with the hull it stands in for, so the eyes belong at the top of the box rather than
	// at the usual fraction of it - the same place a boat puts them.
	@Override
	protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height;
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

	/** A hit on either half of the hull is a hit on the real boat, unless it came from someone aboard. */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (owner==null || owner.getPassengers().contains(source.getEntity())) return false;
		return owner.hurt(source, amount);
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
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canCollideWith(Entity other) {
		return (other.canBeCollidedWith() || other.isPushable()) && !this.isPassengerOfSameVehicle(other) && other!=owner;
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
					// Water animals are shoved aside rather than boarded, the same exception vanilla makes
					// for its own boats; the passenger count is the room check, and it already accounts
					// for whether a chest is taking up a seat.
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