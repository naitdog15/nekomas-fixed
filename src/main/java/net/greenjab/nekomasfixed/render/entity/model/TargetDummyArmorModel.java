package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Render-state collapse: {@code HumanoidModel<TargetDummyRenderState>} → {@code HumanoidModel<TargetDummy>}.
 * The custom {@code getEquipmentModelData(...)}/{@code ArmorModelSet} helper is dropped —
 * {@code TargetDummyEntityRenderer} now wires the armor layer onto vanilla's shared
 * {@code ModelLayers.PLAYER_INNER_ARMOR}/{@code PLAYER_OUTER_ARMOR} mesh instead of a mod-owned one
 * (see {@code ModModelLayerRegistry}'s javadoc — armor shape is generic per mob family). This class's
 * remaining job is reading the six named-part rotations directly off the entity.
 */
public class TargetDummyArmorModel extends HumanoidModel<TargetDummy> {
	public TargetDummyArmorModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void setupAnim(TargetDummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.head.xRot = (float) (Math.PI / 180.0) * entity.getHeadRotation().getX();
		this.head.yRot = (float) (Math.PI / 180.0) * entity.getHeadRotation().getY();
		this.head.zRot = (float) (Math.PI / 180.0) * entity.getHeadRotation().getZ();
		this.body.xRot = (float) (Math.PI / 180.0) * entity.getBodyRotation().getX();
		this.body.yRot = (float) (Math.PI / 180.0) * entity.getBodyRotation().getY();
		this.body.zRot = (float) (Math.PI / 180.0) * entity.getBodyRotation().getZ();
		this.leftArm.xRot = (float) (Math.PI / 180.0) * entity.getLeftArmRotation().getX();
		this.leftArm.yRot = (float) (Math.PI / 180.0) * entity.getLeftArmRotation().getY();
		this.leftArm.zRot = (float) (Math.PI / 180.0) * entity.getLeftArmRotation().getZ();
		this.rightArm.xRot = (float) (Math.PI / 180.0) * entity.getRightArmRotation().getX();
		this.rightArm.yRot = (float) (Math.PI / 180.0) * entity.getRightArmRotation().getY();
		this.rightArm.zRot = (float) (Math.PI / 180.0) * entity.getRightArmRotation().getZ();
		this.leftLeg.xRot = (float) (Math.PI / 180.0) * entity.getLeftLegRotation().getX();
		this.leftLeg.yRot = (float) (Math.PI / 180.0) * entity.getLeftLegRotation().getY();
		this.leftLeg.zRot = (float) (Math.PI / 180.0) * entity.getLeftLegRotation().getZ();
		this.rightLeg.xRot = (float) (Math.PI / 180.0) * entity.getRightLegRotation().getX();
		this.rightLeg.yRot = (float) (Math.PI / 180.0) * entity.getRightLegRotation().getY();
		this.rightLeg.zRot = (float) (Math.PI / 180.0) * entity.getRightLegRotation().getZ();
	}
}
