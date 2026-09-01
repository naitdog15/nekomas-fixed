package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.model.IllagerModel;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.client.mixins.json still names
 * {@code IllagerModelMixin}).
 * <p>
 * 1.20.1's {@code IllagerModel<T extends AbstractIllager>.setupAnim(T, float, float, float, float,
 * float)} takes the entity directly (no render state — VERIFIED forge-1.20.1-mapped-src
 * IllagerModel.java:60,96), so the retarget itself (a {@code @WrapOperation} on
 * {@code AnimationUtils.swingWeaponDown(ModelPart, ModelPart, Mob, float, float)}, unchanged shape)
 * would have been straightforward. What is not portable is the condition it existed to test:
 * {@code ItemTags.SPEARS} does not exist on 1.20.1 (VERIFIED: zero matches in ItemTags.java) — the
 * same vanilla spear content gap boat.PillagerMixin already documents. Since a Pillager can now never
 * hold a spear-tagged item (that mixin's spear-equip branch is gone too), the pose this method chose
 * between "spear grip" and "default swing" can only ever be "default swing" — an @Inject/@WrapOperation
 * that can only ever call straight through is not a faithful port, just dead weight (see
 * PlayerMixin's matching note on the same category of gap). Restore this once the mod or the game
 * ships a spear-tagged weapon.
 */
@Mixin(IllagerModel.class)
public abstract class IllagerModelMixin {
}
