package net.greenjab.nekomasfixed.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.greenjab.nekomasfixed.render.entity.model.BasePlateModel;
import net.greenjab.nekomasfixed.render.entity.model.TargetDummyArmorModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BasePlateFeatureRenderer extends RenderLayer<TargetDummy, TargetDummyArmorModel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/armorstand/wood.png");
	private final BasePlateModel model;

	public BasePlateFeatureRenderer(RenderLayerParent<TargetDummy, TargetDummyArmorModel> context, EntityModelSet entityModels) {
		super(context);
		this.model = new BasePlateModel(entityModels.bakeLayer(ModModelLayerRegistry.TARGET_DUMMY_BASE));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TargetDummy entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.isInvisible()) return;
		this.model.yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
		this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
		RenderType renderType = RenderType.entitySolid(TEXTURE);
		this.model.renderToBuffer(poseStack, buffer.getBuffer(renderType), packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
