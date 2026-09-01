package net.greenjab.nekomasfixed.util;

/**
 * PORT: HARD, VERSION-FORCED BLOCK - not attempted, and not fakeable. Every piece this class needs is
 * absent from 1.20.1, not merely renamed:
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
 * This is categorically different from every other design-gap in this port: those retarget an API to
 * an equivalent 1.20.1 mechanism (e.g. {@code getDefaultAttributeModifiers} in place of a component).
 * Here there is no equivalent 1.20.1 mechanism to retarget onto - the vanilla feature this class hangs
 * off of (Happy Ghast) simply is not part of the game on this version. Inventing a mod-original
 * stand-in mob/mechanic to give harnesses something to attach to would be new game content, not a
 * port, and is squarely a product decision (the "Happy Ghast harness" question is a candidate cut,
 * but that cut was not taken unilaterally here), so
 * the class is left as a compile error rather than silently deleted, pending that product decision.
 * {@code ModColors}-keyed dye variants
 * (AMBER/AQUA/INDIGO/MAROON harnesses) are preserved conceptually in {@link ModEquipmentAssets}'s own
 * doc comment for whenever this becomes actionable.
 */
public class HarnessHelper {
}
