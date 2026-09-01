package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.entity.HugeBoat;
import net.greenjab.nekomasfixed.render.entity.model.HugeBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class HugeBoatRenderer extends BigBoatRenderer<HugeBoat, HugeBoatModel<HugeBoat>> {

	public HugeBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
		super(context, layer);
	}

	@Override
	public void renderBanners(HugeBoat entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {
		if (!entity.getBanner().is(net.minecraft.tags.ItemTags.BANNERS)) return;
		poseStack.pushPose();
		poseStack.translate(0.0F, 2F, -0.06F);
		this.itemRenderer.renderStatic(entity.getBanner(), ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.0F, 0.85F, -1.5F);
		this.itemRenderer.renderStatic(entity.getBanner(), ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
		poseStack.popPose();
	}

	@NotNull
	@Override
	public HugeBoatModel<HugeBoat> getThisModel(EntityRendererProvider.Context context, ModelLayerLocation layer) {
		return new HugeBoatModel<>(context.bakeLayer(layer));
	}
}
