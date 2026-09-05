package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// spear holds level instead of winding up an overhead swing, reads as a thrust across the field.
@Mixin(IllagerModel.class)
public abstract class IllagerModelMixin {

    @WrapOperation(
            method = "setupAnim(Lnet/minecraft/world/entity/monster/AbstractIllager;FFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/AnimationUtils;swingWeaponDown(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/world/entity/Mob;FF)V"))
    private void spearArmPose(ModelPart rightArm, ModelPart leftArm, Mob illager, float attackTime,
                              float ageInTicks, Operation<Void> original) {
        if (NekomasFixedConfig.SPEAR_INTERACTIONS.get() && illager.getMainHandItem().is(ModTags.SPEARS)) {
            rightArm.xRot = (float) (Math.PI / 180.0) * -25.0F;
        } else {
            original.call(rightArm, leftArm, illager, attackTime, ageInTicks);
        }
    }
}
