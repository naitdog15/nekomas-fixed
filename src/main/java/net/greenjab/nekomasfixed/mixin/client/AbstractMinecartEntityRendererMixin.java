package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// generic renderer also carries a bridge method of the same name, hence the explicit descriptor.
// see MinecartModelMixin for the cart's shape/wheels half of this.
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
