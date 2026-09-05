package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// items opt in via Item#initializeClient -> IClientItemExtensions#getCustomRenderer(); only reached
// when the item model declares builtin/entity, otherwise it draws as a flat sprite
public class NekomasFixedBEWLR extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TRIDENT_TEXTURE = NekomasFixed.id("textures/entity/wildfire_trident/default.png");
    private static final ResourceLocation SHIELD_TEXTURE = NekomasFixed.id("textures/entity/wildfire_shield/default.png");
    private static final ResourceLocation SHIELD_TEXTURE_SOUL = NekomasFixed.id("textures/entity/wildfire_shield/soul.png");

    private static NekomasFixedBEWLR INSTANCE;

    private TridentModel tridentModel;
    private ShieldModel shieldModel;

    public NekomasFixedBEWLR(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    public static NekomasFixedBEWLR instance() {
        if (INSTANCE == null) {
            Minecraft mc = Minecraft.getInstance();
            INSTANCE = new NekomasFixedBEWLR(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
            INSTANCE.onResourceManagerReload(mc.getResourceManager());
        }
        return INSTANCE;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        this.tridentModel = new TridentModel(modelSet.bakeLayer(ModModelLayerRegistry.WILDFIRE_TRIDENT));
        this.shieldModel = new ShieldModel(modelSet.bakeLayer(ModelLayers.SHIELD));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item instanceof net.greenjab.nekomasfixed.registry.item.WildfireTridentItem) {
            renderTrident(stack, poseStack, buffer, packedLight, packedOverlay);
        } else if (item instanceof net.greenjab.nekomasfixed.registry.item.WildfireShieldItem) {
            renderShield(stack, poseStack, buffer, packedLight, packedOverlay);
        }
    }

    private void renderTrident(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, this.tridentModel.renderType(TRIDENT_TEXTURE), false, stack.hasFoil());
        this.tridentModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private void renderShield(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int maxDamage = stack.getMaxDamage();
        ResourceLocation texture = (maxDamage <= 0 || stack.getDamageValue() < maxDamage / 2) ? SHIELD_TEXTURE : SHIELD_TEXTURE_SOUL;
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, this.shieldModel.renderType(texture), true, stack.hasFoil());
        this.shieldModel.handle().render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.shieldModel.plate().render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
