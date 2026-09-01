package net.greenjab.nekomasfixed.mixin.target_dummy;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.mixins.json still names {@code
 * target_dummy.EnchantmentMixin}).
 * <p>
 * {@code Enchantment.applyEffects(List, LootContext, GenericAction)} — this mixin's original
 * target — has zero 1.20.1 counterpart and must be re-expressed as an override on the new
 * subclasses instead: a rewrite, not a retarget. {@code ConditionalEffect} — the data-driven
 * per-effect predicate type this mixin's whole mechanism (make Smite treat a zombie-flagged
 * TargetDummy as undead) depends on — does not exist at all on 1.20.1: enchantment damage bonuses
 * there are computed by {@code Enchantment} subclasses overriding {@code getDamageBonus(int,
 * MobType)} directly (VERIFIED: 1.20.1 has no {@code ConditionalEffect} class anywhere). The
 * faithful 1.20.1 shape is a mixin on the Smite-carrying {@code DamageEnchantment} class overriding
 * that method to special-case a zombie-flagged {@code TargetDummy} — new logic against a different
 * class, not a retarget of this one, and out of scope here.
 */
@Mixin(Enchantment.class)
public class EnchantmentMixin {
}
