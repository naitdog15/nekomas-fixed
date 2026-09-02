package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Halves the first-person draw time of a slingshot, matching the shorter pull the third-person arm
 * pose uses.
 *
 * <p>The literal being modified is the tick count the bow's draw progress is measured against; it is
 * the only one of its kind inside the arm-drawing method, so the constant match needs no ordinal.
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
