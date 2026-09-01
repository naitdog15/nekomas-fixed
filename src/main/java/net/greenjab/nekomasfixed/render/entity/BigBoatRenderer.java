package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registry.entity.BigBoat;
import net.greenjab.nekomasfixed.render.entity.model.BigBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

/**
 * Render-state collapse: {@code EntityRenderer<T, S extends BigBoatRenderState>} →
 * {@code EntityRenderer<T>}; {@code submit(state, ..., SubmitNodeCollector, CameraRenderState)} → the
 * classic {@code render(entity, entityYaw, partialTicks, PoseStack, MultiBufferSource, int)}. The
 * banner is no longer a precomputed {@code ItemStackRenderState} field — it's rendered directly each
 * frame via {@link ItemRenderer#renderStatic}, the same classic 1.20.1 API vanilla itself uses for a
 * held/standing banner render.
 */
public class BigBoatRenderer<T extends BigBoat, M extends BigBoatModel<T>> extends EntityRenderer<T> {
	private final ResourceLocation texture;
	protected final BigBoatModel<T> model;
	protected final ItemRenderer itemRenderer;

	public BigBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.shadowRadius = 0.8F;
		this.texture = new ResourceLocation(layer.getModel().getNamespace(), "textures/entity/" + layer.getModel().getPath() + ".png");
		this.model = getThisModel(context, layer);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.texture;
	}

	@SuppressWarnings("unchecked")
	public M getThisModel(EntityRendererProvider.Context context, ModelLayerLocation layer) {
		return (M) new BigBoatModel<T>(context.bakeLayer(layer));
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.375F, 0.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
		float hurtTime = entity.getHurtTime() - partialTicks;
		float damage = Math.max(entity.getDamage() - partialTicks, 0.0F);
		if (damage > 0.0F) {
			poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * entity.getHurtDir()));
		}
		if (!entity.isUnderWater() && !Mth.equal(entity.getBubbleAngle(partialTicks), 0.0F)) {
			poseStack.mulPose(new Quaternionf().setAngleAxis(entity.getBubbleAngle(partialTicks) * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
		}
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.prepareMobModel(entity, 0, 0, partialTicks);
		this.model.setupAnim(entity, 0, 0, 0, 0, 0);
		VertexConsumer vertexConsumer = buffer.getBuffer(this.getRenderLayer());
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		renderBanners(entity, poseStack, buffer, packedLight, partialTicks);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	public void renderBanners(T entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {
		if (!entity.getBanner().is(ItemTags.BANNERS)) return;
		poseStack.pushPose();
		poseStack.translate(0.0F, 1F, 0.125F);
		this.itemRenderer.renderStatic(entity.getBanner(), ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
		poseStack.popPose();
	}

	protected RenderType getRenderLayer() {
		return this.model.renderType(this.texture);
	}
}
