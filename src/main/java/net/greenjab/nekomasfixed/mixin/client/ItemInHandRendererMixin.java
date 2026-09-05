package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// only one 20.0f literal in renderArmWithItem (bow draw tick count), so no ordinal is needed here.
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
