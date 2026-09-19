package net.greenjab.nekomasfixed.render.block.entity;

import net.greenjab.nekomasfixed.registry.block.entity.StackedCakeBlockEntity;
import net.greenjab.nekomasfixed.render.block.entity.state.StackedCakeBlockEntityRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;


public class StackedCakeBlockEntityRenderer implements BlockEntityRenderer<StackedCakeBlockEntity, StackedCakeBlockEntityRenderState> {

    public StackedCakeBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void extractRenderState(StackedCakeBlockEntity blockEntity, StackedCakeBlockEntityRenderState state, float f, Vec3 vec3d, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlayCommand) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, f, vec3d, crumblingOverlayCommand);
        state.LAYER_2_STATE = blockEntity.LAYER_2_STATE;
        state.LAYER_3_STATE = blockEntity.LAYER_3_STATE;
        state.CANDLE_STATE = blockEntity.CANDLE_STATE;
    }

    @Override
    public StackedCakeBlockEntityRenderState createRenderState() {
        return new StackedCakeBlockEntityRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public void submit(StackedCakeBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        Minecraft client = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderManager = client.getBlockRenderer();

        int i = 1;

        if (!state.LAYER_2_STATE.is(Blocks.AIR)) {
            matrices.pushPose();
            float scale = (float)(1.0 - (0.2 * i));

            matrices.translate((1.0f - scale) / 2.0f, i * 0.5f, (1.0f - scale) / 2.0f);
            matrices.scale(scale, scale, scale);
            blockRenderManager.renderSingleBlock(
                    state.LAYER_2_STATE,
                    matrices,
                    client.renderBuffers().bufferSource(),
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY
            );
            matrices.popPose();
            i=2;
            if (!state.LAYER_3_STATE.is(Blocks.AIR)) {
                matrices.pushPose();
                scale = (float)(1.0 - (0.2 * i));

                matrices.translate((1.0f - scale) / 2.0f, i*0.5 -0.1, (1.0f - scale) / 2.0f);
                matrices.scale(scale, scale, scale);
                blockRenderManager.renderSingleBlock(
                        state.LAYER_3_STATE,
                        matrices,
                        client.renderBuffers().bufferSource(),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                );
                matrices.popPose();
                i=3;
            }
        }

        matrices.pushPose();
        matrices.translate(0, i * 0.5f - ((i-1) * 0.1) - ((Math.max(0, i-2)) * 0.1), 0);
        blockRenderManager.renderSingleBlock(
                state.CANDLE_STATE,
                matrices,
                client.renderBuffers().bufferSource(),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY
        );

        matrices.popPose();
    }

}
