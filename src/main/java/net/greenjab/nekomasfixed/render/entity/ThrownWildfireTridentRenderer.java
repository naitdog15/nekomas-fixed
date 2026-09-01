package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.WildfireTrident;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Render-state collapse: {@code EntityRenderer<WildfireTrident, ThrownTridentRenderState>} →
 * {@code EntityRenderer<WildfireTrident>}; {@code submit(...)} → the classic {@code render(...)}. */
public class ThrownWildfireTridentRenderer extends EntityRenderer<WildfireTrident> {
	public static final ResourceLocation TEXTURE = NekomasFixed.id("textures/entity/wildfire_trident/default.png");
	private final TridentModel model;

	public ThrownWildfireTridentRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new TridentModel(context.bakeLayer(ModModelLayerRegistry.WILDFIRE_TRIDENT));
	}

	@Override
	public ResourceLocation getTextureLocation(WildfireTrident entity) {
		return TEXTURE;
	}

	@Override
	public void render(WildfireTrident entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot(partialTicks) - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot(partialTicks) + 90.0F));
		VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(TEXTURE), false, entity.isEnchanted());
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
