package net.greenjab.nekomasfixed.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.UUID;

/**
 * attribute modifiers aren't on Item.Properties - Item#getDefaultAttributeModifiers(EquipmentSlot)
 * is where the game reads them, so sickleAttributeModifiers/anchorAttributeModifiers hand back a
 * multimap for the item class to return instead.
 * the anchor's extra reach isn't here - reach isn't attribute-modifiable on this version.
 */
public class ModItemSettings {

    private static final UUID SICKLE_DAMAGE_MODIFIER = UUID.fromString("6f3f4b6e-5e2d-4b1a-8f7a-1e2c3d4a5b6c");
    private static final UUID SICKLE_SPEED_MODIFIER = UUID.fromString("7a4f5c7f-6f3e-4c2b-9084-2f3d4e5b6c7d");

    public static Item.Properties sickle(Tier material, float speed) {
        return new Item.Properties()
                .durability(material.getUses());
    }

    public static Multimap<Attribute, AttributeModifier> sickleAttributeModifiers(Tier material, float speed) {
        float realDamage = 0.0f;
        if (material.equals(Tiers.WOOD)) { realDamage = 1f; }
        else if (material.equals(Tiers.STONE)) { realDamage = 1.5f; }
        else if (material.equals(Tiers.IRON)) { realDamage = 2f; }
        else if (material.equals(Tiers.GOLD)) { realDamage = 3f; }
        else if (material.equals(Tiers.DIAMOND)) { realDamage = 4.5f; }
        else if (material.equals(Tiers.NETHERITE)) { realDamage = 5f; }
        return createAttributes(realDamage, speed, SICKLE_DAMAGE_MODIFIER, SICKLE_SPEED_MODIFIER);
    }

    /**
     * The combo step a sickle of this tier starts on, in percent. The cheaper the metal the bigger
     * the ramp, so a wooden sickle rewards the combo hardest and a netherite one barely needs it.
     */
    public static int sickleDefaultCombo(Tier material) {
        return (int) (10 - material.getAttackDamageBonus());
    }

    private static final UUID ANCHOR_DAMAGE_MODIFIER = UUID.fromString("8b5a6d8f-704f-4d3c-a195-3f4e5d6c7b8e");
    private static final UUID ANCHOR_SPEED_MODIFIER = UUID.fromString("9c6b7e90-815f-4e4d-b2a6-405f6e7d8c9f");

    private static Multimap<Attribute, AttributeModifier> createAttributes(float damage, float speed, UUID damageId, UUID speedId) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(damageId, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(speedId, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    public static Item.Properties anchor(float damage, float speed) {
        return new Item.Properties()
                .durability(2500);
    }

    public static Multimap<Attribute, AttributeModifier> anchorAttributeModifiers(float damage, float speed) {
        return createAttributes(damage, speed, ANCHOR_DAMAGE_MODIFIER, ANCHOR_SPEED_MODIFIER);
    }
}
