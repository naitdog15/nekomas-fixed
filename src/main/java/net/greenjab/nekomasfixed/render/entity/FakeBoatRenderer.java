package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.entity.FakeBoat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

// invisible on purpose - only exists to be hit/pushed/stood on; texture below is never sampled, just required
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
