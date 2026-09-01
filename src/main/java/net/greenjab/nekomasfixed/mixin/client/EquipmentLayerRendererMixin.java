package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
 * Points the turtle armour set and the five crowns at their own worn-armour textures.
 *
 * <p>Armour texture paths are built in Forge's {@code HumanoidArmorLayer#getArmorResource}, which is
 * a Forge addition rather than a vanilla method - hence {@code remap = false} - and it is called once
 * per slot with no hint of which layer image it is after beyond the slot itself. The layer suffix
 * therefore follows the same rule the layer's own inner/outer model choice uses: the leggings slot
 * draws layer 2, everything else layer 1. The crowns never hit that branch, a helmet never being the
 * leggings slot.
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
