package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.greenjab.nekomasfixed.registry.entity.WildFire.FireBomb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-state collapse: {@code submitCustomGeometry(matrices, layer, (entry, vertexConsumer) -> {...})}
 * (26.2's batched pipeline) → a direct {@code VertexConsumer} quad built inline in the classic
 * {@code render(...)} override, using the 1.20.1 fluent {@code vertex().color().uv().overlayCoords()
 * .uv2().normal().endVertex()} builder chain. {@code cameraState.orientation} → the render
 * dispatcher's own {@code cameraOrientation()} (a protected field on {@code EntityRenderer}, the
 * classic billboard-facing hook).
 */
public class FireBombRenderer extends EntityRenderer<FireBomb> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/item/blaze_powder.png");
	private static final RenderType LAYER = RenderType.entityTranslucentEmissive(TEXTURE);

	public FireBombRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected int getBlockLightLevel(FireBomb fireBombEntity, BlockPos blockPos) {
		return 15;
	}

	@Override
	public ResourceLocation getTextureLocation(FireBomb entity) {
		return TEXTURE;
	}

	@Override
	public void render(FireBomb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		VertexConsumer vertexConsumer = buffer.getBuffer(LAYER);
		PoseStack.Pose pose = poseStack.last();
		produceVertex(vertexConsumer, pose, packedLight, 0.0F, 0, 0, 1);
		produceVertex(vertexConsumer, pose, packedLight, 1.0F, 0, 1, 1);
		produceVertex(vertexConsumer, pose, packedLight, 1.0F, 1, 1, 0);
		produceVertex(vertexConsumer, pose, packedLight, 0.0F, 1, 0, 0);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	public static void produceVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, int light, float x, int z, int textureU, int textureV) {
		vertexConsumer.vertex(pose.pose(), x - 0.5F, z - 0.25F, 0.0F)
				.color(255, 255, 255, 255)
				.uv(textureU, textureV)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(light)
				.normal(pose.normal(), 0.0F, 1.0F, 0.0F)
				.endVertex();
	}
}
