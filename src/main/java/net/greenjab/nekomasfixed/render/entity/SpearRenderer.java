package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registry.entity.SpearEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws a planted spear as its own item model, crossed with itself so it reads as a shaft from every
 * angle, and rides it up out of the ground over the first half-second it is there.
 */
public class SpearRenderer extends EntityRenderer<SpearEntity> {
	private final ItemRenderer itemRenderer;

	public SpearRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	/** Nothing here is drawn from an entity texture - the spear is its own item model. */
	@Override
	public ResourceLocation getTextureLocation(SpearEntity entity) {
		return MissingTextureAtlasSprite.getLocation();
	}

	@Override
	public void render(SpearEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		ItemStack stack = entity.getStack();
		if (stack.isEmpty()) return;

		float ageInTicks = entity.tickCount + partialTicks;
		poseStack.pushPose();
		poseStack.translate(0, 0.3, 0);

		Direction dir = entity.getDirection();
		if (dir.getAxis().isHorizontal()) {
			poseStack.translate(0, -0.13, 0);
			poseStack.mulPose(Axis.ZP.rotationDegrees(90));
			poseStack.mulPose(Axis.XP.rotationDegrees(-dir.toYRot() + 90));
		} else if (dir == Direction.DOWN) {
			poseStack.mulPose(Axis.ZP.rotationDegrees(180));
		}

		poseStack.translate(0, -1.1 + Math.min(-Math.abs((ageInTicks - 10) / 5) + 2, 1), 0);
		poseStack.mulPose(Axis.YP.rotationDegrees(45));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
		poseStack.scale(1, 1, 0.01f);
		this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
		poseStack.scale(1, 1, 100f);
		poseStack.mulPose(Axis.ZP.rotationDegrees(45));
		poseStack.mulPose(Axis.YP.rotationDegrees(90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
		poseStack.scale(1, 1, 0.01f);
		this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
		poseStack.scale(1, 1, 100f);
		poseStack.popPose();
	}
}
