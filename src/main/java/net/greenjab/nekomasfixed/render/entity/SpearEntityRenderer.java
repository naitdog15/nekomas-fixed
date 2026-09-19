package net.greenjab.nekomasfixed.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.nekomasfixed.registry.entity.SpearEntity;
import net.greenjab.nekomasfixed.render.entity.state.SpearEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;


@Environment(EnvType.CLIENT)
public class SpearEntityRenderer extends EntityRenderer<SpearEntity, SpearEntityRenderState> {
	private final ItemModelResolver itemModelManager;
	
	public SpearEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelManager = context.getItemModelResolver();
	}

	public SpearEntityRenderState createRenderState() {
		return new SpearEntityRenderState();
	}

	public void extractRenderState(SpearEntity spearEntity, SpearEntityRenderState spearEntityRenderState, float f) {
		super.extractRenderState(spearEntity, spearEntityRenderState, f);
		spearEntityRenderState.duration = spearEntity.tickCount;
		spearEntityRenderState.direction = spearEntity.getDirection();

		this.itemModelManager.updateForNonLiving(spearEntityRenderState.spearRenderState, spearEntity.getStack(), ItemDisplayContext.FIXED, spearEntity);
	}

	public void submit(
			SpearEntityRenderState spearEntityRenderState,
			PoseStack matrixStack,
			SubmitNodeCollector orderedRenderCommandQueue,
			CameraRenderState cameraRenderState
	) {
		super.submit(spearEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
		ItemStackRenderState spearRenderState = spearEntityRenderState.spearRenderState;
		if (spearRenderState != null) {
			matrixStack.pushPose();
			matrixStack.translate(new Vec3(0, 0.3, 0));

			Direction dir = spearEntityRenderState.direction;
			if (dir.getAxis().isHorizontal()) {
				matrixStack.translate(new Vec3(0, -0.13, 0));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
				matrixStack.mulPose(Axis.XP.rotationDegrees(-dir.toYRot()+90));
			} else if (dir == Direction.DOWN) {
				matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
			}


			matrixStack.translate(new Vec3(0, -1.1+Math.min(-Math.abs((spearEntityRenderState.ageInTicks-10)/5)+2,1), 0));

			matrixStack.mulPose(Axis.YP.rotationDegrees(45));
			matrixStack.mulPose(Axis.ZP.rotationDegrees(-45));
			matrixStack.scale(1, 1, 0.01f);
			spearRenderState.submit(matrixStack, orderedRenderCommandQueue, spearEntityRenderState.light, OverlayTexture.NO_OVERLAY, 0);
			matrixStack.scale(1, 1,  100f);
			matrixStack.mulPose(Axis.ZP.rotationDegrees(45));
			matrixStack.mulPose(Axis.YP.rotationDegrees(90));
			matrixStack.mulPose(Axis.ZP.rotationDegrees(-45));
			matrixStack.scale(1, 1, 0.01f);
			spearRenderState.submit(matrixStack, orderedRenderCommandQueue, spearEntityRenderState.light, OverlayTexture.NO_OVERLAY, 0);
			matrixStack.scale(1, 1,  100f);
			matrixStack.popPose();
		}
	}
}
