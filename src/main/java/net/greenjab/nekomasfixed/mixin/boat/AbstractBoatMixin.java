package net.greenjab.nekomasfixed.mixin.boat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.entity.BigBoat;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AbstractBoatMixin's 8 {@code @At} targets were the largest single cluster of unverified retargets
 * in the port; all 8 are now VERIFIED against forge-1.20.1-mapped-src Boat.java. The 26.2
 * {@code AbstractBoat}/{@code Boat}/{@code ChestBoat} split (a raft-support refactor) does not exist
 * on 1.20.1: there is one {@code Boat} class, and — the good news — every method this mixin targets
 * exists on it under the identical name and, with two exceptions below, the identical call shape, so
 * this turned out to be a near-verbatim retarget once the class rename was found.
 * <p>
 * Exception 1: {@code floatBoat()}'s friction value the mixin needs is {@code this.invFriction}, a
 * private FIELD read at the {@code setDeltaMovement(DDD)} call site (Boat.java:603), not a captured
 * local — {@code @Local} cannot bind it, so it is {@code @Shadow}ed instead.
 * <p>
 * Exception 2: {@code tick()}'s two {@code this.level().isClientSide} checks (Boat.java:269,289 —
 * the second is the one this mixin wants, unchanged {@code ordinal = 1}) are FIELD reads on 1.20.1,
 * not the {@code isClientSide()} METHOD call 26.2 has (VERIFIED: {@code Level.isClientSide} is
 * {@code public final boolean}) — retargeted from an INVOKE {@code @At} to a FIELD one.
 */
@Mixin(Boat.class)
public abstract class AbstractBoatMixin {

    @Shadow private float deltaRotation;

    @Shadow private float invFriction;

    @Shadow protected abstract void controlBoat();

    @Shadow protected abstract int getMaxPassengers();

    @WrapOperation(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setYRot(F)V"))
    private void adjustTurningForBigBoat(Boat boat, float v, Operation<Void> original){
        float f = 1.0f;
        if (boat instanceof BigBoat bigBoat) f= bigBoat.getRotationSpeed();
        original.call(boat, boat.getYRot() + deltaRotation*f);
    }

    @WrapOperation(method = "positionRider", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setYRot(F)V"))
    private void adjustTurningForBigBoat2(Entity boat, float yRot, Operation<Void> original){
        float f = 1.0f;
        Boat ABE = (Boat)(Object)this;
        if (ABE instanceof BigBoat bigBoat) f= bigBoat.getRotationSpeed();
        original.call(boat, boat.getYRot() + deltaRotation*f);
    }
    @WrapOperation(method = "positionRider", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setYHeadRot(F)V"))
    private void adjustTurningForBigBoat3(Entity boat, float yHeadRot, Operation<Void> original){
        float f = 1.0f;
        Boat ABE = (Boat)(Object)this;
        if (ABE instanceof BigBoat bigBoat) f= bigBoat.getRotationSpeed();
        original.call(boat, boat.getYHeadRot() + deltaRotation*f);
    }
    @ModifyExpressionValue(method = "positionRider", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int animalsFaceSideways(int original){
        if (original <2 ) return original;
        return getMaxPassengers();
    }

    @Inject(method = "clampRotation", at = @At(value = "HEAD"), cancellable = true)
    private void adjustTurningForBigBoat4(Entity passenger, CallbackInfo ci){
        Boat ABE = (Boat)(Object)this;
        if (!(passenger instanceof Player)) {
            passenger.setYBodyRot(ABE.getYRot());
            float f = Mth.wrapDegrees(passenger.getYHeadRot() - ABE.getYRot());
            float g = Mth.clamp(f, -105.0F, 105.0F)+ABE.getYRot();
            passenger.yRotO += g;
            passenger.setYRot(g);
            passenger.setYHeadRot(g);
            ci.cancel();
        }
    }

    @WrapOperation(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void adjustAccelerationForBigBoat(Boat instance, Vec3 vec3, Operation<Void> original, @Local float acceleration){
        Boat ABE = (Boat)(Object)this;
        if (ABE instanceof BigBoat bigBoat) acceleration *= bigBoat.getSpeed();
        original.call(instance,
                ABE.getDeltaMovement().add(Mth.sin(-ABE.getYRot() * (float) (Math.PI / 180.0)) * acceleration, 0.0, Mth.cos(ABE.getYRot() * (float) (Math.PI / 180.0)) * acceleration)
        );
    }

    @WrapOperation(method = "floatBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setDeltaMovement(DDD)V", ordinal = 0))
    private void adjustSpeedForBigBoat2(Boat instance, double x, double y, double z, Operation<Void> original){
        Boat ABE = (Boat)(Object)this;
        float adjustedInvFriction = this.invFriction;
        if (ABE instanceof BigBoat bigBoat) adjustedInvFriction = 1-(1- adjustedInvFriction)/(bigBoat.getSpeed()*3.0f);
        Vec3 vec3d = ABE.getDeltaMovement();
        original.call(instance, vec3d.x * adjustedInvFriction, y, vec3d.z * adjustedInvFriction);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 1))
    private boolean letIllagerControl(boolean original) {
        if (!original) {
            Boat ABE = (Boat)(Object)this;
            if (ABE.getFirstPassenger() instanceof Raider) {
                this.controlBoat();
            }
        }
        return original;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stopTurnWhenEmpty(CallbackInfo ci) {
        Boat ABE = (Boat)(Object)this;
        if (!(ABE.getFirstPassenger() instanceof Player ||ABE.getFirstPassenger() instanceof Raider)) {
            deltaRotation=0;
        }
    }
}
