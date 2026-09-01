package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Derelict;
import net.greenjab.nekomasfixed.render.entity.feature.DerelictOuterLayer;
import net.greenjab.nekomasfixed.render.entity.model.DerelictModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-state collapse: {@code AbstractZombieRenderer<Derelict, ZombieRenderState, DerelictModel>}
 * → 1.20.1's real {@code AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>>}, which
 * takes exactly THREE models (body, inner armor, outer armor) — not a fourth/fifth for a baby variant
 * (VERIFIED against {@code forge-1.20.1-mapped-src}: its constructor is
 * {@code (Context, M model, M innerArmor, M outerArmor)}). See {@link DerelictModel}'s javadoc for why
 * no separate baby model is needed.
 */
public class DerelictRenderer extends AbstractZombieRenderer<Derelict, DerelictModel> {
    private static final ResourceLocation DERELICT_LOCATION = NekomasFixed.id("textures/entity/zombie/derelict.png");
    private static final ResourceLocation BABY_DERELICT_LOCATION = NekomasFixed.id("textures/entity/zombie/derelict_baby.png");

    public DerelictRenderer(final EntityRendererProvider.Context context) {
        super(context,
                new DerelictModel(context.bakeLayer(ModModelLayerRegistry.DERELICT)),
                new DerelictModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new DerelictModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)));
        this.addLayer(new DerelictOuterLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(final Derelict entity) {
        return entity.isBaby() ? BABY_DERELICT_LOCATION : DERELICT_LOCATION;
    }
}
