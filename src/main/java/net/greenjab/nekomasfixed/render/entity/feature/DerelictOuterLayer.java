package net.greenjab.nekomasfixed.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Derelict;
import net.greenjab.nekomasfixed.render.entity.model.DerelictModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-state collapse: {@code RenderLayer<ZombieRenderState, DerelictModel>} → {@code <Derelict,
 * DerelictModel>}, {@code submit(...)} (26.2's batched pipeline) → the classic {@code render(...)}
 * override. No dedicated baby model any more — see {@link DerelictModel}'s javadoc; only the texture
 * switches for the baby variant, matching vanilla Zombie/Husk/Drowned's own outer-layer handling.
 */
public class DerelictOuterLayer extends RenderLayer<Derelict, DerelictModel> {
    private static final ResourceLocation DERELICT_OUTER_LAYER_LOCATION = NekomasFixed.id("textures/entity/zombie/derelict_outer_layer.png");
    private static final ResourceLocation BABY_DERELICT_OUTER_LAYER_LOCATION = NekomasFixed.id("textures/entity/zombie/derelict_outer_layer_baby.png");
    private final DerelictModel model;

    public DerelictOuterLayer(final RenderLayerParent<Derelict, DerelictModel> renderer, final EntityModelSet modelSet) {
        super(renderer);
        this.model = new DerelictModel(modelSet.bakeLayer(ModModelLayerRegistry.DERELICT_OUTER_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Derelict entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = entity.isBaby() ? BABY_DERELICT_OUTER_LAYER_LOCATION : DERELICT_OUTER_LAYER_LOCATION;
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, texture, poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
