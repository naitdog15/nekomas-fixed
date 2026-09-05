package net.greenjab.nekomasfixed.registry.registries;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;

// no .humanoidArmor(...) or COPPER constant on ArmorMaterials here (both 1.20.5+) - on 1.20.1 an
// armor item is just new ArmorItem(ArmorMaterial, Type, Properties) and ArmorMaterial is a plain
// interface with no mod-registry of its own. COPPER is the one genuinely new tier this mod adds.
// numbers below are provisional - eyeballed between IRON and GOLD, not play-tested.
public final class ModArmorMaterials {
    private ModArmorMaterials() {
    }

    public static final ArmorMaterial COPPER = new ArmorMaterial() {
        // same per-slot base-health table vanilla's ArmorMaterials uses internally, times a
        // multiplier placed between IRON (15) and GOLD (7)
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
