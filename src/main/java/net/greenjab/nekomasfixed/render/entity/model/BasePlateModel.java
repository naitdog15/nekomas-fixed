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

	/** The seven parts {@code HumanoidModel}'s constructor looks up. */
	private static final String[] HUMANOID_PARTS = {
			"head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"
	};

	public static LayerDefinition getTexturedModelData() {
		// Only the plate is ever drawn, but the humanoid parts still have to exist or the inherited
		// constructor cannot find them. 1.20.1 has no way to strip cubes off a built mesh, so they go
		// in empty from the start.
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
		// The humanoid model only draws its head and body parts; the plate is a sibling of them, so it
		// has to be listed here or it never reaches the buffer.
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.basePlate));
	}

	@Override
	public void setupAnim(TargetDummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.basePlate.yRot = (float) (Math.PI / 180.0) * -this.yaw;
	}
}
