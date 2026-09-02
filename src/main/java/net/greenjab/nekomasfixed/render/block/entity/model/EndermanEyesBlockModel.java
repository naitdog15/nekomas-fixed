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

/** Eye layer for the enderman head block, rendered with the glowing eyes RenderType. */
public class EndermanEyesBlockModel extends Model {
	private final ModelPart root;
	private final ModelPart head;

	public EndermanEyesBlockModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.head = root.getChild("head");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
				.addBox(4.0F, 0.0F, 4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.ZERO);
		return LayerDefinition.create(modelData, 64, 32);
	}

	public void setupAnim(boolean powered, boolean wall) {
		this.head.y = 0;
		if (powered) {
			if (wall) this.head.y -= 2.5F;
			else this.head.y -= 5.0F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
