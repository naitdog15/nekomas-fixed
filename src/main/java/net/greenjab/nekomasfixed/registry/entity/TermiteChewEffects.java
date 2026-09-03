package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.registry.block.enums.HollowLogType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Everything a chewing or depositing termite looks and sounds like lives here, kept off Termite
// itself so the entity class stays about behaviour rather than presentation.
public final class TermiteChewEffects {

    /** Ticks between wood chips while a termite is chewing. */
    private static final int CHIP_INTERVAL_TICKS = 3;
    /** Chips thrown out when a log finally gives way. */
    private static final int FINISH_CHIP_COUNT = 12;
    /** Chips shaken off the mound when a laden termite unloads. */
    private static final int DEPOSIT_CHIP_COUNT = 6;

    private TermiteChewEffects() {
    }

    // Called from Termite's tick on the client. Level.addParticle has an empty body outside
    // ClientLevel, so the side check below is what makes the intent match the effect.
    public static void clientTick(Termite termite) {
        Level level = termite.level();
        if (!level.isClientSide()
                || !termite.isChewing()
                || termite.tickCount % CHIP_INTERVAL_TICKS != 0) {
            return;
        }

        BlockPos logPos = termite.getChewTarget().orElse(null);
        if (logPos == null || !level.isLoaded(logPos)) {
            return;
        }

        // A chunk the client has not got yet reads back as void air, and a log that has already
        // turned hollow no longer maps to anything - either way there is nothing left to chip.
        BlockState logState = level.getBlockState(logPos);
        if (!HollowLogType.hasHollowVariant(logState)) {
            return;
        }

        double centerX = logPos.getX() + 0.5D;
        double centerZ = logPos.getZ() + 0.5D;
        double deltaX = termite.getX() - centerX;
        double deltaZ = termite.getZ() - centerZ;

        // Chips come off whichever side of the log the termite is standing at: the bigger horizontal
        // offset picks the axis, its sign picks the face.
        double normalX = 0.0D;
        double normalZ = 0.0D;
        if (Math.abs(deltaX) >= Math.abs(deltaZ)) {
            normalX = deltaX < 0.0D ? -1.0D : 1.0D;
        } else {
            normalZ = deltaZ < 0.0D ? -1.0D : 1.0D;
        }

        double tangentX = -normalZ;
        double tangentZ = normalX;

        RandomSource random = level.getRandom();

        // Chips spawn across a narrow patch at mouth height, clamped inside the log's own face so
        // they never appear in the block next door.
        double tangentOffset = (random.nextDouble() - 0.5D) * 0.20D;
        double chipY = Mth.clamp(
                termite.getY() + 0.25D,
                logPos.getY() + 0.18D,
                logPos.getY() + 0.82D
        ) + (random.nextDouble() - 0.5D) * 0.12D;
        double chipX = centerX + normalX * 0.505D + tangentX * tangentOffset;
        double chipZ = centerZ + normalZ * 0.505D + tangentZ * tangentOffset;

        // A short kick out of the cut, with a little sideways scatter and lift.
        double outwardSpeed = 0.020D + random.nextDouble() * 0.015D;
        double tangentSpeed = (random.nextDouble() - 0.5D) * 0.030D;
        double upwardSpeed = 0.015D + random.nextDouble() * 0.025D;

        // The source position is what lets a block whose model varies by location chip the right texture.
        level.addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, logState).setPos(logPos),
                chipX,
                chipY,
                chipZ,
                normalX * outwardSpeed + tangentX * tangentSpeed,
                upwardSpeed,
                normalZ * outwardSpeed + tangentZ * tangentSpeed
        );
    }

    // The block has already been swapped by the time this runs, so the burst is built from the log's
    // previous state - the chips are the wood that just came out of it.
    public static void onChewFinished(ServerLevel level, BlockPos pos, BlockState oldLogState) {
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, oldLogState).setPos(pos),
                pos.getX() + 0.5D,
                pos.getY() + 0.35D,
                pos.getZ() + 0.5D,
                FINISH_CHIP_COUNT,
                0.35D,
                0.30D,
                0.35D,
                0.08D
        );
    }

    // A few crumbs off the top of the mound as a laden termite unloads. This only draws: the hive
    // stores nothing for it, so there is nothing here to mark.
    public static void onDeposit(ServerLevel level, BlockPos hivePos, BlockState hiveState) {
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, hiveState).setPos(hivePos),
                hivePos.getX() + 0.5D,
                hivePos.getY() + 1.05D,
                hivePos.getZ() + 0.5D,
                DEPOSIT_CHIP_COUNT,
                0.25D,
                0.10D,
                0.25D,
                0.03D
        );
    }
}
