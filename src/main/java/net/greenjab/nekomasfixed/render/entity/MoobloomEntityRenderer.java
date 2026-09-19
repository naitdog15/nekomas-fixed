package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModEntityLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.MoobloomEntity;
import net.greenjab.nekomasfixed.render.entity.model.MoobloomEntityModel;
import net.greenjab.nekomasfixed.render.entity.state.MoobloomEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

public class MoobloomEntityRenderer extends MobRenderer<MoobloomEntity, MoobloomEntityRenderState, MoobloomEntityModel> {

    public MoobloomEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MoobloomEntityModel(context.bakeLayer(ModEntityLayerRegistry.MOOBLOOM)), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(MoobloomEntityRenderState state) {
        String PATH = "textures/entity/moobloom/".concat(state.variantPath).concat(".png");
        String PATH_SHEARED = "textures/entity/moobloom/".concat(state.variantPath).concat("_sheared.png");
        return state.sheared ? NekomasFixed.id(PATH_SHEARED) : NekomasFixed.id(PATH);
    }

    @Override
    public void extractRenderState(MoobloomEntity entity, MoobloomEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.sheared = entity.getEntityData().get(MoobloomEntity.SHEARED);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.runAnimationState.copyFrom(entity.runAnimationState);
        state.variantPath = entity.getEntityData().get(MoobloomEntity.VARIANT);
        state.baby = entity.isBaby();
    }

    @Override
    public MoobloomEntityRenderState createRenderState() {
        return new MoobloomEntityRenderState();
    }

    @Override
    public void submit(MoobloomEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();
        if (state.baby) {
            matrices.scale(0.5F, 0.5F, 0.5F);
        }
        super.submit(state, matrices, queue, cameraState);
        matrices.popPose();
    }

}
