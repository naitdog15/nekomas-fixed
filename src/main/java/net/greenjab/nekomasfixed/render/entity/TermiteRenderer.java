package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModEntityLayerRegistry;
import net.greenjab.nekomasfixed.render.entity.model.TermiteModel;import net.greenjab.nekomasfixed.render.entity.state.TermiteRenderState;
import net.greenjab.nekomasfixed.registry.entity.TermiteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class TermiteRenderer extends MobRenderer<TermiteEntity, TermiteRenderState, TermiteModel> {
    public TermiteRenderer(EntityRendererProvider.Context context) {
        super(context, new TermiteModel(context.bakeLayer(ModEntityLayerRegistry.TERMITE)), 0.25f);
    }

    @Override
    public TermiteRenderState createRenderState() {
        return new TermiteRenderState();
    }


    @Override
    public Identifier getTextureLocation(TermiteRenderState state) {
        return NekomasFixed.id("textures/entity/termite/termite.png");
    }

    @Override
    public void extractRenderState(TermiteEntity entity, TermiteRenderState state, float f) {
        super.extractRenderState(entity, state, f);
        state.swipeAnimationState.copyFrom(entity.swipeAnimationState);
    }
}
