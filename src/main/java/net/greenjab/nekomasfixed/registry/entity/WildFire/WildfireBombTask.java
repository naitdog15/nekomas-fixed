package net.greenjab.nekomasfixed.registry.entity.WildFire;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class WildfireBombTask extends Behavior<WildfireEntity> {
	private static final int SHOOT_CHARGING_EXPIRY = Math.round(20.0F);
	private static final int RECOVER_EXPIRY = Math.round(39.0F);
	private static final int SHOOT_COOLDOWN_EXPIRY = Math.round(4.0F);

	@VisibleForTesting
	public WildfireBombTask() {
		super(ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryStatus.VALUE_PRESENT,
				MemoryModuleType.WALK_TARGET,
				MemoryStatus.VALUE_ABSENT,
				WildfireRegistrations.BREEZE_SHOOT_COOLDOWN.get(),
				MemoryStatus.VALUE_PRESENT,
				WildfireRegistrations.BREEZE_LEAVING_WATER.get(),
				MemoryStatus.VALUE_PRESENT,
				WildfireRegistrations.BREEZE_SHOOT.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_SHOOT_CHARGING.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_SHOOT_RECOVERING.get(),
				MemoryStatus.REGISTERED,
				MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
				MemoryStatus.REGISTERED
		), SHOOT_CHARGING_EXPIRY + RECOVER_EXPIRY);
	}

	protected boolean checkExtraStartConditions(ServerLevel level, WildfireEntity wildFireEntity) {
		if (wildFireEntity.getPose() != Pose.DIGGING) return false;
		return wildFireEntity.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET)
                .map(target -> isTargetWithinRange(wildFireEntity, target))
                .map(withinRange -> {
                    if (!withinRange) wildFireEntity.getBrain().eraseMemory(WildfireRegistrations.BREEZE_SHOOT.get());
					return withinRange;
                }).orElse(false);
	}

	protected boolean canStillUse(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		return wildFireEntity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
	}

	protected void start(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		wildFireEntity.setPose(Pose.STANDING);
		wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT_CHARGING.get(), Unit.INSTANCE, SHOOT_CHARGING_EXPIRY);
		wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT.get(), Unit.INSTANCE,SHOOT_CHARGING_EXPIRY + RECOVER_EXPIRY);
		wildFireEntity.playSound(WildfireRegistrations.inhale(), 1.0F, 1.0F);
		wildFireEntity.setFireActive(true);
	}

	protected void stop(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT_COOLDOWN.get(), Unit.INSTANCE, 200L);
		wildFireEntity.getBrain().eraseMemory(WildfireRegistrations.BREEZE_SHOOT.get());
		wildFireEntity.getBrain().eraseMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
		wildFireEntity.setFireActive(false);
		wildFireEntity.eyeOffset = -0.5f;
	}

	protected void tick(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		Brain<WildfireEntity> brain = wildFireEntity.getBrain();
		LivingEntity livingEntity = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
		if (livingEntity != null) {
			wildFireEntity.lookAt(EntityAnchorArgument.Anchor.EYES, livingEntity.position());
			if (brain.getMemory(WildfireRegistrations.BREEZE_SHOOT_CHARGING.get()).isEmpty() &&
				brain.getMemory(WildfireRegistrations.BREEZE_SHOOT_RECOVERING.get()).isEmpty()) {
				brain.setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT_RECOVERING.get(), Unit.INSTANCE, SHOOT_COOLDOWN_EXPIRY);

				// The lob solves against the bomb's own fall speed, not the mob's - that's why the jump
				// solver can't be reused as-is. The config switch instead solves on mob gravity, so the
				// bomb falls slower than the arc assumed and sails long.
				double lobGravity = NekomasFixedConfig.WILDFIRE_BOMB_ARC.get()
						? FireBomb.GRAVITY
						: WildfireMovementUtil.MOB_GRAVITY;
				Optional<Vec3> optional = WildfireMovementUtil.calculateLaunchVector(
						wildFireEntity.position(), livingEntity.position(), lobGravity, 1.11f, level.getRandom().nextInt(10) + 45);
				if (optional.isPresent()) {
					int i = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS).orElse(-1);
					brain.setMemoryWithExpiry(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, i+1, 60);
					if (i < 5 ||wildFireEntity.isSoulActive()) {
						Vec3 v = optional.get();
						int j = (int) (i / 2f + 0.5f + (i % 2 == 0 ? -i + 15.5f : 0));
						v = v.yRot((float) (22.5 * j * Math.PI / 180.0));
						if (i == -1) v = v.scale(0);

						FireBomb fireBombEntity = new FireBomb(level, wildFireEntity);
						fireBombEntity.setPos(fireBombEntity.getX(), wildFireEntity.getY(0.5) + 0.5, fireBombEntity.getZ());
						fireBombEntity.shoot(v.x, v.y, v.z, (float) v.length() * (1-i/20f), 0.0F);
						level.addFreshEntity(fireBombEntity);
						wildFireEntity.playSound(WildfireRegistrations.shoot(), 1.5F, 1.0F);
					}
				}
			}
		}
	}

	private static boolean isTargetWithinRange(WildfireEntity wildFire, LivingEntity target) {
		double d = wildFire.position().distanceToSqr(target.position());
		return d < 1024;
	}
}
