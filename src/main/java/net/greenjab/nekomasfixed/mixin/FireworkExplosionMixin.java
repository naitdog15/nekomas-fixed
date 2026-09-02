package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.util.ModColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.FireworkStarItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code net.minecraft.world.item.component.FireworkExplosion} (a 1.20.5+ data component) does not
 * exist on 1.20.1. Its {@code getColorName} tooltip helper is instead a private static method
 * directly on {@code FireworkStarItem}, with the identical {@code (int) -> Component} shape.
 */
@Mixin(FireworkStarItem.class)
public class FireworkExplosionMixin {

    @Inject(method = "getColorName", at = @At(value = "HEAD"), cancellable = true)
    private static void newDyes(int colorIndex, CallbackInfoReturnable<Component> cir) {
        if (colorIndex == ModColors.AMBER.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.amber"));
        else if (colorIndex == ModColors.AQUA.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.aqua"));
        else if (colorIndex == ModColors.INDIGO.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.indigo"));
        else if (colorIndex == ModColors.MAROON.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.maroon"));
    }
}
