package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registry.block.FloorClockBlock;
import net.greenjab.nekomasfixed.registry.block.WallClockBlock;
import net.greenjab.nekomasfixed.registry.block.entity.ClockBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class ClockBlockEntityRenderer implements BlockEntityRenderer<ClockBlockEntity> {
	private final ItemRenderer itemRenderer;
	private final Font font;

	public ClockBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
		this.font = context.getFont();
	}

	@Override
	public void render(ClockBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState blockState = blockEntity.getBlockState();
		boolean wall = blockState.getBlock() instanceof WallClockBlock;
		net.minecraft.core.Direction facing = wall ? blockState.getValue(WallClockBlock.FACING) : null;
		int segment = wall ? RotationSegment.convertToSegment(facing.getOpposite()) : blockState.getValue(FloorClockBlock.ROTATION);
		float yaw = RotationSegment.convertToDegrees(segment);

		boolean bell = blockEntity.hasBell();
		int timer = blockEntity.getTimer();
		int dayTime = blockEntity.getShowsTime() && !bell
				? (int) ((blockEntity.getLevel().getDayTime() + 6000) % 24000)
				: -1;

		int color = FastColor.ARGB32.color(255, 255, 255, 255);
		if (dayTime != -1) {
			int hour = dayTime / 1000;
			int min = ((dayTime % 1000) * 60) / 1000;
			String text = (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
			drawFloatingText(text, poseStack, buffer, packedLight, wall, yaw, color);
		} else {
			int time = timer + 20;
			int min = time / 1200;
			int sec = (time - min * 1200) / 20;
			if (time > 20) {
				String text = (min != 0 ? min + " Minute" + (min != 1 ? "s" : "") + ", " : "") + sec + " Second" + (sec != 1 ? "s" : "");
				drawFloatingText(text, poseStack, buffer, packedLight, wall, yaw, color);
			}
		}

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		if (!wall && timer > -ClockBlockEntity.timerDuration && timer < 0) {
			poseStack.mulPose(Axis.YP.rotationDegrees(10 * (timer % 2 == 0 ? 1 : -1)));
		}
		renderClock(blockEntity, poseStack, buffer, packedLight, yaw, wall);
		if (!wall) {
			renderStand(blockEntity, poseStack, buffer, packedLight, yaw);
			if (bell) renderBell(blockEntity, poseStack, buffer, packedLight, yaw);
		}
		poseStack.popPose();
	}

	private void drawFloatingText(String text, PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean wall, float yaw, int color) {
		poseStack.pushPose();
		if (wall) {
			poseStack.translate(-0.4 * Math.sin(yaw * Math.PI / 180.0) + 0.5, 0.75, 0.4 * Math.cos(yaw * Math.PI / 180.0) + 0.5);
		} else {
			poseStack.translate(0.5, 0.5, 0.5);
		}
		poseStack.mulPose(net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
		poseStack.scale(-0.025F, -0.025F, 0.025F);
		Component component = Component.literal(text);
		float x = -this.font.width(component) / 2.0F;
		poseStack.translate(0, 0, 0);
		this.font.drawInBatch(component, x, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
		poseStack.popPose();
	}

	private void renderClock(ClockBlockEntity blockEntity, PoseStack matrices, MultiBufferSource queue, int light, float rotationDegrees, boolean wall) {
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
		matrices.translate(0, wall ? 0 : -0.15, wall ? 0.46875 : -0.1);
		if (!wall) matrices.mulPose(Axis.XP.rotationDegrees(30));
		float scale = wall ? 1 : 0.8f;
		matrices.scale(scale, scale, scale);
		this.itemRenderer.renderStatic(Items.CLOCK.getDefaultInstance(), ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, queue,
				blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong());
		matrices.popPose();
	}

	private void renderStand(ClockBlockEntity blockEntity, PoseStack matrices, MultiBufferSource queue, int light, float rotationDegrees) {
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
		matrices.translate(0, -0.35, 0.2);
		matrices.mulPose(Axis.XP.rotationDegrees(-30));
		matrices.scale(1f, 1.6f, 1f);
		this.itemRenderer.renderStatic(Items.SPRUCE_FENCE_GATE.getDefaultInstance(), ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, queue,
				blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong() + 1);
		matrices.popPose();
	}

	private void renderBell(ClockBlockEntity blockEntity, PoseStack matrices, MultiBufferSource queue, int light, float rotationDegrees) {
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
		matrices.translate(0, 0.35, 0.1);
		matrices.mulPose(Axis.ZP.rotationDegrees(180));
		matrices.scale(0.5f, 0.5f, 0.5f);
		this.itemRenderer.renderStatic(Items.BELL.getDefaultInstance(), ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, queue,
				blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong() + 1);
		matrices.popPose();
	}
}
