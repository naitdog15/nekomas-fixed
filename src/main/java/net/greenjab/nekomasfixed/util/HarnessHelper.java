package net.greenjab.nekomasfixed.util;

/**
 * PORT: nothing this class needs survives on 1.20.1, and none of it can be faked. Every piece is
 * absent, not merely renamed:
 * <ul>
 *   <li>{@code EquipmentSlot.BODY} does not exist - 1.20.1's {@code EquipmentSlot} enum has exactly 6
 *       values (MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD; verified against
 *       forge-1.20.1-mapped-src/net/minecraft/world/entity/EquipmentSlot.java). The body slot family
 *       (horse armor via {@code EquipmentSlot.BODY}) is a 1.20.5+ addition.</li>
 *   <li>{@code net.minecraft.world.item.equipment.{Equippable,EquipmentAsset,EquipmentAssets}} do not
 *       exist as a package or classes on 1.20.1 at all - the whole equipment-rendering/component
 *       system this class is built on is 1.21.2+.</li>
 *   <li>{@code EntityTypeTags.CAN_EQUIP_HARNESS} does not exist - it is a tag that exists only because
 *       vanilla's Happy Ghast exists, and Happy Ghast is a 1.21.6 vanilla mob. 1.20.1 has no such
 *       entity, no such tag, and nothing in the game a harness could attach to even if the item and
 *       component machinery were reconstructed from scratch.</li>
 * </ul>
 * Elsewhere in this port an absent API can be retargeted onto an equivalent 1.20.1 mechanism (e.g.
 * {@code getDefaultAttributeModifiers} in place of a component). There is no equivalent to retarget
 * onto here - the vanilla feature this class hangs off of (Happy Ghast) is simply not part of the
 * game on this version. Inventing a mod-original stand-in mob to give harnesses something to attach
 * to would be new game content rather than a port, so the class is kept as an empty marker instead
 * of being deleted. The {@code ModColors}-keyed dye variants (AMBER/AQUA/INDIGO/MAROON harnesses)
 * are recorded in {@link ModEquipmentAssets}'s own doc comment.
 */
public class HarnessHelper {
}
