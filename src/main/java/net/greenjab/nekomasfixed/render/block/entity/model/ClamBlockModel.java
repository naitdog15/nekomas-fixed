package net.greenjab.nekomasfixed.render.block.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

/**
 * 1.20.1's {@code Model} (VERIFIED against {@code forge-1.20.1-mapped-src}) is a plain, non-generic
 * abstract class — no {@code Model<T>} type parameter, no inherited {@code setupAnim}, and
 * {@code renderToBuffer} is abstract with no {@code root()}-based default. Every block-entity model
 * in {@code render/block/entity/model/**} ({@link ClamBlockModel}, {@link ClockBlockModel},
 * {@link EndermanHeadBlockModel}, {@link EndermanEyesBlockModel}) needed this same restructure:
 * store the root {@link ModelPart} directly, implement {@code renderToBuffer} as
 * {@code root.render(...)}, and keep the old {@code setupAnim(...)} as a plain (no longer overriding)
 * method the renderer calls directly before {@code renderToBuffer}.
 */
public class ClamBlockModel extends Model {
	private final ModelPart root;
	private final ModelPart lid;
	private final ModelPart lid_hinge;

	public ClamBlockModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.lid = root.getChild("lid");
		this.lid_hinge = root.getChild("lid_hinge");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 4.0F, 14.0F, 2.0F, 12.0F), PartPose.ZERO);
		modelPartData.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 14).addBox(1F, 0.0F, 3.0F, 14.0F, 2.0F, 12.0F), PartPose.offset(0.0F, 2.0F, 1.0F));
		modelPartData.addOrReplaceChild("bottom_hinge", CubeListBuilder.create().texOffs(18, 28).addBox(5.0F, 0.0F, 1.0F, 6.0F, 2.0F, 3.0F), PartPose.ZERO);
		modelPartData.addOrReplaceChild("lid_hinge", CubeListBuilder.create().texOffs(0, 28).addBox(5.0F, 0.0F, 0.0F, 6.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 2.0F, 1.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	public void setupAnim(float openness) {
		this.lid.xRot = -(openness * (float) (Math.PI / 2));
		this.lid_hinge.xRot = this.lid.xRot;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
