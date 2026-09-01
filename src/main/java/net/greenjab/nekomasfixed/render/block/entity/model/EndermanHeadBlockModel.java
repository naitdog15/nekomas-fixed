package net.greenjab.nekomasfixed.render.block.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

/** See {@link ClamBlockModel}'s javadoc. {@code setupAnim(S state)} → a plain
 * {@code setupAnim(boolean powered, boolean wall)} taking the two fields the renderer used to read
 * off the render state, now read directly off the block entity / block state instead. */
public class EndermanHeadBlockModel extends Model {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart mouth;

	public EndermanHeadBlockModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.head = root.getChild("head");
		this.mouth = root.getChild("mouth");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
				.addBox(4.0F, 0.0F, 4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
		modelPartData.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 16)
				.addBox(4.0F, 0.0F, 4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.ZERO);

		return LayerDefinition.create(modelData, 64, 32);
	}

	public void setupAnim(boolean powered, boolean wall) {
		this.head.y = 0;
		this.mouth.y = 0;
		if (powered) {
			if (wall) {
				this.head.y -= 2.5F;
				this.mouth.y += 2.5F;
			} else this.head.y -= 5.0F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
