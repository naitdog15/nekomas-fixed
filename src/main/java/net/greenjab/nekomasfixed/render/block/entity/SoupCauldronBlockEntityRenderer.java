package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registry.block.cauldron.SoupCauldronBlock;
import net.greenjab.nekomasfixed.registry.block.entity.SoupCauldronBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SoupCauldronBlockEntityRenderer implements BlockEntityRenderer<SoupCauldronBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SoupCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SoupCauldronBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) return;
        float animationTime = blockEntity.getLevel().getGameTime() + partialTick;
        float stirProgress = SoupCauldronBlock.getAnimationProgressRetriever(blockEntity).getFallback().get(partialTick);
        if (stirProgress >= 1) return;

        int seedBase = (int) blockEntity.getBlockPos().asLong();
        var inputs = blockEntity.getInputs();
        for (int i = 0; i < inputs.size(); ++i) {
            ItemStack itemStack = inputs.get(i);
            if (itemStack.isEmpty()) continue;
            poseStack.pushPose();
            float bob = (float) Math.sin(animationTime * 0.1f) * 0.02f;
            float stir = stirProgress * stirProgress;
            poseStack.translate(0.5F, 1F + bob - stir * 0.2f, 0.5F);
            Direction direction2 = Direction.from2DDataValue((i + Direction.NORTH.get2DDataValue()) % 4);
            poseStack.mulPose(Axis.YN.rotationDegrees(720 * stir - direction2.toYRot()));
            poseStack.mulPose(Axis.XN.rotationDegrees(-70.0F));
            poseStack.translate(-0.23 * (1 - stir), -0.1, 0.0F);
            poseStack.scale(0.275F, 0.375F, 0.275F);
            poseStack.scale(1 - stir, 1 - stir, 1 - stir);
            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer,
                    blockEntity.getLevel(), seedBase + i);
            poseStack.popPose();
        }
    }
}
