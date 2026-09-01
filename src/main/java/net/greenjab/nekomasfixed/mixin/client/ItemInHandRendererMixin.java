package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * {@code ItemInHandRenderer} has no {@code submitArmWithItem} on 1.20.1 (the submit-node-collector
 * era) — the equivalent immediate-mode method is {@code renderArmWithItem(...)} (VERIFIED
 * forge-1.20.1-mapped-src ItemInHandRenderer.java:317). Unverified: whether {@code 20.0f} appears
 * exactly once inside that specific method on 1.20.1 (the class declares several {@code ±20.0F}
 * constants used across multiple methods — VERIFIED lines 44,45,59,88 — so {@code @ModifyConstant}'s
 * default "match every occurrence in this method" could be too broad if more than one literal
 * {@code 20.0F} appears inside {@code renderArmWithItem} itself specifically); needs runtime
 * verification.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @ModifyConstant(method="renderArmWithItem", constant = @Constant(floatValue = 20.0f))
    private float slingshotFasterPullTime(float constant, @Local(argsOnly = true) AbstractClientPlayer player) {
       if (player.getUseItem().is(ItemRegistry.SLINGSHOT.get())) {
           return constant/2;
       }
       return constant;
    }
}
