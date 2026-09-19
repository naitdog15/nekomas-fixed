package net.greenjab.nekomasfixed.registry.entity.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Ocelot;

// its own class so a monster that joins the level twice can be checked for it instead of collecting copies
public class AvoidTrustingOcelotGoal extends AvoidEntityGoal<Ocelot> {
    public AvoidTrustingOcelotGoal(PathfinderMob mob) {
        super(mob, Ocelot.class, target -> target instanceof Ocelot ocelot && ocelot.isTrusting(), 8.0F, 1.0D, 1.3D, livingEntity -> true);
    }
}
