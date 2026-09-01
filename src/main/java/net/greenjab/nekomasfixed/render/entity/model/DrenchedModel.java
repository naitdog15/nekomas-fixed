package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Render-state collapse: {@code SkeletonModel<DrenchedRenderState>} (26.2 package
 * {@code .monster.skeleton}) → 1.20.1's real {@code net.minecraft.client.model.SkeletonModel<T extends
 * Mob & RangedAttackMob>}. {@code state.swimAmount} needs no stashing here: {@code HumanoidModel}
 * already fills its own {@code swimAmount} field from {@code entity.getSwimAmount(partialTick)} in
 * {@code prepareMobModel}, which is exactly what the Drowned arm pose this mirrors reads.
 */
public class DrenchedModel extends SkeletonModel<Drenched> {

    public DrenchedModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition modelPartData = modelData.getRoot();

        // 1.20.1's SkeletonModel has no createDefaultSkeletonMesh(PartDefinition) helper to reuse —
        // its own createBodyLayer() builds a whole mesh from scratch. Replicated verbatim here to
        // replace HumanoidModel's full-width arms/legs with the thin skeleton ones.
        modelPartData.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition head = modelPartData.getChild("head");
        PartDefinition bone2Data = head.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.ZERO);

        bone2Data.addOrReplaceChild("plane_1",
                CubeListBuilder.create().texOffs(33, 1).addBox(-4.0F, -9.0F, 0.0F, 8.0F, 9.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));

        bone2Data.addOrReplaceChild("plane_2",
                CubeListBuilder.create().texOffs(33, 1).addBox(-4.0F, -9.0F, 0.0F, 8.0F, 9.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, -((float)Math.PI / 4F), 0.0F));

        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void setupAnim(Drenched entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (this.swimAmount > 0.0F) {
            this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, (float) (-Math.PI * 4.0 / 5.0 - 90 * Math.PI / 180.0)) + this.swimAmount * 0.35F * Mth.sin((0.1F * ageInTicks));
            this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, (float) (-Math.PI * 4.0 / 5.0 - 90 * Math.PI / 180.0)) - this.swimAmount * 0.35F * Mth.sin((0.1F * ageInTicks));
            this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15F - (float) (Math.PI));
            this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15F - (float) (Math.PI));
            this.leftLeg.xRot -= this.swimAmount * 0.55F * Mth.sin((0.1F * ageInTicks));
            this.rightLeg.xRot += this.swimAmount * 0.55F * Mth.sin((0.1F * ageInTicks));
            this.head.xRot = 0.0F;
        }
    }
}
