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
 * PORT: 1.20.1's {@code Item.Properties} has no {@code .component(...)}
 * builder at all - the whole data-component system landed in 1.20.5. Every {@code .component(
 * DataComponents.X, ...)} call is therefore removed from the factory helpers below, including the
 * mod-owned {@code ComboComponent} default this file used to bake in (the only
 * factory helper that does so). Attribute modifiers move to 1.20.1's actual pre-component mechanism:
 * an {@code Item#getDefaultAttributeModifiers(EquipmentSlot)} override on the ITEM CLASS ITSELF
 * (verified against vanilla SwordItem - {@code Multimap<Attribute,AttributeModifier>}, not a
 * Properties setting). 26.2's material dispatch used {@code ToolMaterial}; 1.20.1's equivalent
 * interface is {@code Tier}/{@code Tiers} (verified: {@code getUses()}/{@code getSpeed()}/{@code
 * getAttackDamageBonus()} all present). {@code Item.BASE_ATTACK_DAMAGE_UUID}/{@code
 * BASE_ATTACK_SPEED_UUID} exist but are {@code protected} on {@code Item} (package-private access from
 * here), so this file defines its own stable UUIDs instead - functionally identical (any fixed UUID
 * works as a modifier-identity key; only reuse across an item's own modifiers matters).
 * {@code Attributes.ENTITY_INTERACTION_RANGE} also doesn't exist on 1.20.1 (a post-1.20.5 addition;
 * reach was not attribute-modifiable per-item before then) - the anchor's reach bonus simply has no
 * equivalent on this version.
 * <p>
 * {@code Item.Properties} also has no {@code .repairable(Item)} on 1.20.1 - anvil repair material is
 * an {@code Item#isValidRepairItem(ItemStack, ItemStack)} override on the item class, so the sickle's
 * iron ingot and the anchor's prismarine shard belong on SickleItem/AnchorItem, not here.
 * <p>
 * What registry/item/** has to do to hold up its end: the sickle and
 * anchor Item subclasses need to (1) call {@link #sickleAttributeModifiers}/{@link
 * #anchorAttributeModifiers} from their own {@code getDefaultAttributeModifiers(EquipmentSlot)}
 * override, (2) override {@code isValidRepairItem} with the repair material named above, and
 * (3) when reading {@code ComboComponent} via {@code StackData}, use {@link
 * #sickleDefaultCombo(Tier)} as the fallback instead of {@code StackData.readCombo}'s generic
 * hard-coded {@code 0} - the 26.2 sickle bakes a material-dependent starting combo
 * ({@code 10 - material.attackDamageBonus()}), not zero, and silently losing that changes the
 * displayed combo multiplier on a freshly-crafted sickle before any swing.
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

    /** Material-dependent starting combo, matching the 26.2 sickle's own baked default exactly. */
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

    // PORT: Attributes.ENTITY_INTERACTION_RANGE doesn't exist on 1.20.1 - the anchor's reach bonus is
    // dropped rather than reimplemented; per-item reach modification is a 1.20.5+ mechanic with no
    // attribute to attach to here.
    public static Multimap<Attribute, AttributeModifier> anchorAttributeModifiers(float damage, float speed) {
        return createAttributes(damage, speed, ANCHOR_DAMAGE_MODIFIER, ANCHOR_SPEED_MODIFIER);
    }
}
