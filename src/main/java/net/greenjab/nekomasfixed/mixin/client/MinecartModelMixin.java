package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the minecart's geometry with the rebuilt cart (chassis, railings, chain hitch and four
 * turning wheels) and spins those wheels as the cart travels.
 *
 * <p>The wheel spin used to live in a {@code MinecartModel} subclass swapped in from the renderer.
 * There is no render-state object to read here - {@code MinecartRenderer} hands the model the live
 * entity - so the animation is done on the vanilla model itself and the subclass is gone. All
 * minecart layers bake from this one method, so replacing it covers every cart variant.
 */
@Mixin(MinecartModel.class)
public class MinecartModelMixin {

    @Unique private ModelPart nekomasfixed$wheel1;
    @Unique private ModelPart nekomasfixed$wheel2;
    @Unique private ModelPart nekomasfixed$wheel3;
    @Unique private ModelPart nekomasfixed$wheel4;
    @Unique private boolean nekomasfixed$wheelsResolved;

    @Inject(method = "createBodyLayer", at = @At("HEAD"), cancellable = true)
    private static void useCustomMinecartModel(CallbackInfoReturnable<LayerDefinition> cir) {
        cir.setReturnValue(nekomasfixed$customBodyLayer());
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void rollWheels(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                            float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!this.nekomasfixed$wheelsResolved) {
            this.nekomasfixed$wheelsResolved = true;
            ModelPart modelRoot = ((MinecartModel<?>) (Object) this).root();
            if (modelRoot.hasChild("group") && modelRoot.getChild("group").hasChild("wheels")) {
                ModelPart wheels = modelRoot.getChild("group").getChild("wheels");
                this.nekomasfixed$wheel1 = wheels.getChild("wheel1");
                this.nekomasfixed$wheel2 = wheels.getChild("wheel2");
                this.nekomasfixed$wheel3 = wheels.getChild("wheel3");
                this.nekomasfixed$wheel4 = wheels.getChild("wheel4");
            }
        }
        if (this.nekomasfixed$wheel1 == null) return;

        // roll is driven by how far into the block the cart sits along whichever axis it is facing,
        // signed so the wheels turn the way the cart is actually moving
        float yaw = entity.getYRot();
        double travelled = (entity.getX() - (int) entity.getX()) * (Mth.degreesDifferenceAbs(yaw, 180.0F) < 90.0F ? 1 : -1)
                + (entity.getZ() - (int) entity.getZ()) * (Mth.degreesDifferenceAbs(yaw, 90.0F) < 90.0F ? 1 : -1);
        float roll = (float) Math.toRadians(360.0D * travelled);

        this.nekomasfixed$wheel1.zRot = roll;
        this.nekomasfixed$wheel2.zRot = roll;
        this.nekomasfixed$wheel3.zRot = roll;
        this.nekomasfixed$wheel4.zRot = roll;
    }

    @Unique
    private static LayerDefinition nekomasfixed$customBodyLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition group = modelPartData.addOrReplaceChild("group", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -4F, 0.0F, 0.0F, 0F, 0.0F));

        group.addOrReplaceChild("base", CubeListBuilder.create().texOffs(19, 17).addBox(-11.0F, -1.0F, -9.0F, 22.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0556F, 0.0F));

        PartDefinition top = group.addOrReplaceChild("top", CubeListBuilder.create().texOffs(9, 4).addBox(-10.4544F, -4.1927F, 7.4385F, 22.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(82, 10).addBox(9.5456F, -4.1927F, -6.5615F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(9, 0).addBox(-10.4544F, -4.1927F, -8.5615F, 22.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5456F, 0.2482F, -0.4385F));

        top.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -3.5F, -1.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 5).addBox(-19.5F, -3.5F, -1.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(9.7956F, 0.3073F, -6.5615F, 0.0873F, 0.0F, 0.0F));

        top.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(7, 3).addBox(-0.5F, -3.5F, -7.0F, 1.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.7456F, 1.3073F, 0.4385F, 0.0F, 0.0F, 0.0873F));

        top.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -3.5F, 0.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 5).addBox(-19.5F, -3.5F, 0.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(9.7956F, 0.3073F, 7.4385F, -0.0873F, 0.0F, 0.0F));

        top.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 5).addBox(0.0F, -3.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 5).addBox(-19.5F, -3.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(9.7956F, 0.3073F, 6.2385F, -0.0873F, 0.0F, 0.0F));

        top.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(27, 8).addBox(-10.0F, -3.5F, -0.5F, 20.0F, 7.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.5456F, 1.2573F, 7.4885F, -0.1309F, 0.0F, 0.0F));

        top.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(7, 3).mirror().addBox(-0.5F, -3.5F, -7.0F, 1.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.6794F, 1.3073F, 0.4385F, 0.0F, 0.0F, -0.0873F));

        top.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 5).addBox(0.0F, -3.5F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 5).addBox(-19.5F, -3.5F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(9.7956F, 0.3073F, -5.3615F, 0.0873F, 0.0F, 0.0F));

        top.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(6, 8).addBox(-10.0F, -3.5F, -0.5F, 20.0F, 7.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.5456F, 1.2573F, -6.6115F, 0.1309F, 0.0F, 0.0F));

        top.addOrReplaceChild("front_tile", CubeListBuilder.create().texOffs(82, 10).addBox(-1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.4544F, -3.1927F, 0.4385F));

        PartDefinition chain = group.addOrReplaceChild("chain", CubeListBuilder.create(), PartPose.offset(-2.4375F, 4.7569F, 0.5F));

        chain.addOrReplaceChild("chain_w_hook", CubeListBuilder.create().texOffs(10, 18).mirror().addBox(-3.875F, -0.5F, 0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 18).addBox(-3.875F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 18).mirror().addBox(-3.875F, -0.5F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 18).addBox(-1.875F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.6875F, -0.2569F, 0.0F));

        PartDefinition separate_chain = chain.addOrReplaceChild("separate_chain", CubeListBuilder.create(), PartPose.offset(13.6042F, 0.7431F, 0.0F));

        separate_chain.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(11, 18).mirror().addBox(-0.75F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 18).addBox(-0.75F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 18).mirror().addBox(-0.75F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0833F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        chain.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(1, 15).mirror().addBox(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(1, 15).mirror().addBox(-0.5F, -1.0F, 0.5F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-9.0625F, -1.2014F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition wheels = group.addOrReplaceChild("wheels", CubeListBuilder.create(), PartPose.offset(6.5F, 8.5556F, 5.1667F));

        wheels.addOrReplaceChild("wheel1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, 0.8333F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -0.5F, -2.1667F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.5F, -1.1667F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        wheels.addOrReplaceChild("wheel2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, 0.8333F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -0.5F, -2.1667F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.5F, -1.1667F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, 0.0F, 0.0F));

        wheels.addOrReplaceChild("wheel3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, 0.8333F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -0.5F, -0.1667F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).mirror().addBox(-1.5F, -1.5F, -1.1667F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-13.0F, 0.0F, -11.0F));

        wheels.addOrReplaceChild("wheel4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, 0.8333F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -0.5F, -0.1667F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.5F, -1.1667F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -11.0F));

        group.addOrReplaceChild("foundation", CubeListBuilder.create().texOffs(58, 0).addBox(-10.0F, -1.0F, -3.0F, 20.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0556F, 0.0F));
        return LayerDefinition.create(modelData, 128, 64);
    }
}
