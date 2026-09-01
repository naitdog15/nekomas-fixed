package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * The behaviour here stays, retargeted to 1.20.1's single-pass render that swaps the Material
 * directly — ShulkerBoxRenderStateMixin (the {@code SpriteId} holder) is deleted; this file replaces
 * the whole indirection.
 * <p>
 * 1.20.1 has no {@code ShulkerBoxRenderState}/{@code extractRenderState}/{@code submit(...)} split
 * (1.21.2+ deferred-render architecture) or sprite-atlas shulker textures — there is one
 * {@code render(ShulkerBoxBlockEntity, float, PoseStack, MultiBufferSource, int, int)} that computes
 * a local {@code Material} (sheet {@code Sheets.SHULKER_SHEET}, path unchanged from what the
 * pristine sprite id used) from the block entity's colour and feeds it straight into
 * {@code material.buffer(...)} (VERIFIED forge-1.20.1-mapped-src ShulkerBoxRenderer.java) — no
 * {@code SpriteId}/atlas involved, so this modifies that {@code Material} local directly instead of
 * stashing a {@code SpriteId} on a render state that no longer exists. The block entity itself is
 * already a direct method parameter, so the colour-block lookup no longer needs a render-state cast.
 */
@Mixin(ShulkerBoxRenderer.class)
public class ShulkerBoxRendererMixin {

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private Material replaceTexture(Material material, @Local(argsOnly = true) ShulkerBoxBlockEntity blockEntity) {
        String identifier = null;
        if (blockEntity.getBlockState().getBlock() == BlockRegistry.AMBER_SHULKER_BOX.get()) identifier = "entity/shulker/shulker_amber";
        else if (blockEntity.getBlockState().getBlock() == BlockRegistry.AQUA_SHULKER_BOX.get()) identifier = "entity/shulker/shulker_aqua";
        else if (blockEntity.getBlockState().getBlock() == BlockRegistry.MAROON_SHULKER_BOX.get()) identifier = "entity/shulker/shulker_maroon";
        else if (blockEntity.getBlockState().getBlock() == BlockRegistry.INDIGO_SHULKER_BOX.get()) identifier = "entity/shulker/shulker_indigo";
        if (identifier != null) return new Material(Sheets.SHULKER_SHEET, NekomasFixed.id(identifier));
        return material;
    }
}
