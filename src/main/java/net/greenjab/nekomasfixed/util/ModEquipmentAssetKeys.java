package net.greenjab.nekomasfixed.util;

/**
 * PORT: HARD, VERSION-FORCED BLOCK. {@code net.minecraft.world.item.equipment.{EquipmentAsset,
 * EquipmentAssets}} do not exist on 1.20.1 (verified: no such package under
 * forge-1.20.1-mapped-src/net/minecraft/world/item/) - the whole equipment-rendering-asset system
 * these 5 crown keys (netherite/copper/iron/golden/diamond) described is 1.21.2+. The real 1.20.1
 * crown/turtle-armour work already lives in {@code
 * EquipmentLayerRendererMixin} (a "keep and expect pain" mixin, {@code
 * @ModifyExpressionValue} on {@code EquipmentAssetManager#get}), and the
 * equipment layer is among what will not be finished this pass - that mixin cannot retarget onto a
 * system that does not exist at all pre-1.21.2. On 1.20.1 custom armour textures are described the
 * older way (a texture path per {@code ArmorMaterial}, resolved by {@code HumanoidArmorLayer}'s
 * naming convention, not a registry of {@code EquipmentAsset} keys), which is a genuine redesign for
 * whichever package ends up owning the crown renderer - not invented here.
 */
public class ModEquipmentAssetKeys {
}
