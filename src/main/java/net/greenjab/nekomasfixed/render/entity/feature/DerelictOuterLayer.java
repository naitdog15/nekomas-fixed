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

// no separate baby model here - only the texture switches, matching vanilla zombie/husk/drowned
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
