package net.greenjab.nekomasfixed.registry.entity.goal;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.Moobloom;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import java.util.List;

public class PollinatingMoobloomGoal extends Goal {
    private final Bee bee;
    private Moobloom target;
    // canUse() runs every tick for every bee, and the scan below is not cheap, so only look
    // about once a second. The offset keeps a hive's worth of bees off the same tick.
    private int nextScanTick;
    private int runTicks;

    public PollinatingMoobloomGoal(Bee bee) {this.bee = bee;}

    @Override
    public boolean canUse() {
        if (!NekomasFixedConfig.BEES_POLLINATE_MOOBLOOMS.get()) {return false;}
        if (bee.hasNectar()) {return false;}
        if (bee.tickCount < this.nextScanTick) {return false;}
        this.nextScanTick = bee.tickCount + 20;
        List<Moobloom> list = bee.level().getEntitiesOfClass(Moobloom.class, bee.getBoundingBox().inflate(8), entity -> !entity.getEntityData().get(Moobloom.SHEARED));
        if (list.isEmpty()) {return false;}

        this.target = list.get(0);
        return true;
    }

    @Override
    public void start() {
        this.runTicks = 0;
        bee.getNavigation().moveTo(target, 1.2D);
    }

    @Override
    public void tick() {
        this.runTicks++;
        if (target == null) return;
        bee.getLookControl().setLookAt(target);

        if (bee.distanceToSqr(target) < 2.0D) {
            bee.setHasNectar(true);

        } else {
            bee.getNavigation().moveTo(target, 1.2D);
        }
    }

    @Override
    public boolean canContinueToUse() {
        // the 200 stops a bee locking onto a moobloom it can never path to and never pollinating again
        return NekomasFixedConfig.BEES_POLLINATE_MOOBLOOMS.get() && this.runTicks < 200
                && target != null && target.isAlive() && !bee.hasNectar() && !target.getEntityData().get(Moobloom.SHEARED);
    }

    @Override
    public boolean isInterruptable(){
        return bee.hasNectar();
    }
}
