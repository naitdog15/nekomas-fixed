package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registries.TextureRegistry;
import net.greenjab.nekomasfixed.registry.block.ClamBlock;
import net.greenjab.nekomasfixed.registry.block.entity.ClamBlockEntity;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.render.block.entity.model.ClamBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the clam's lid, hinge and (once it's cracked open) whatever item is sitting inside.
 * The chest-style sheet materials come straight from {@link TextureRegistry}'s four {@link Material}
 * constants, one per clam colour variant.
 */
public class ClamBlockEntityRenderer implements BlockEntityRenderer<ClamBlockEntity> {
	private final ClamBlockModel clamModel;
	private final ItemRenderer itemRenderer;

	public ClamBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.clamModel = new ClamBlockModel(context.bakeLayer(ModModelLayerRegistry.CLAM));
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(ClamBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		boolean hasLevel = blockEntity.getLevel() != null;
		BlockState blockState = hasLevel ? blockEntity.getBlockState() : BlockRegistry.CLAM.get().defaultBlockState().setValue(ClamBlock.FACING, Direction.SOUTH);
		float yaw = blockState.getValue(ClamBlock.FACING).toYRot();
		TextureRegistry.Variant variant = getVariant(blockEntity);
		float lidAnimationProgress = ClamBlock.getAnimationProgressRetriever(blockEntity).getFallback().get(partialTick);

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
		poseStack.translate(-0.5F, -0.5F, -0.5F);
		float f = lidAnimationProgress;
		f = 1.0F - f;
		f = 1.0F - f * f * f;
		// Atlas-backed sheet: build the RenderType/VertexConsumer against the shared atlas and let
		// the sprite wrapper remap the mesh's local UVs into this sprite's region of it.
		Material material = TextureRegistry.getClamMaterial(variant);
		RenderType renderType = this.clamModel.renderType(material.atlasLocation());
		VertexConsumer vertexConsumer = material.sprite().wrap(buffer.getBuffer(renderType));
		this.clamModel.setupAnim(f);
		this.clamModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();

		if (lidAnimationProgress > 0) {
			NonNullList<ItemStack> items = blockEntity.getItems();
			ItemStack itemStack = items.get(0);
			if (!itemStack.isEmpty()) {
				renderItem(itemStack, poseStack, buffer, packedLight, -yaw, blockEntity);
			}
		}
	}

	private void renderItem(ItemStack stack, PoseStack matrices, MultiBufferSource queue, int packedLight, float rotationDegrees, BlockEntity blockEntity) {
		matrices.pushPose();
		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.mulPose(Axis.YP.rotationDegrees(rotationDegrees + 180));
		matrices.translate(0, -0.37, -0.11);
		matrices.mulPose(Axis.XP.rotationDegrees(90));
		matrices.scale(0.5F, 0.5F, 0.5F);
		this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrices, queue,
				blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong());
		matrices.popPose();
	}

	private TextureRegistry.Variant getVariant(BlockEntity blockEntity) {
		if (blockEntity.getBlockState().getBlock() instanceof ClamBlock clamBlock) {
			return switch (clamBlock.getClamType()) {
				case REGULAR -> TextureRegistry.Variant.REGULAR;
				case BLUE -> TextureRegistry.Variant.BLUE;
				case PINK -> TextureRegistry.Variant.PINK;
				case PURPLE -> TextureRegistry.Variant.PURPLE;
			};
		}
		return TextureRegistry.Variant.REGULAR;
	}
}
