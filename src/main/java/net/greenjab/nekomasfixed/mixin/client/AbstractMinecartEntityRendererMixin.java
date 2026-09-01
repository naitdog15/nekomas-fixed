package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.render.entity.model.CustomMinecartModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyReturnValue;

/**
 * 1.20.1 has no {@code AbstractMinecartRenderer}/{@code SubmitNodeCollector} split (1.21.2+); there
 * is one {@code MinecartRenderer<T extends AbstractMinecart>} with a classic immediate-mode
 * {@code render(...)} (VERIFIED forge-1.20.1-mapped-src MinecartRenderer.java). Its texture is a
 * PUBLIC, OVERRIDABLE {@code getTextureLocation(T)} rather than a private static field read inside a
 * submit method — simpler to retarget than the pristine field-GETSTATIC trick, not harder.
 * {@code net.minecraft.client.model.object.cart.MinecartModel} (26.2) is
 * {@code net.minecraft.client.model.MinecartModel} here (no {@code .object.cart} subpackage).
 */
@Mixin(MinecartRenderer.class)
public class AbstractMinecartEntityRendererMixin {

    @Unique private static final ResourceLocation NEW_MINECART_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/minecart/new_minecart.png");

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/geom/ModelPart;)Lnet/minecraft/client/model/MinecartModel;"))
    private static MinecartModel useCustomMinecartModel(ModelPart root, Operation<MinecartModel> original) {
        return new CustomMinecartModel(root);
    }

    @ModifyReturnValue(method = "getTextureLocation", at = @At("RETURN"))
    private ResourceLocation useCustomMinecartTexture(ResourceLocation original) {
        return NEW_MINECART_LOCATION;
    }

}
