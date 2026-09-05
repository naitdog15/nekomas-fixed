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

// the four added colours share a dye slot with a vanilla one, so the block identifies them, not
// the colour.
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
