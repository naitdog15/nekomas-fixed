package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.render.other.TintedGlint;
import net.greenjab.nekomasfixed.util.GlintContext;
import net.greenjab.nekomasfixed.registry.other.PrismGlint;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// tints item foil for prism-coloured stacks. compass/clock and glint_translucent stay vanilla.
@Mixin(ItemRenderer.class)
public class ItemRendererGlintMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void nekomasfixed$glintEnter(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack,
                                         MultiBufferSource buffer, int light, int overlay, BakedModel model, CallbackInfo ci) {
        boolean tinted = !stack.isEmpty() && stack.hasFoil() && PrismGlint.fromStack(stack) != null;
        GlintContext.enter(tinted ? stack : null);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void nekomasfixed$glintExit(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack,
                                        MultiBufferSource buffer, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintContext.exit();
    }

    @WrapOperation(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType nekomasfixed$glint(Operation<RenderType> original) {
        RenderType type = nekomasfixed$tinted(0);
        return type != null ? type : original.call();
    }

    @WrapOperation(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType nekomasfixed$entityGlint(Operation<RenderType> original) {
        RenderType type = nekomasfixed$tinted(1);
        return type != null ? type : original.call();
    }

    @WrapOperation(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintDirect()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType nekomasfixed$glintDirect(Operation<RenderType> original) {
        RenderType type = nekomasfixed$tinted(2);
        return type != null ? type : original.call();
    }

    @WrapOperation(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType nekomasfixed$entityGlintDirect(Operation<RenderType> original) {
        RenderType type = nekomasfixed$tinted(3);
        return type != null ? type : original.call();
    }

    // 0 glint, 1 entityGlint, 2 glintDirect, 3 entityGlintDirect
    @Unique
    private static RenderType nekomasfixed$tinted(int kind) {
        ItemStack stack = GlintContext.current();
        if (stack == null) return null;
        PrismGlint pg = PrismGlint.fromStack(stack);
        if (pg == null) return null;
        int color = pg.colorAt(Util.getMillis());
        return switch (kind) {
            case 0 -> TintedGlint.glint(color, pg.texture());
            case 1 -> TintedGlint.entityGlint(color, pg.texture());
            case 2 -> TintedGlint.glintDirect(color, pg.texture());
            default -> TintedGlint.entityGlintDirect(color, pg.texture());
        };
    }
}
