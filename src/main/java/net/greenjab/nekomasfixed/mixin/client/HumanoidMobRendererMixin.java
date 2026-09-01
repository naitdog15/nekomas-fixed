package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.client.mixins.json still names
 * {@code HumanoidMobRendererMixin}). Its one piece
 * of logic — halve the visual crossbow-charge duration for a slingshot — targeted
 * {@code extractHumanoidRenderState}, a render-state extraction step that does not exist on 1.20.1
 * (VERIFIED: {@code HumanoidMobRenderer.java} calls neither {@code getChargeDuration} nor anything
 * {@code ArmPose}-related at all; that pose math lives entirely in {@code HumanoidModel} itself).
 * Absorbed into {@link HumanoidModelMixin#chargeDuration}, which already has to compute the charge
 * duration locally since there is no pre-populated state field left to read it from.
 */
@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
}
