package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Rime;
import net.greenjab.nekomasfixed.render.entity.feature.RimeOuterLayer;
import net.greenjab.nekomasfixed.render.entity.model.RimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** See {@link DerelictRenderer}'s javadoc — identical collapse. */
public class RimeRenderer extends AbstractZombieRenderer<Rime, RimeModel> {
    private static final ResourceLocation RIME_LOCATION = NekomasFixed.id("textures/entity/zombie/rime.png");
    private static final ResourceLocation BABY_RIME_LOCATION = NekomasFixed.id("textures/entity/zombie/rime_baby.png");

    public RimeRenderer(final EntityRendererProvider.Context context) {
        super(context,
                new RimeModel(context.bakeLayer(ModModelLayerRegistry.RIME)),
                new RimeModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new RimeModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)));
        this.addLayer(new RimeOuterLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(final Rime entity) {
        return entity.isBaby() ? BABY_RIME_LOCATION : RIME_LOCATION;
    }
}
