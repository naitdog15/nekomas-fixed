package net.greenjab.nekomasfixed.registry.registries;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;

/**
 * 26.2's crowns declare {@code .humanoidArmor(ArmorMaterials.COPPER, ArmorType.HELMET)} - neither
 * {@code Item.Properties#humanoidArmor} nor a COPPER constant on {@code ArmorMaterials} exist on
 * 1.20.1 (both are 1.20.5+; on 1.20.1 an armor item is {@code new ArmorItem(ArmorMaterial, Type,
 * Properties)} and {@code ArmorMaterial} is a plain interface with no mod-registry of its own).
 * IRON/GOLD/DIAMOND/NETHERITE crowns and all three turtle pieces use vanilla 1.20.1
 * {@code ArmorMaterials} constants directly (verified: {@code ArmorMaterials.TURTLE} already carries
 * real defense values for all four slots on 1.20.1, not just the helmet, so no new material is
 * needed there). COPPER is the one genuinely new tier this mod adds.
 * <p>
 * <b>The COPPER numbers are provisional.</b> Durability/defense/toughness below sit between IRON and
 * GOLD by eyeballing vanilla's own progression; none of it is play-tested, so expect to rebalance.
 */
public final class ModArmorMaterials {
    private ModArmorMaterials() {
    }

    public static final ArmorMaterial COPPER = new ArmorMaterial() {
        // Same per-slot base-health table vanilla's own ArmorMaterials uses internally
        // (ArmorMaterials.java's private HEALTH_FUNCTION_FOR_TYPE), times a multiplier placed
        // between IRON (15) and GOLD (7).
        private final Map<ArmorItem.Type, Integer> baseDurability = new EnumMap<>(Map.of(
                ArmorItem.Type.BOOTS, 13,
                ArmorItem.Type.LEGGINGS, 15,
                ArmorItem.Type.CHESTPLATE, 16,
                ArmorItem.Type.HELMET, 11
        ));
        private final int durabilityMultiplier = 11;
        private final Map<ArmorItem.Type, Integer> defense = new EnumMap<>(Map.of(
                ArmorItem.Type.BOOTS, 2,
                ArmorItem.Type.LEGGINGS, 4,
                ArmorItem.Type.CHESTPLATE, 5,
                ArmorItem.Type.HELMET, 2
        ));

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return baseDurability.get(type) * durabilityMultiplier;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return defense.get(type);
        }

        @Override
        public int getEnchantmentValue() {
            return 8;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.COPPER_INGOT);
        }

        @Override
        public String getName() {
            return "nekomasfixed:copper";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };
}
