package net.greenjab.nekomasfixed.registry.entity.WildFire;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class WildfireMovementUtil {

	/** The value vanilla's own long-jump behaviour uses for mob gravity when solving a jump arc. */
	public static final double MOB_GRAVITY = 0.08;

	/**
	 * Solves the launch velocity that lands something fired from {@code from} at {@code target}, given
	 * a launch angle and the falling speed of whatever is being launched.
	 * <p>
	 * Vanilla keeps this math private inside {@code LongJumpToRandomPos} and hard-codes mob gravity in
	 * it, which is no use to the Wildfire: it needs the same solver both for its own leap and for
	 * lobbing a fire bomb, which falls far slower. Same arc as vanilla's, gravity passed in, and
	 * without vanilla's landing-spot collision sweep (the Wildfire aims at a live target, not a block).
	 *
	 * @return empty when no arc at that angle reaches the target within {@code maxVelocity}
	 */
	public static Optional<Vec3> calculateLaunchVector(Vec3 from, Vec3 target, double gravity, float maxVelocity, int angleDegrees) {
		Vec3 pullBack = new Vec3(target.x - from.x, 0.0, target.z - from.z).normalize().scale(0.5);
		Vec3 delta = target.subtract(pullBack).subtract(from);
		float angle = (float) angleDegrees * (float) Math.PI / 180.0F;
		double heading = Math.atan2(delta.z, delta.x);
		double horizontalSq = delta.subtract(0.0, delta.y, 0.0).lengthSqr();
		double horizontal = Math.sqrt(horizontalSq);
		double divisor = horizontal * Math.sin(2.0F * angle) - 2.0 * delta.y * Math.pow(Math.cos(angle), 2.0);
		double speedSq = horizontalSq * gravity / divisor;
		if (!Double.isFinite(speedSq) || speedSq < 0.0) {
			return Optional.empty();
		}
		double speed = Math.sqrt(speedSq);
		if (speed > maxVelocity) {
			return Optional.empty();
		}
		double flat = speed * Math.cos(angle);
		return Optional.of(new Vec3(flat * Math.cos(heading), speed * Math.sin(angle), flat * Math.sin(heading)).scale(0.95));
	}

	public static boolean cantMoveTo(WildfireEntity wildFire, Vec3 pos) {
		Vec3 vec3d = new Vec3(wildFire.getX(), wildFire.getY(), wildFire.getZ());
		return pos.distanceTo(vec3d) > getMaxMoveDistance(wildFire) || wildFire.level().clip(new ClipContext(vec3d, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, wildFire)).getType() != HitResult.Type.MISS;
	}

	private static double getMaxMoveDistance(WildfireEntity wildFire) {
		return Math.max(50.0, wildFire.getAttributeValue(Attributes.FOLLOW_RANGE));
	}

	public static Vec3 findFirePos(WildfireEntity wildFire, boolean findAnyway) {
		BlockPos blockPos = wildFire.blockPosition();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		RandomSource random = wildFire.getRandom();

		for (int i = 0; i < 100; i++) {
			int x = -7 + random.nextInt(15);
			int y = 2;
			int z = -7 + random.nextInt(15);

			mutable.setWithOffset(blockPos, x, y, z);
			while (wildFire.level().getBlockState(mutable).isAir()&&y>-4){
				y--;
				mutable.setWithOffset(blockPos, x, y, z);
			}
			if (wildFire.isWithinRestriction(mutable) && wildFire.level().getBlockState(mutable).is(BlockTags.FIRE)) {
				return Vec3.atCenterOf(mutable);
			}
		}
		if (findAnyway) return Vec3.atCenterOf(mutable);
		return null;
	}
}
