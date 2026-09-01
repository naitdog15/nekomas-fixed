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

/**
 * 1.20.1's {@code HumanoidModel} has no {@code HumanoidRenderState} generic at all — it is
 * {@code HumanoidModel<T extends LivingEntity>}, and {@code poseRightArm}/{@code poseLeftArm} take
 * the {@code LivingEntity} directly (VERIFIED forge-1.20.1-mapped-src HumanoidModel.java:233,279).
 * {@code rightArmPose}/{@code leftArmPose} are already fields on the model itself (lines 39-40, this
 * mixin's own target class), not on a separate state object, so they are read as {@code this.}
 * fields rather than {@code state.}. {@code ticksUsingItem} and the held stack are derived from the
 * entity ({@code getTicksUsingItem()} — VERIFIED LivingEntity.java:3042 — and
 * {@code getItemInHand(...)}, resolved against {@code getMainArm()} the same way vanilla's own
 * dominant-hand logic does) instead of read off a pre-populated state field, and
 * {@code CrossbowItem.getChargeDuration(ItemStack)} (unchanged, static) is called directly here
 * instead of through {@code HumanoidMobRendererMixin}'s now-absorbed extraction-time halving — see
 * that file's own header.
 */
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

    /** Absorbs HumanoidMobRendererMixin's original halving of the crossbow charge duration for a
     * slingshot — there is no separate render-state extraction step on 1.20.1 to hook instead. */
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
