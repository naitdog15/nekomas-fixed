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

// invFriction is a private field, so it's @Shadow-ed instead of captured as a local; the illager
// hook rides a FIELD read (isClientSide), not an INVOKE, since that's what the check actually is.
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
