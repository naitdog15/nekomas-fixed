package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Points every minecart at the rebuilt cart's texture. The renderer is generic over its cart type,
 * so it also carries a bridge method of the same name - hence the explicit descriptor, which keeps
 * the injector on the real one.
 *
 * <p>The cart's shape and its turning wheels are {@link MinecartModelMixin}'s half of the same
 * switch.
 */
@Mixin(MinecartRenderer.class)
public class AbstractMinecartEntityRendererMixin {

    @Unique private static final ResourceLocation NEKOMASFIXED$NEW_MINECART_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/minecart/new_minecart.png");

    @ModifyReturnValue(method = "getTextureLocation(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation useCustomMinecartTexture(ResourceLocation original) {
        if (!NekomasFixedClientConfig.CUSTOM_MINECART_MODEL.get()) return original;
        return NEKOMASFIXED$NEW_MINECART_LOCATION;
    }
}
