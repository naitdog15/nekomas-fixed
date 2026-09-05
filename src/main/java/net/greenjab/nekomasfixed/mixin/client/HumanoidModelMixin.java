package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.model.HumanoidModel.ArmPose.BOW_AND_ARROW;

// both arm poses are replaced outright, resolved against the entity's main arm the same way
// vanilla's own dominant-hand logic does.
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {

    @Inject(method = "poseRightArm", at = @At(value = "HEAD"), cancellable = true)
    private void rightArmSlingshot(T entity, CallbackInfo ci) {
        HumanoidModel<?> BEM = (HumanoidModel<?>) (Object) this;
        if (BEM.rightArmPose == BOW_AND_ARROW) {
            boolean rightIsMain = entity.getMainArm() == HumanoidArm.RIGHT;
            ItemStack stack = entity.getItemInHand(rightIsMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            charge(BEM.rightArm, BEM.leftArm, BEM.head, chargeDuration(stack), entity.getTicksUsingItem(), true, stack.is(ItemRegistry.SLINGSHOT.get()));
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At(value = "HEAD"), cancellable = true)
    private void leftArmSlingshot(T entity, CallbackInfo ci) {
        HumanoidModel<?> BEM = (HumanoidModel<?>) (Object) this;
        if (BEM.leftArmPose == BOW_AND_ARROW) {
            boolean leftIsMain = entity.getMainArm() == HumanoidArm.LEFT;
            ItemStack stack = entity.getItemInHand(leftIsMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            charge(BEM.rightArm, BEM.leftArm, BEM.head, chargeDuration(stack), entity.getTicksUsingItem(), false, stack.is(ItemRegistry.SLINGSHOT.get()));
            ci.cancel();
        }
    }

    @Unique
    private static float chargeDuration(ItemStack stack) {
        float duration = CrossbowItem.getChargeDuration(stack);
        return stack.is(ItemRegistry.SLINGSHOT.get()) ? duration / 2 : duration;
    }

    @Unique
    private static void charge(ModelPart holdingArm, ModelPart pullingArm,ModelPart head, float crossbowPullTime, float f, boolean rightArm, boolean slingshot) {
        ModelPart modelPart = rightArm ? holdingArm : pullingArm;
        ModelPart modelPart2 = rightArm ? pullingArm : holdingArm;

        modelPart.xRot = Mth.clamp(head.xRot, -1.2F, 1.2F) - 1.4835298F;
        modelPart.yRot = 0.7f*head.yRot - (float) (Math.PI / 12) * (rightArm ? 1 : -1);

        modelPart2.xRot = modelPart.xRot+(slingshot?-0.7f:0);
        float g = Mth.clamp(f, 0.0F, crossbowPullTime);
        float h = g / crossbowPullTime;
        modelPart2.yRot = (float) (-Math.sin(modelPart2.xRot)*Mth.lerp(h, 0.4F, 0.85F) * (rightArm ? 1 : -1));
        modelPart2.zRot = (float) (Math.cos(modelPart2.xRot)*Mth.lerp(h, 0.4F, 0.85F) * (rightArm ? 1 : -1));
    }
}
