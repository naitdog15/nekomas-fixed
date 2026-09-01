package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.render.entity.model.SuspiciousSpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class SuspiciousSpiderEyesFeatureRenderer<M extends SuspiciousSpiderModel> extends EyesLayer<SuspiciousSpider, M> {
    private static final RenderType SKIN = RenderType.eyes(NekomasFixed.id("textures/entity/suspicious_spider_eyes.png"));

    public SuspiciousSpiderEyesFeatureRenderer(RenderLayerParent<SuspiciousSpider, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderType renderType() {
        return SKIN;
    }
}
