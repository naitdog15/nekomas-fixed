package net.greenjab.nekomasfixed.util;

/**
 * PORT: {@code net.minecraft.world.item.equipment.{EquipmentAsset, EquipmentAssets}} do not exist
 * on 1.20.1 (verified: no such package under forge-1.20.1-mapped-src/net/minecraft/world/item/) -
 * the whole equipment-rendering-asset system these 5 crown keys (netherite/copper/iron/golden/
 * diamond) described is 1.21.2+. The crown/turtle-armour hook lives in {@code
 * EquipmentLayerRendererMixin} ({@code @ModifyExpressionValue} on {@code EquipmentAssetManager#get}),
 * which cannot retarget onto a system that does not exist at all pre-1.21.2. On 1.20.1 custom armour
 * textures are described the older way (a texture path per {@code ArmorMaterial}, resolved by
 * {@code HumanoidArmorLayer}'s naming convention, not a registry of {@code EquipmentAsset} keys), so
 * the crown renderer needs a genuine redesign rather than a translation.
 */
public class ModEquipmentAssetKeys {
}
