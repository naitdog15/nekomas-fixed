package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.render.entity.model.SuspiciousSpiderModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SuspiciousSpiderEntityRenderer extends MobRenderer<SuspiciousSpider, SuspiciousSpiderModel> {
    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/entity/suspicious_spider/suspicious_spider.png");

    public SuspiciousSpiderEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SuspiciousSpiderModel(context.bakeLayer(ModModelLayerRegistry.SUSPICIOUS_SPIDER)), 0.5f);
        this.addLayer(new SuspiciousSpiderEyesFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SuspiciousSpider entity) {
        return TEXTURE;
    }
}
