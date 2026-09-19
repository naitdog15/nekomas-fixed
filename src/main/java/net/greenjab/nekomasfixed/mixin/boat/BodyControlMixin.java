package net.greenjab.nekomasfixed.mixin.boat;

import net.minecraft.world.mob.ai.control.BodyRotationControl;
import net.minecraft.world.mob.Mob;
import net.minecraft.world.mob.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BodyRotationControl.class)
public class BodyControlMixin {
    @Shadow @Final
    private Mob mob;

    @Inject(method = "clientTick", at = @At(value = "HEAD"), cancellable = true)
    private void notInBoat(CallbackInfo ci) {
        if (mob.isPassenger() && mob.getVehicle() instanceof AbstractBoat) {
            ci.cancel();
        }
    }
}