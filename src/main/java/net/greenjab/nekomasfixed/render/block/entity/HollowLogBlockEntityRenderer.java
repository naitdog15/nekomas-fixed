package net.greenjab.nekomasfixed.render.block.entity;

import net.greenjab.nekomasfixed.registry.block.entity.HollowLogBlockEntity;
import net.greenjab.nekomasfixed.render.block.entity.state.HollowLogBlockEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class HollowLogBlockEntityRenderer implements BlockEntityRenderer<HollowLogBlockEntity, HollowLogBlockEntityRenderState>{

    public HollowLogBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public HollowLogBlockEntityRenderState createRenderState() {
        return new HollowLogBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(HollowLogBlockEntity blockEntity,
                                  HollowLogBlockEntityRenderState state,
                                  float tickProgress,
                                  Vec3 cameraPos,
                                  ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {

        state.blockState = blockEntity.getStoredBlock();
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
    }

    @Override
    public void submit(HollowLogBlockEntityRenderState state,
                       PoseStack matrixStack,
                       SubmitNodeCollector queue,
                       CameraRenderState cameraState) {

        Minecraft client = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderManager = client.getBlockRenderer();

        if (state.blockState == null) return;

        matrixStack.pushPose();
        matrixStack.translate(0.125, 0.125, 0.125);
        matrixStack.scale(0.75f, 0.75f, 0.75f);

        blockRenderManager.renderSingleBlock(
                state.blockState,
                matrixStack,
                client.renderBuffers().bufferSource(),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY
        );

        matrixStack.popPose();
    }
}
