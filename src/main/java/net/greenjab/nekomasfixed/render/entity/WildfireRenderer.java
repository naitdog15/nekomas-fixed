package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.render.entity.model.WildfireModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WildfireRenderer extends MobRenderer<WildfireEntity, WildfireModel> {
	private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/entity/wildfire/default.png");
	private static final ResourceLocation TEXTURE_SOUL = NekomasFixed.id("textures/entity/wildfire/soul.png");

	public WildfireRenderer(EntityRendererProvider.Context context) {
		super(context, new WildfireModel(context.bakeLayer(ModModelLayerRegistry.WILD_FIRE)), 0.5F);
	}

	@Override
	protected int getBlockLightLevel(WildfireEntity wildFireEntity, BlockPos blockPos) {
		return 15;
	}

	@Override
	public ResourceLocation getTextureLocation(WildfireEntity entity) {
		return entity.isSoulActive() ? TEXTURE_SOUL : TEXTURE;
	}

	@Override
	public void render(WildfireEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		this.getModel().bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
}
