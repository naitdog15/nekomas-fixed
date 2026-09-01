package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.entity.FakeBoat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-state collapse: the original {@code createRenderState}/{@code submit} pair had no body
 * beyond delegating to the vanilla defaults (FakeBoat renders nothing itself — it exists purely as an
 * invisible collision/interaction anchor). {@code EntityRenderer<FakeBoat>} needs a
 * {@code getTextureLocation} override regardless (abstract on {@code EntityRenderer}); it is never
 * actually sampled since {@link #render} never submits any geometry.
 */
public class FakeBoatRenderer extends EntityRenderer<FakeBoat> {

	public FakeBoatRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(FakeBoat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(FakeBoat entity) {
		return new ResourceLocation("minecraft", "textures/entity/boat/oak.png");
	}
}
