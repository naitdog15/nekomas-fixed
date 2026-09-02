package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.block.entity.HollowLogBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Draws whatever block is currently stored inside, scaled down, via {@link BlockRenderDispatcher#renderSingleBlock}. */
public class HollowLogBlockEntityRenderer implements BlockEntityRenderer<HollowLogBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public HollowLogBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(HollowLogBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState storedBlock = blockEntity.getStoredBlock();
        if (storedBlock.is(Blocks.AIR)) return;
        poseStack.pushPose();
        poseStack.translate(0.125, 0.125, 0.125);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        this.blockRenderer.renderSingleBlock(storedBlock, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
