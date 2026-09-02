package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registry.block.BaobabShelfBlock;
import net.greenjab.nekomasfixed.registry.block.entity.BaobabShelfBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Lays the shelf's three items out along its front, left to right as the shelf is faced. */
public class BaobabShelfBlockEntityRenderer implements BlockEntityRenderer<BaobabShelfBlockEntity> {

    private final ItemRenderer itemRenderer;

    public BaobabShelfBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BaobabShelfBlockEntity shelf, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction facing = shelf.getBlockState().getValue(BaobabShelfBlock.FACING);
        NonNullList<ItemStack> items = shelf.getItems();
        int seed = (int) shelf.getBlockPos().asLong();

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate((slot - 1) * 0.3125, 0.0, -0.25);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(0.25F, 0.25F, 0.25F);
            this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                    poseStack, buffer, shelf.getLevel(), seed + slot);
            poseStack.popPose();
        }
    }
}
