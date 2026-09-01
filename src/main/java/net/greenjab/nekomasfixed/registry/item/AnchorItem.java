package net.greenjab.nekomasfixed.registry.item;

import com.google.common.collect.Multimap;
import net.greenjab.nekomasfixed.util.ModItemSettings;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnchorItem extends Item {

    public static final float DAMAGE = 12.0F;
    public static final float SPEED = -3.5F;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers =
            ModItemSettings.anchorAttributeModifiers(DAMAGE, SPEED);

    public AnchorItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.PRISMARINE_SHARD);
    }
}
