package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// renderFlame is shared by every entity and pulls ModelBakery.FIRE_0/FIRE_1 statically, so the swap
// is keyed on whether the burning entity is a wildfire with its soul flame lit.
@Mixin(EntityRenderDispatcher.class)
public class FlameFeatureRendererMixin {

    @Unique
    private static final Material SOUL_FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/soul_fire_0"));
    @Unique
    private static final Material SOUL_FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/soul_fire_1"));

    @ModifyExpressionValue(method = "renderFlame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_0:Lnet/minecraft/client/resources/model/Material;", opcode = Opcodes.GETSTATIC))
    private Material soulFire0(Material original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof WildfireEntity wildfire && wildfire.isSoulActive()) return SOUL_FIRE_0;
        return original;
    }

    @ModifyExpressionValue(method = "renderFlame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_1:Lnet/minecraft/client/resources/model/Material;", opcode = Opcodes.GETSTATIC))
    private Material soulFire1(Material original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof WildfireEntity wildfire && wildfire.isSoulActive()) return SOUL_FIRE_1;
        return original;
    }
}
