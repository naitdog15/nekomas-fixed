package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * In the 26.2 source these were {@code ResourceKey<Enchantment>} constants only, with the real
 * enchantments living as datapack JSON (empty {@code "effects": {}} - the gameplay lives entirely
 * in mixins). 1.20.1 has no datapack-JSON enchantment registry at all: enchantments are a plain
 * Java {@code DeferredRegister<Enchantment>}, and {@code Enchantment} is abstract with a
 * trivial {@code protected Enchantment(Rarity, EnchantmentCategory, EquipmentSlot[])} constructor -
 * three one-line subclasses, no other members, since the JSON's own effects were already empty.
 * <p>
 * Rarity/category/slot values below are read off {@code data/nekomasfixed/enchantment/*.json}
 * (still present, still the source of truth for these numbers even though the JSON registry path
 * itself is gone): all three have {@code "weight": 5} (-&gt; {@link Enchantment.Rarity#UNCOMMON},
 * the only 1.20.1 rarity whose own weight is 5) and {@code "slots": ["mainhand"]}. Category has no
 * clean 1:1 translation (1.20.1's {@code EnchantmentCategory} is a coarser per-item-class
 * classifier, not the JSON's tag-based {@code supported_items}) - chosen by nearest existing
 * category to the JSON's {@code supported_items} tag: dismount -&gt; spears/TRIDENT (matches
 * {@code #nekomasfixed:spears}), leeching -&gt; WEAPON (matches {@code
 * #minecraft:enchantable/melee_weapon}), shatter -&gt; BOW (matches {@code
 * #nekomasfixed:enchantable/slingshot}, a projectile launcher). This governs only which items the
 * enchantment table / anvil will OFFER the enchantment for - it does not gate whether the mixins
 * that implement the actual effects fire, since the effects blocks were already empty and no
 * behaviour moves.
 * <p>
 * The target dummy's "count Smite as if the dummy were undead" rule is not an enchantment concern
 * on this version: it lives in {@code mixin/target_dummy/LivingEntityMobTypeMixin}, which reports
 * the dummy's {@code MobType} instead, so 1.20.1's own {@code DamageEnchantment} damage bonus picks
 * it up without any hook on the three subclasses below.
 */
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
    }

    public static class LeechingEnchantment extends Enchantment {
        public LeechingEnchantment() {
            super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        }
    }

    public static class ShatterEnchantment extends Enchantment {
        public ShatterEnchantment() {
            super(Rarity.UNCOMMON, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        }
    }
}
