package net.greenjab.nekomasfixed.mixin.boat;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.20.1 has one Boat class, not the 26.2 AbstractBoat/Boat/ChestBoat split (see boat.AbstractBoatMixin's
// header). clientTick() itself is unchanged (VERIFIED BodyRotationControl.java:18).
@Mixin(BodyRotationControl.class)
public class BodyRotationControlMixin {
    @Shadow @Final
    private Mob mob;

    @Inject(method = "clientTick", at = @At(value = "HEAD"), cancellable = true)
    private void notInBoat(CallbackInfo ci) {
        if (mob.isPassenger() && mob.getVehicle() instanceof Boat) {
            ci.cancel();
        }
    }
}
