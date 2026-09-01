package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.greenjab.nekomasfixed.render.entity.model.DrenchedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DrenchedRenderer extends HumanoidMobRenderer<Drenched, DrenchedModel> {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            NekomasFixed.id("textures/entity/drenched/purple.png"),
            NekomasFixed.id("textures/entity/drenched/red.png"),
            NekomasFixed.id("textures/entity/drenched/yellow.png")
    };

    public DrenchedRenderer(EntityRendererProvider.Context context) {
        super(context, new DrenchedModel(context.bakeLayer(ModModelLayerRegistry.DRENCHED)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new DrenchedModel(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new DrenchedModel(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Drenched entity) {
        int variant = entity.getVariant();
        if (variant < 0 || variant >= TEXTURES.length) return TEXTURES[0];
        return TEXTURES[variant];
    }

    @Override
    protected void setupRotations(Drenched entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
        // NOTE: 26.2's setupRotations(state, poseStack, f, g) is 4-arg; 1.20.1's real
        // LivingEntityRenderer#setupRotations is 5-arg (entity, poseStack, bob, rotationYaw,
        // partialTick) with no separate "entity scale" float at this call site. The original divided
        // the pivot height by its trailing param `g`; mapping that to `partialTick` here would
        // occasionally divide by a near-zero value and produce a wildly wrong pivot on some frames,
        // so the division is dropped (equivalent to assuming scale=1.0, true for every current variant
        // of this mob) rather than guessed. Documented simplification, not a silent cut.
        float swimAmount = entity.getSwimAmount(partialTick);
        if (swimAmount > 0.0F) {
            float i = -10.0F - entity.getXRot();
            float j = Mth.lerp(swimAmount, 0.0F, i);
            poseStack.rotateAround(Axis.XP.rotationDegrees(j), 0.0F, entity.getBbHeight() / 2.0F, 0.0F);
        }
    }
}
