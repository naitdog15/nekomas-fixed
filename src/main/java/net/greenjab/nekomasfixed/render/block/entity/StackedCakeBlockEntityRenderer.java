package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.block.entity.StackedCakeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** See {@link HollowLogBlockEntityRenderer}'s javadoc — identical collapse, three stacked insets. */
public class StackedCakeBlockEntityRenderer implements BlockEntityRenderer<StackedCakeBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public StackedCakeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public boolean shouldRenderOffScreen(StackedCakeBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(StackedCakeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int height = 1;
        BlockState layer2 = blockEntity.LAYER_2_STATE;
        if (!layer2.is(Blocks.AIR)) {
            poseStack.pushPose();
            float scale = (float) (1.0 - (0.2 * height));
            poseStack.translate((1.0f - scale) / 2.0f, height * 0.5f, (1.0f - scale) / 2.0f);
            poseStack.scale(scale, scale, scale);
            this.blockRenderer.renderSingleBlock(layer2, poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();
            height = 2;

            BlockState layer3 = blockEntity.LAYER_3_STATE;
            if (!layer3.is(Blocks.AIR)) {
                poseStack.pushPose();
                scale = (float) (1.0 - (0.2 * height));
                poseStack.translate((1.0f - scale) / 2.0f, height * 0.5 - 0.1, (1.0f - scale) / 2.0f);
                poseStack.scale(scale, scale, scale);
                this.blockRenderer.renderSingleBlock(layer3, poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
                height = 3;
            }
        }

        BlockState candle = blockEntity.CANDLE_STATE;
        if (!candle.is(Blocks.AIR)) {
            poseStack.pushPose();
            poseStack.translate(0, height * 0.5f - ((height - 1) * 0.1) - ((Math.max(0, height - 2)) * 0.1), 0);
            this.blockRenderer.renderSingleBlock(candle, poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
}
