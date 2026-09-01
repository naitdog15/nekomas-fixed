package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.block.AbstractEndermanHeadBlock;
import net.greenjab.nekomasfixed.registry.block.FloorEndermanHeadHead;
import net.greenjab.nekomasfixed.registry.block.WallEndermanHeadHead;
import net.greenjab.nekomasfixed.render.block.entity.model.EndermanEyesBlockModel;
import net.greenjab.nekomasfixed.render.block.entity.model.EndermanHeadBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class EndermanHeadBlockEntityRenderer implements BlockEntityRenderer<BlockEntity> {

	private final EndermanHeadBlockModel endermanHeadModel;
	private final EndermanEyesBlockModel endermanEyesModel;

	private final RandomSource random = RandomSource.create();
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/enderman/enderman.png");
	private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("minecraft", "textures/entity/enderman/enderman_eyes.png");

	public EndermanHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.endermanHeadModel = new EndermanHeadBlockModel(context.bakeLayer(ModModelLayerRegistry.ENDERMAN_HEAD));
		this.endermanEyesModel = new EndermanEyesBlockModel(context.bakeLayer(ModModelLayerRegistry.ENDERMAN_HEAD));
	}

	@Override
	public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState blockState = blockEntity.getBlockState();
		boolean wall = blockState.getBlock() instanceof WallEndermanHeadHead;
		Direction facing = wall ? blockState.getValue(WallEndermanHeadHead.FACING) : null;
		int segment = wall ? RotationSegment.convertToSegment(facing.getOpposite()) : blockState.getValue(FloorEndermanHeadHead.ROTATION);
		float yaw = RotationSegment.convertToDegrees(segment);
		boolean powered = blockState.getValue(AbstractEndermanHeadBlock.POWER) > 0;

		poseStack.pushPose();
		if (facing == null) poseStack.translate(0.5F, 0.0F, 0.5F);
		else poseStack.translate(0.5F - facing.getStepX() * 0.2499F, 0.25F, 0.5F - facing.getStepZ() * 0.2499F);

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
		poseStack.translate(-0.5F, -0.5f, -0.5F);
		if (powered) {
			if (wall) poseStack.translate(this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, 0.0F);
			else poseStack.translate(this.random.nextGaussian() * 0.02, 0.0F, this.random.nextGaussian() * 0.02);
		}

		this.endermanHeadModel.setupAnim(powered, wall);
		this.endermanEyesModel.setupAnim(powered, wall);
		RenderType renderType = RenderType.entityCutout(TEXTURE);
		RenderType renderTypeEyes = RenderType.eyes(TEXTURE_EYES);
		this.endermanHeadModel.renderToBuffer(poseStack, buffer.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		this.endermanEyesModel.renderToBuffer(poseStack, buffer.getBuffer(renderTypeEyes), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}
}
