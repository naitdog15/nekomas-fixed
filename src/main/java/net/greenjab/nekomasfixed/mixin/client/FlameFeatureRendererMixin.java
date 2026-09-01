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

/**
 * 1.20.1 has no {@code FlameFeatureRenderer}/{@code AtlasManager}/{@code SpriteId}
 * (1.21.2+ deferred-render architecture); the on-fire overlay every entity gets is one shared
 * private method, {@code EntityRenderDispatcher#renderFlame(PoseStack, MultiBufferSource, Entity)},
 * reading two {@code ModelBakery.FIRE_0}/{@code FIRE_1} {@code Material} constants (VERIFIED
 * forge-1.20.1-mapped-src EntityRenderDispatcher.java:203-205; ModelBakery.java:65-66 — the same
 * {@code Material} type this port already uses for shulker boxes). There is no per-entity "which
 * atlas group" indirection to hook the way the pristine {@code AtlasManager#get(SpriteId)} call let
 * this mixin intercept per-{@code Submit} — but {@code renderFlame} already receives the burning
 * {@code Entity} itself as a parameter, so the Wildfire soul-fire check reads directly off it
 * ({@code WildfireEntity#isSoulActive()}, an existing method — VERIFIED
 * registry/entity/WildFire/WildfireEntity.java) instead of a {@code WildfireRenderState} field that
 * no longer exists. The soul-fire sprites are built the same way {@code ModelBakery.FIRE_0}/
 * {@code FIRE_1} themselves are (VERIFIED ModelBakery.java:65-66) pointed at vanilla's own soul-fire
 * block textures, which 1.20.1 already ships (soul campfires/soul fire use them).
 */
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
