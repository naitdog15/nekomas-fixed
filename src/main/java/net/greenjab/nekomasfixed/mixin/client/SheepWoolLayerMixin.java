package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.util.SpottedSheepAccess;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Gives a spotted sheep its spotted fleece.
 *
 * <p>The layer is handed the sheep itself, so the spotted flag is read straight off it. The fur
 * texture is used twice while the layer draws - once for the glowing outline, once for the ordinary
 * coloured pass - and both should show the spots, so this deliberately matches every use of the
 * field rather than picking one.
 */
@Mixin(SheepFurLayer.class)
public abstract class SheepWoolLayerMixin {

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/layers/SheepFurLayer;SHEEP_FUR_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC))
    private ResourceLocation spottedTexture(ResourceLocation original, @Local(argsOnly = true) Sheep sheep){
        if (((SpottedSheepAccess) sheep).nekomasfixed$isSpotted())
            return NekomasFixed.id( "textures/entity/sheep/sheep_wool_spotted.png");
        return original;
    }
}
