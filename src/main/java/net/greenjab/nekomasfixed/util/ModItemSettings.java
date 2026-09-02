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
 * The shared numbers behind the mod's two hand-built weapons - the sickle family and the anchor -
 * kept in one place so the tiers stay readable next to each other.
 * <p>
 * Only the plain settings live on {@code Item.Properties}: durability, and fire resistance where a
 * tier earns it. Everything else is an override on the item class itself, because that is where the
 * game looks for it:
 * <ul>
 *   <li>attribute modifiers come from {@code Item#getDefaultAttributeModifiers(EquipmentSlot)},
 *       which is why {@link #sickleAttributeModifiers} and {@link #anchorAttributeModifiers} return
 *       a multimap for the item to hand back rather than something to hang on the settings;</li>
 *   <li>the anvil repair material is {@code Item#isValidRepairItem(ItemStack, ItemStack)} - the
 *       sickle's iron ingot and the anchor's prismarine shard sit on those two classes;</li>
 *   <li>the starting combo step is {@link #sickleDefaultCombo(Tier)}, which the sickle holds and
 *       hands out as the fallback whenever a stack carries no combo value of its own. Reading a
 *       flat zero there instead would leave every freshly crafted sickle advertising - and
 *       dealing - no combo at all.</li>
 * </ul>
 * The two UUIDs per weapon are just stable identity keys for its own modifiers; any fixed pair
 * works, they only have to stay distinct from each other.
 * <p>
 * The anchor's extra reach is not here. Reach is not something an item can modify with an attribute
 * on this version, so that part of the anchor is simply not present.
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
