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

/**
 * Replaces {@code SpecialModelRenderersMixin} (a mixin injecting into
 * {@code IClientItemExtensions#getCustomRenderer()}). 26.2's {@code SpecialModelRenderer<T>}/
 * {@code NoDataSpecialModelRenderer} framework (the {@code WildfireTridentModelRenderer}/
 * {@code WildfireShieldModelRenderer} classes this replaces) does not exist on 1.20.1 at all — the
 * 1.20.1-native mechanism for "this item needs a full 3D model instead of a flat sprite" is a
 * {@link BlockEntityWithoutLevelRenderer} (a "BEWLR") returned from {@code Item#initializeClient(...)}
 * ({@code IClientItemExtensions.getCustomRenderer()}'s own javadoc names this as the intended hook).
 * Modeled directly on Forge's own vanilla {@code BlockEntityWithoutLevelRenderer#renderByItem}
 * TRIDENT/SHIELD branches (read from {@code forge-1.20.1-mapped-src}) — same
 * {@code getFoilBufferDirect}/{@code scale(1,-1,-1)} shape, retargeted at this mod's two items and its
 * damage-based soul-shield texture swap.
 *
 * <p><b>Not wired up yet.</b> To use it, override
 * {@code Item#initializeClient(Consumer<IClientItemExtensions>)} on
 * {@code WildfireTridentItem}/{@code WildfireShieldItem}:
 * <pre>{@code
 * @Override
 * public void initializeClient(Consumer<IClientItemExtensions> consumer) {
 *     consumer.accept(new IClientItemExtensions() {
 *         @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
 *             return NekomasFixedBEWLR.instance();
 *         }
 *     });
 * }
 * }</pre>
 */
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

    /** Lazily constructed the first time an item asks for it — mirrors how Forge itself constructs
     * its own default BEWLR once, in {@code ItemRenderer}'s init, and hands out that same instance
     * from {@code IClientItemExtensions.DEFAULT}. */
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
