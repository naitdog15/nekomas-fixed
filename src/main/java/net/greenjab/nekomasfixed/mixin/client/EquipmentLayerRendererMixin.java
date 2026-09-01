package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.v2.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 1.20.1 has no {@code EquipmentLayerRenderer}/{@code EquipmentClientInfo}/{@code
 * EquipmentAssetManager}/{@code EquipmentAsset} at all — that whole indirection is 1.21.4+
 * (VERIFIED: zero matches for any of the four anywhere in forge-1.20.1-mapped-src). Armor texture
 * resolution instead lives in {@code HumanoidArmorLayer#getArmorResource(Entity, ItemStack,
 * EquipmentSlot, String)} — a FORGE-added public method (VERIFIED HumanoidArmorLayer.java; the
 * vanilla class only has a deprecated, unused {@code getArmorLocation}), so {@code remap = false}
 * throughout.
 * <p>
 * Retargeted with MixinExtras {@code @ModifyReturnValue} rather than {@code @ModifyExpressionValue}
 * on an inner {@code EquipmentAssetManager#get} call, since there is no such call to wrap — the
 * whole computation is one expression this mixin now overrides at the very end, using item identity
 * (read from {@code ItemRegistry}, which the pristine mixin never needed since it worked from a
 * resolved asset-id string) in place of the substring match on that string.
 * <p>
 * {@code addHumanoidLayers(id)} (turtle armor — a full 3-slot set) built both
 * {@code <id>_layer_1.png} and {@code <id>_layer_2.png}; {@code addMainHumanoidLayer(id, false)}
 * (the 5 helmet-only crowns) built only {@code _layer_1}. {@code getArmorResource} is called once
 * per slot with no built-in "which layer" signal beyond the slot itself, so the layer suffix is
 * reproduced from the same rule {@code HumanoidArmorLayer#usesInnerModel} already uses on 1.20.1
 * (VERIFIED: {@code slot == EquipmentSlot.LEGS} is the only inner-model, i.e. layer-2, slot) — the
 * crowns never reach it since a helmet is never the LEGS slot.
 */
@Mixin(HumanoidArmorLayer.class)
public class EquipmentLayerRendererMixin {

    @ModifyReturnValue(method = "getArmorResource", at = @At("RETURN"), remap = false)
    private ResourceLocation useNewArmorModel(ResourceLocation original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) EquipmentSlot slot) {
        Item item = stack.getItem();
        if (item == ItemRegistry.TURTLE_CHESTPLATE.get() || item == ItemRegistry.TURTLE_LEGGINGS.get()
                || item == ItemRegistry.TURTLE_BOOTS.get() || item == Items.TURTLE_HELMET) {
            return armorTexture("turtle_scute", slot);
        }
        if (item == ItemRegistry.NETHERITE_CROWN.get()) return armorTexture("netherite_crown", slot);
        if (item == ItemRegistry.COPPER_CROWN.get()) return armorTexture("copper_crown", slot);
        if (item == ItemRegistry.IRON_CROWN.get()) return armorTexture("iron_crown", slot);
        if (item == ItemRegistry.GOLDEN_CROWN.get()) return armorTexture("golden_crown", slot);
        if (item == ItemRegistry.DIAMOND_CROWN.get()) return armorTexture("diamond_crown", slot);
        return original;
    }

    @Unique
    private static ResourceLocation armorTexture(String id, EquipmentSlot slot) {
        int layer = slot == EquipmentSlot.LEGS ? 2 : 1;
        return ResourceLocation.withDefaultNamespace("textures/models/armor/" + id + "_layer_" + layer + ".png");
    }
}
