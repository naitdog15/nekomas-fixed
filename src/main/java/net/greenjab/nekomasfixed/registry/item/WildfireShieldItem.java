package net.greenjab.nekomasfixed.registry.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;

/**
 * Nothing to add for the offhand slot: 1.20.1's {@link ShieldItem} already implements
 * {@code Equipable} and reports {@code EquipmentSlot.OFFHAND}, and its {@code use()} raises the
 * shield instead of swapping it into that slot - which is what "equippable, unswappable" means.
 */
public class WildfireShieldItem extends ShieldItem {
    public WildfireShieldItem(Item.Properties settings) {
        super(settings);
    }

}
