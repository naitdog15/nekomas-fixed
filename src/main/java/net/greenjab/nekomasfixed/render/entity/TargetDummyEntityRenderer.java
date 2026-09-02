package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.greenjab.nekomasfixed.render.entity.feature.BasePlateFeatureRenderer;
import net.greenjab.nekomasfixed.render.entity.model.TargetDummyArmorModel;
import net.greenjab.nekomasfixed.render.entity.model.TargetDummyModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * A {@code LivingEntityRenderer<TargetDummy, TargetDummyArmorModel>} with no separate render-state
 * type - the dummy pulls what it needs straight off the entity. Wings/elytra rendering rides the
 * ordinary {@code ElytraLayer}.
 *
 * <p><b>Custom player-skin lookup is not restored here</b>: resolving a real skin from a profile needs
 * {@code SkinManager}/{@code GameProfile} plumbing that starts on the entity side
 * ({@code TargetDummy.getTargetDummyProfile()}'s return type), not the renderer, and still needs to be
 * wired up there. Until that lands, this renderer falls back to the mod's own two static textures
 * (default/zombie), matching what already happens today whenever no profile is set.
 */
public class TargetDummyEntityRenderer extends LivingEntityRenderer<TargetDummy, TargetDummyArmorModel> {
	private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/entity/target_dummy/default.png");
	private static final ResourceLocation ZOMBIE_TEXTURE = NekomasFixed.id("textures/entity/target_dummy/zombie.png");

	public TargetDummyEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new TargetDummyModel(context.bakeLayer(ModModelLayerRegistry.TARGET_DUMMY)), 0.0F);
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
		this.addLayer(new BasePlateFeatureRenderer(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(TargetDummy entity) {
		return entity.isZombie() ? ZOMBIE_TEXTURE : TEXTURE;
	}

	@Override
	protected void setupRotations(TargetDummy entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - rotationYaw));
		float timeSinceLastHit = (float) (entity.level().getGameTime() - entity.lastHitTime) + partialTick;
		if (timeSinceLastHit < 5.0F) {
			poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(Mth.sin(timeSinceLastHit / 1.5F * (float) Math.PI) * 3.0F));
		}
	}

	@Override
	protected boolean shouldShowName(TargetDummy entity) {
		return entity.isCustomNameVisible();
	}

	@Nullable
	@Override
	protected RenderType getRenderType(TargetDummy entity, boolean bodyVisible, boolean translucent, boolean glowing) {
		ResourceLocation texture = this.getTextureLocation(entity);
		if (translucent) return RenderType.entityTranslucent(texture, false);
		return bodyVisible ? RenderType.entityCutout(texture) : null;
	}
}
