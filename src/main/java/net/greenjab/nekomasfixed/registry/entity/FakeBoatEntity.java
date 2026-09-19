package net.greenjab.nekomasfixed.registry.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class FakeBoatEntity extends Entity {
	@Nullable
	public BigBoatEntity owner = null;
	private int counter = 0;

	public FakeBoatEntity(EntityType<FakeBoatEntity> fakeBoatEntityEntityType, Level world) {
        super(fakeBoatEntityEntityType, world);
    }

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	@Override
	protected void readAdditionalSaveData(ValueInput view) {
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view) {
	}

	@Nullable
	@Override
	public ItemStack getPickResult() {
		return this.owner==null?ItemStack.EMPTY:this.owner.getPickResult();
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	@Override
	public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
		if (owner==null || owner.getPassengers().contains(source.getEntity())) return false;
		return owner.damage(world, source, amount);
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
	public boolean canBeCollidedWith(@Nullable Entity entity) {
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
					if (bl
							&& owner.getPassengers().size() < owner.getMaxPassengers()
							&& !entity.isPassenger()
							&& owner.hasEnoughSpaceFor(entity)
							&& entity instanceof LivingEntity
							&& !entity.getType().is(EntityTypeTags.CANNOT_BE_PUSHED_ONTO_BOATS)) {
						entity.startRiding(owner);
					} else {
						this.push(entity);
					}
				}
			}
		}
	}
}