package net.greenjab.nekomasfixed.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

// yaw needs partialTick, unavailable in setupAnim's 5-float signature, so BasePlateFeatureRenderer
// sets this field before calling setupAnim - same pattern as WildfireModel.bodyRot
public class BasePlateModel extends TargetDummyArmorModel {

	private final ModelPart basePlate;
	public float yaw;

	public BasePlateModel(ModelPart modelPart) {
		super(modelPart);
		this.basePlate = modelPart.getChild("base_plate");
	}

	/** The seven parts {@code HumanoidModel}'s constructor looks up. */
	private static final String[] HUMANOID_PARTS = {
			"head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"
	};

	public static LayerDefinition getTexturedModelData() {
		// humanoid parts still have to exist or the inherited constructor can't find them - 1.20.1 has
		// no way to strip cubes off a built mesh, so they go in empty
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		for (String part : HUMANOID_PARTS) {
			modelPartData.addOrReplaceChild(part, CubeListBuilder.create(), PartPose.ZERO);
		}
		modelPartData.addOrReplaceChild("base_plate", CubeListBuilder.create().texOffs(0, 32)
				.addBox(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.offset(0.0F, 12.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		// plate is a sibling part, not drawn by the humanoid model, so it must be listed here or it never reaches the buffer
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.basePlate));
	}

	@Override
	public void setupAnim(TargetDummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.basePlate.yRot = (float) (Math.PI / 180.0) * -this.yaw;
	}
}
