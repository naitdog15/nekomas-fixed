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
 * The spotted-sheep behaviour re-sources directly from the {@code Sheep} entity here;
 * SheepRenderStateMixin (the spotted-flag holder on a render state) and SheepRendererMixin (the
 * extractor that copied it there) are both deleted, and this file replaces the whole indirection.
 * <p>
 * 1.20.1 has no {@code SheepRenderState}/{@code submit(...)} deferred-render architecture (that is
 * 1.21.2+); {@code SheepWoolLayer} is renamed {@code SheepFurLayer} and its {@code render(...)}
 * receives the {@code Sheep} directly as a parameter (VERIFIED forge-1.20.1-mapped-src
 * SheepFurLayer.java) — so {@code ((SpottedSheepAccess) sheep).nekomasfixed$isSpotted()} can be read
 * right there, no state-stashing needed at all. {@code Identifier} (26.2) is
 * {@code ResourceLocation} here; the field itself is {@code SHEEP_FUR_LOCATION}, not
 * {@code SHEEP_WOOL_LOCATION}. It is read twice in {@code render} (the glowing-outline branch and
 * the normal-colour branch); both should show the spotted texture, so this is left unordinaled to
 * apply to both, matching the field's every use.
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
