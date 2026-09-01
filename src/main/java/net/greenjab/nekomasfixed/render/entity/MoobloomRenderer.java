package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.Moobloom;
import net.greenjab.nekomasfixed.render.entity.model.BabyMoobloomModel;
import net.greenjab.nekomasfixed.render.entity.model.MoobloomModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Render-state collapse. 1.20.1 has no {@code AgeableMobRenderer} (VERIFIED — no such class in
 * {@code forge-1.20.1-mapped-src}; vanilla mobs with babies just scale one model via the young flag).
 * Since this mod's baby model is a bespoke mesh, not an auto-scale of the adult one, the adult/baby
 * swap is done by hand: {@code this.model} (protected on {@code LivingEntityRenderer}) is reassigned
 * before {@code super.render(...)} runs, exactly mirroring what {@code extractRenderState} used to
 * decide once per frame.
 */
public class MoobloomRenderer extends MobRenderer<Moobloom, MoobloomModel> {
    private final MoobloomModel adultModel;
    private final MoobloomModel babyModel;

    public MoobloomRenderer(EntityRendererProvider.Context context) {
        super(context, new MoobloomModel(context.bakeLayer(ModModelLayerRegistry.MOOBLOOM)), 0.7F);
        this.adultModel = this.getModel();
        this.babyModel = new BabyMoobloomModel(context.bakeLayer(ModModelLayerRegistry.MOOBLOOM_BABY));
    }

    @Override
    public void render(Moobloom entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.model = entity.isBaby() ? this.babyModel : this.adultModel;
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Moobloom entity) {
        String variantPath = entity.getEntityData().get(Moobloom.VARIANT);
        boolean sheared = entity.getEntityData().get(Moobloom.SHEARED);
        if (entity.isBaby()) return NekomasFixed.id("textures/entity/moobloom/" + variantPath + "_baby.png");
        if (sheared) return NekomasFixed.id("textures/entity/moobloom/" + variantPath + "_sheared.png");
        return NekomasFixed.id("textures/entity/moobloom/" + variantPath + ".png");
    }
}
