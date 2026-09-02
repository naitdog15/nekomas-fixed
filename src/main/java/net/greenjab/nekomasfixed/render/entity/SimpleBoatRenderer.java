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
 * Vanilla's boat renderer picks its texture off the boat's wood variant, which is a closed set this
 * mod's woods are not in. This one is variant-agnostic instead: it renders any {@link Boat} (chest
 * boats included, since they are boats) from one supplied model and one texture named after the
 * model layer, the same way {@link BigBoatRenderer} finds its own.
 */
public class SimpleBoatRenderer extends EntityRenderer<Boat> {
    private final ResourceLocation texture;
    private final BoatModel model;

    public SimpleBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, BoatModel model) {
        super(context);
        this.shadowRadius = 0.8F;
        this.texture = new ResourceLocation(layer.getModel().getNamespace(), "textures/entity/" + layer.getModel().getPath() + ".png");
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
