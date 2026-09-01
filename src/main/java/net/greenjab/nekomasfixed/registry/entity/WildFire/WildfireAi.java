package net.greenjab.nekomasfixed.registry.entity.WildFire;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

/**
 * PORT: 1.20.1's {@code Brain} has no {@code ActivityData}/single-call {@code Brain.provider(sensors,
 * activityFactory)} shape (26.2 restructured brain construction). Rewritten onto the real vanilla
 * idiom - verified against forge-1.20.1-mapped-src/.../monster/piglin/PiglinAi.java's own {@code
 * makeBrain(mob, brain)} - an imperative static helper that mutates the brain in place via {@code
 * addActivity}/{@code addActivityWithConditions} then {@code setCoreActivities}/{@code
 * setDefaultActivity}/{@code useDefaultActivity}. {@link WildfireEntity#brainProvider()} supplies the
 * two Collection args {@code Brain.provider(memoryTypes, sensorTypes)} now needs directly.
 */
public class WildfireAi {

	static void updateActivities(WildfireEntity wildFire) {
		wildFire.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
	}

	public static Brain<?> makeBrain(WildfireEntity wildfire, Brain<WildfireEntity> brain) {
		initCoreActivity(brain);
		initIdleActivity(brain);
		initFightActivity(wildfire, brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.useDefaultActivity();
		return brain;
	}

	private static void initCoreActivity(Brain<WildfireEntity> brain) {
		brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim<>(0.8F), new LookAtTargetSink(45, 90)));
	}

	private static void initIdleActivity(Brain<WildfireEntity> brain) {
		brain.addActivity(Activity.IDLE, ImmutableList.of(
				Pair.of(0, StartAttacking.create((wf) -> wf.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
				Pair.of(1, StartAttacking.create((wf) -> wf.getBrain().getMemory(MemoryModuleType.HURT_BY)
						.map(DamageSource::getEntity).filter( entity -> entity instanceof LivingEntity).map( entity -> (LivingEntity)entity))),
				Pair.of(2, new WildfireAi.SlideAroundTask(20, 40)),
				Pair.of(3, new RunOne<>(ImmutableList.of(Pair.of(new DoNothing(20, 100), 1),Pair.of(new WildfireSlideTowardsTargetTask(), 3))))));
	}

	private static void initFightActivity(WildfireEntity wildfire, Brain<WildfireEntity> brain) {
		brain.addActivityWithConditions(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, StopAttackingIfTargetInvalid.create(Sensor.wasEntityAttackableLastNTicks(wildfire, 1).negate()::test)),
				Pair.of(1, new WildfireShootTask()),
				Pair.of(2, new WildfireMeleeTask()),
				Pair.of(3, new WildfireJumpTask()),
				Pair.of(4, new WildfireBombTask()),
				Pair.of(5, new WildfireSlideTowardsTargetTask())
		), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)));
	}

	public static class SlideAroundTask extends MoveToTargetSink {
		@VisibleForTesting
		public SlideAroundTask(int i, int j) {
			super(i, j);
		}

		@Override
		protected void start(ServerLevel level, Mob mobEntity, long l) {
			super.start(level, mobEntity, l);
			mobEntity.playSound(WildfireRegistrations.BREEZE_SLIDE.get());
			mobEntity.setPose(Pose.STANDING);
		}

		@Override
		protected void stop(ServerLevel level, Mob mobEntity, long l) {
			super.stop(level, mobEntity, l);
			if (mobEntity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
				mobEntity.getBrain().setMemoryWithExpiry(WildfireRegistrations.BREEZE_SHOOT.get(), Unit.INSTANCE, 60L);
			}
		}
	}
}
