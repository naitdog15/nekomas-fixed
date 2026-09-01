package net.greenjab.nekomasfixed.registry.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class HugeBoat extends BigBoat {

	public HugeBoat(EntityType<? extends BigBoat> entityType, Level level, Supplier<Item> supplier) {
		super(entityType, level, supplier);
	}

	@Override
	protected int getMaxPassengers() {
		return hasChest()?3:4;
	}

	/**
	 * 1.20.1's Boat has no per-passenger attachment-point hook - it lays its riders out inside
	 * positionRider - so the long hull's seat spacing lives here: 1.6 forward for the helm, 1.25 back
	 * for each seat behind it. Super still runs first so vanilla's turn-delta and rotation clamping
	 * still reach the rider; only the horizontal seat position is replaced.
	 */
	@Override
	protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
		super.positionRider(passenger, moveFunction);
		if (this.hasPassenger(passenger)) {
			float forward = 1.6f - this.getPassengers().indexOf(passenger)*1.25f;
			double height = (this.isRemoved() ? 0.01 : this.getPassengersRidingOffset()) + passenger.getMyRidingOffset();
			Vec3 seat = new Vec3(0.0, 0.0, forward).yRot(-this.getYRot() * (float) (Math.PI / 180.0));
			moveFunction.accept(passenger, this.getX() + seat.x, this.getY() + height, this.getZ() + seat.z);
		}
	}

	@Override
	public float getSpeed() {
		float s = 0.3f+countRowable()*0.1f+(!getBanner().isEmpty()?0.2f:0f);
		return getFirstPassenger() instanceof Raider ? Math.min(s, 0.6f) : s;
	}

	@Override
	public float getRotationSpeed() {
		return 0.4f;
	}

	@Override
	public float fakeOffset() {
		return 2.1f;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return super.hurt(source, amount*0.6f);
	}
}