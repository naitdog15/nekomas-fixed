package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.render.other.TintedGlint;
import net.greenjab.nekomasfixed.util.GlintContext;
import net.greenjab.nekomasfixed.registry.other.PrismGlint;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// humanoid armour foil only (elytra, trims and non-humanoid layers stay vanilla). foil is only drawn
// for already-enchanted armour, so prisms never force it.
@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerGlintMixin {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void nekomasfixed$glintEnter(PoseStack poseStack, MultiBufferSource buffer, LivingEntity entity, EquipmentSlot slot,
                                         int light, HumanoidModel<?> model, CallbackInfo ci) {
        ItemStack stack = entity.getItemBySlot(slot);
        boolean tinted = !stack.isEmpty() && stack.hasFoil() && PrismGlint.fromStack(stack) != null;
        GlintContext.enter(tinted ? stack : null);
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"))
    private void nekomasfixed$glintExit(PoseStack poseStack, MultiBufferSource buffer, LivingEntity entity, EquipmentSlot slot,
                                        int light, HumanoidModel<?> model, CallbackInfo ci) {
        GlintContext.exit();
    }

    @WrapOperation(method = "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType nekomasfixed$armorGlint(Operation<RenderType> original) {
        ItemStack stack = GlintContext.current();
        PrismGlint pg = stack == null ? null : PrismGlint.fromStack(stack);
        if (pg != null) {
            RenderType type = TintedGlint.armorEntityGlint(pg.colorAt(Util.getMillis()), pg.texture());
            if (type != null) return type;
        }
        return original.call();
    }
}
