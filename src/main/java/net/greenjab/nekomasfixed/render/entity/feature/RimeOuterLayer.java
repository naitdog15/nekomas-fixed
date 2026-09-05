package net.greenjab.nekomasfixed.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Rime;
import net.greenjab.nekomasfixed.render.entity.model.RimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RimeOuterLayer extends RenderLayer<Rime, RimeModel> {
    private static final ResourceLocation RIME_OUTER_LAYER_LOCATION = NekomasFixed.id("textures/entity/zombie/rime_outer_layer.png");
    private static final ResourceLocation BABY_RIME_OUTER_LAYER_LOCATION = NekomasFixed.id("textures/entity/zombie/rime_outer_layer_baby.png");
    private final RimeModel model;

    public RimeOuterLayer(final RenderLayerParent<Rime, RimeModel> renderer, final EntityModelSet modelSet) {
        super(renderer);
        this.model = new RimeModel(modelSet.bakeLayer(ModModelLayerRegistry.RIME_OUTER_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Rime entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = entity.isBaby() ? BABY_RIME_OUTER_LAYER_LOCATION : RIME_OUTER_LAYER_LOCATION;
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, texture, poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
