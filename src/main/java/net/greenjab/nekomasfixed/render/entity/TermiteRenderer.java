package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Termite;
import net.greenjab.nekomasfixed.render.entity.model.TermiteModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TermiteRenderer extends MobRenderer<Termite, TermiteModel> {
    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/entity/termite/termite.png");

    public TermiteRenderer(EntityRendererProvider.Context context) {
        super(context, new TermiteModel(context.bakeLayer(ModModelLayerRegistry.TERMITE)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(Termite entity) {
        return TEXTURE;
    }
}
