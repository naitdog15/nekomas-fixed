package net.greenjab.nekomasfixed.registry.entity.WildFire;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public class WildfireJumpTask extends Behavior<WildfireEntity> {
	private static final int JUMP_INHALING_EXPIRY = 20;

    @VisibleForTesting
	public WildfireJumpTask() {
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryStatus.VALUE_PRESENT,
				MemoryModuleType.WALK_TARGET,
				MemoryStatus.VALUE_ABSENT,
				WildfireRegistrations.BREEZE_SHOOT_COOLDOWN.get(),
				MemoryStatus.VALUE_ABSENT,
				WildfireRegistrations.BREEZE_SHOOT.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_JUMP_TARGET.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_JUMP_COOLDOWN.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_JUMP_INHALING.get(),
				MemoryStatus.REGISTERED,
				WildfireRegistrations.BREEZE_LEAVING_WATER.get(),
				MemoryStatus.REGISTERED
		), 120);
	}

	public static boolean shouldJump(ServerLevel level, WildfireEntity wildFire) {
		if (wildFire.getPose() != Pose.LONG_JUMPING) return false;
		if (wildFire.getBrain().checkMemory(WildfireRegistrations.BREEZE_JUMP_TARGET.get(), MemoryStatus.VALUE_PRESENT)) return true;
		LivingEntity livingEntity = wildFire.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
		if (livingEntity == null) return false;
		BlockPos blockPos = wildFire.getSpawnPos().offset(0, 3, 0);
		if (!blockPos.closerThan(wildFire.blockPosition(), 15))
			blockPos = livingEntity.blockPosition().offset(0, 4, 0);
		BlockState blockState = level.getBlockState(blockPos.below());
		if (wildFire.getType().isBlockDangerous(blockState)) return false;
		else if (WildfireMovementUtil.cantMoveTo(wildFire, Vec3.atCenterOf(blockPos))
				&& WildfireMovementUtil.cantMoveTo(wildFire, Vec3.atCenterOf(blockPos.above(4)))) return false;
		else {
			wildFire.getBrain().setMemory(WildfireRegistrations.BREEZE_JUMP_TARGET.get(), blockPos);
			return true;
		}
	}

	protected boolean checkExtraStartConditions(ServerLevel level, WildfireEntity wildFireEntity) {
		return shouldJump(level, wildFireEntity);
	}

	protected boolean canStillUse(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		return !wildFireEntity.getBrain().hasMemoryValue(WildfireRegistrations.BREEZE_JUMP_COOLDOWN.get());
	}

	protected void start(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		if (wildFireEntity.getBrain().checkMemory(WildfireRegistrations.BREEZE_JUMP_INHALING.get(), MemoryStatus.VALUE_ABSENT)) {
			wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_JUMP_INHALING.get(), Unit.INSTANCE, JUMP_INHALING_EXPIRY);
		}
		wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT.get(), Unit.INSTANCE,120);
		wildFireEntity.setPose(Pose.DIGGING);
		level.playSound(null, wildFireEntity, WildfireRegistrations.BREEZE_CHARGE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
		wildFireEntity.getBrain().getMemory(WildfireRegistrations.BREEZE_JUMP_TARGET.get())
			.ifPresent( jumpTarget -> wildFireEntity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(jumpTarget)));
	}

	protected void tick(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		if (shouldStopInhalingPose(wildFireEntity)) {
			Vec3 vec3d = wildFireEntity.getBrain().getMemory(WildfireRegistrations.BREEZE_JUMP_TARGET.get())
				.flatMap( jumpTarget -> getJumpingVelocity(wildFireEntity, Vec3.atBottomCenterOf(jumpTarget)))
				.orElse(null);
			if (vec3d == null) return;
			wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_LEAVING_WATER.get(), Unit.INSTANCE, 60L);
			wildFireEntity.playSound(WildfireRegistrations.BREEZE_JUMP_SOUND.get(), 1.0F, 1.0F);
			wildFireEntity.setYRot(wildFireEntity.yBodyRot);
			wildFireEntity.setDiscardFriction(true);
			wildFireEntity.setDeltaMovement(vec3d);
		} else if (shouldStopLongJumpingPose(wildFireEntity)) {
			wildFireEntity.setDeltaMovement(0, 0, 0);
			wildFireEntity.eyeOffset = -3;
			wildFireEntity.playSound(WildfireRegistrations.BREEZE_LAND.get(), 1.0F, 1.0F);
			wildFireEntity.setDiscardFriction(false);
			boolean bl2 = wildFireEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
			wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_JUMP_COOLDOWN.get(), Unit.INSTANCE, bl2 ? 2L : 10L);
		}
	}

	public static Optional<Vec3> getJumpingVelocity(Mob entity, Vec3 jumpTarget) {
		Vec3 vec3d = entity.position();
		Vec3 vec3d2 = new Vec3(jumpTarget.x - vec3d.x, 0.0, jumpTarget.z - vec3d.z).normalize().scale(0.5);
		Vec3 vec3d3 = jumpTarget.subtract(vec3d2);
		Vec3 vec3d4 = vec3d3.subtract(vec3d);

		double h = vec3d4.y;
		double g = WildfireMovementUtil.MOB_GRAVITY;
		double vy = Math.sqrt(2* g *h);
		double vx = g *vec3d4.horizontalDistance()/ vy;

		double d = Math.atan2(vec3d4.z, vec3d4.x);
		double n = Math.sin(d);
		double o = Math.cos(d);

		if (vy / vx < 0.3) return Optional.empty();
		else return Optional.of(new Vec3(vx * o, vy, vx * n).scale(0.95F));
	}

	protected void stop(ServerLevel level, WildfireEntity wildFireEntity, long l) {
		wildFireEntity.getBrain().eraseMemory(WildfireRegistrations.BREEZE_JUMP_TARGET.get());
		wildFireEntity.getBrain().eraseMemory(WildfireRegistrations.BREEZE_JUMP_INHALING.get());
		wildFireEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT_COOLDOWN.get(), Unit.INSTANCE, 200L);
		wildFireEntity.setDiscardFriction(false);
	}

	private static boolean shouldStopInhalingPose(WildfireEntity wildFire) {
		return wildFire.getBrain().getMemory(WildfireRegistrations.BREEZE_JUMP_INHALING.get()).isEmpty() &&
				wildFire.getBrain().getMemory(WildfireRegistrations.BREEZE_LEAVING_WATER.get()).isEmpty();
	}

	private static boolean shouldStopLongJumpingPose(WildfireEntity wildFire) {
        return wildFire.getDeltaMovement().y < -0 && wildFire.getBrain().getMemory(WildfireRegistrations.BREEZE_JUMP_INHALING.get()).isEmpty();
    }
}
