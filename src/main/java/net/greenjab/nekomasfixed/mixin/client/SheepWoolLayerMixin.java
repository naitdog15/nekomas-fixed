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

// texture field is read twice per render (glow outline + coloured pass); both need the spotted
// swap, so every occurrence is matched here rather than just one.
@Mixin(SheepFurLayer.class)
public abstract class SheepWoolLayerMixin {

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/layers/SheepFurLayer;SHEEP_FUR_LOCATION:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC))
    private ResourceLocation spottedTexture(ResourceLocation original, @Local(argsOnly = true) Sheep sheep){
        if (((SpottedSheepAccess) sheep).nekomasfixed$isSpotted())
            return NekomasFixed.id( "textures/entity/sheep/sheep_wool_spotted.png");
        return original;
    }
}
