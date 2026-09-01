package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

/**
 * New construction, not a port. No {@code BaobabBoat}/{@code BaobabChestBoat} Java class exists
 * anywhere in the tree ({@code registry/entity/**} has {@code BigBoat}/{@code HugeBoat}/
 * {@code FakeBoat} but no plain-tier Baobab class), and 1.20.1's vanilla
 * {@code BoatRenderer(Context, boolean hasChest)} cannot render a non-vanilla {@code Boat.Type} —
 * that enum is closed on 1.20.1. This is a minimal, type-agnostic stand-in built directly on
 * vanilla's {@link BoatModel}: it renders any {@link Boat} (or {@code ChestBoat}, which extends it)
 * using one baked model and one texture derived from the layer id, exactly mirroring
 * {@link BigBoatRenderer}'s own texture-derivation trick. Once
 * {@code EntityTypeRegistry.BAOBAB_BOAT}/{@code BAOBAB_CHEST_BOAT}'s real Java shape is confirmed,
 * this registration in {@code ModEntityRendererRegistry} may need to change to a no-op (if they turn
 * out to just be aliases for {@code EntityType.BOAT}/{@code CHEST_BOAT}, which already renders
 * without any mod registration) or to a dedicated model class (if the chest variant needs its own
 * mesh).
 */
public class SimpleBoatRenderer extends EntityRenderer<Boat> {
    private final ResourceLocation texture;
    private final BoatModel model;

    public SimpleBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, BoatModel model) {
        super(context);
        this.shadowRadius = 0.8F;
        this.texture = new ResourceLocation(layer.model().getNamespace(), "textures/entity/" + layer.model().getPath() + ".png");
        this.model = model;
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        return this.texture;
    }

    @Override
    public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - entityYaw));
        float hurtTime = entity.getHurtTime() - partialTicks;
        float damage = Math.max(entity.getDamage() - partialTicks, 0.0F);
        if (damage > 0.0F) {
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * entity.getHurtDir()));
        }
        if (!Mth.equal(entity.getBubbleAngle(partialTicks), 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(entity.getBubbleAngle(partialTicks) * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.pushPose();
        this.model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(this.texture));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
}
