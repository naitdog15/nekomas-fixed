package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Points every minecart at the rebuilt cart texture. There is one
 * {@code MinecartRenderer<T extends AbstractMinecart>} here with a public, overridable
 * {@code getTextureLocation(T)} rather than a static field read inside a submit method, so the swap
 * is a plain return-value override. The generic bound means the class also carries a synthetic
 * {@code getTextureLocation(Entity)} bridge - hence the explicit descriptor, so the injector lands
 * on the real method only.
 *
 * <p>Geometry and wheel spin now live in {@link MinecartModelMixin}; nothing needs replacing on the
 * renderer's model field.
 */
@Mixin(MinecartRenderer.class)
public class AbstractMinecartEntityRendererMixin {

    @Unique private static final ResourceLocation NEKOMASFIXED$NEW_MINECART_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/minecart/new_minecart.png");

    @ModifyReturnValue(method = "getTextureLocation(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation useCustomMinecartTexture(ResourceLocation original) {
        return NEKOMASFIXED$NEW_MINECART_LOCATION;
    }
}
