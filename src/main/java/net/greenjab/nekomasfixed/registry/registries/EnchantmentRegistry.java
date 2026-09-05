package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// none of these three carry behaviour of their own - Dismount and Leeching apply in the damage
// pipeline, Shatter on the slingshot. category is a coarse per-item-class test, not a list; the
// mod's own sickle and slingshot are widened onto Weapon/Bow in EnchantmentMixin.
public class EnchantmentRegistry {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NekomasFixed.NAMESPACE);

    public static final RegistryObject<Enchantment> DISMOUNT = ENCHANTMENTS.register("dismount", DismountEnchantment::new);
    public static final RegistryObject<Enchantment> LEECHING = ENCHANTMENTS.register("leeching", LeechingEnchantment::new);
    public static final RegistryObject<Enchantment> SHATTER = ENCHANTMENTS.register("shatter", ShatterEnchantment::new);

    public static class DismountEnchantment extends Enchantment {
        public DismountEnchantment() {
            super(Rarity.UNCOMMON, EnchantmentCategory.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        }

        @Override
        public int getMinCost(int level) {
            return 20;
        }

        @Override
        public int getMaxCost(int level) {
            return 50;
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            return super.checkCompatibility(other)
                    && other != Enchantments.MULTISHOT
                    && other != Enchantments.PIERCING
                    && !(other instanceof ShatterEnchantment);
        }
    }

    public static class LeechingEnchantment extends Enchantment {
        public LeechingEnchantment() {
            super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        }

        @Override
        public int getMaxLevel() {
            return 3;
        }

        @Override
        public int getMinCost(int level) {
            return 5 + (level - 1) * 8;
        }

        @Override
        public int getMaxCost(int level) {
            return 20 + (level - 1) * 10;
        }

        /** Not offered at the enchanting table: it comes off loot and out of trades. */
        @Override
        public boolean isTreasureOnly() {
            return true;
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            return super.checkCompatibility(other)
                    && other != Enchantments.MENDING
                    && other != Enchantments.INFINITY_ARROWS;
        }
    }

    public static class ShatterEnchantment extends Enchantment {
        public ShatterEnchantment() {
            super(Rarity.UNCOMMON, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        }

        @Override
        public int getMinCost(int level) {
            return 20;
        }

        @Override
        public int getMaxCost(int level) {
            return 50;
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            return super.checkCompatibility(other)
                    && other != Enchantments.MULTISHOT
                    && other != Enchantments.PIERCING
                    && !(other instanceof DismountEnchantment);
        }
    }
}
