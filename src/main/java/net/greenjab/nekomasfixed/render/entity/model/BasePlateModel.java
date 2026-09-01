package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.player.PlayerModel;

/**
 * {@code yaw} needs {@code partialTick}-interpolated body rotation, unavailable inside
 * {@code setupAnim}'s 5-float signature; {@link net.greenjab.nekomasfixed.render.entity.feature.BasePlateFeatureRenderer}
 * (a {@code RenderLayer}, whose classic {@code render(...)} override does receive {@code partialTick})
 * sets this field before calling {@code setupAnim} — same pattern as {@code WildfireModel.bodyRot}.
 */
public class BasePlateModel extends TargetDummyArmorModel {

	private final ModelPart basePlate;
	public float yaw;

	public BasePlateModel(ModelPart modelPart) {
		super(modelPart);
		this.basePlate = modelPart.getChild("base_plate");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = PlayerModel.createMesh(CubeDeformation.NONE, false);
		PartDefinition modelPartData = modelData.getRoot().clearRecursively();
		modelPartData.addOrReplaceChild("base_plate", CubeListBuilder.create().texOffs(0, 32)
				.addBox(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.offset(0.0F, 12.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(TargetDummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.basePlate.yRot = (float) (Math.PI / 180.0) * -this.yaw;
	}
}
