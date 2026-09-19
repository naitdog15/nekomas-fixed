package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.util.ModColors;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkExplosion.class)
public class FireworkExplosionMixin {

    @Inject(method = "getColorName", at = @At(value = "HEAD"), cancellable = true)
    private static void newDyes(int color, CallbackInfoReturnable<Component> cir) {
        if (color == ModColors.AMBER.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.amber"));
        else if (color == ModColors.AQUA.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.aqua"));
        else if (color == ModColors.INDIGO.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.indigo"));
        else if (color == ModColors.MAROON.getColor()) cir.setReturnValue(Component.translatable("item.minecraft.firework_star.maroon"));
    }
}