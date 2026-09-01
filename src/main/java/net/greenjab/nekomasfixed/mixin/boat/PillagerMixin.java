package net.greenjab.nekomasfixed.mixin.boat;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.1 delta: {@code Pillager} lives directly under {@code net.minecraft.world.entity.monster},
 * not a {@code .illager} subpackage (VERIFIED: no {@code illager} directory exists in
 * forge-1.20.1-mapped-src's entity tree; same for {@code AbstractIllager}). {@code registerGoals()}
 * (no params) and {@code populateDefaultEquipmentSlots(RandomSource, DifficultyInstance)} are
 * unchanged, as is {@code Raider.HoldGroundAttackGoal(AbstractIllager, float)}.
 * <p>
 * NAMED GAP, not a feature cut: {@code net.minecraft.world.item.Items.IRON_SPEAR} and
 * {@code net.minecraft.world.entity.ai.goal.SpearUseGoal} — both VANILLA-namespace types the
 * pristine source imports — do not exist anywhere in forge-1.20.1-mapped-src. Whatever spear weapon
 * 26.2 ships is a real vanilla addition with no 1.20.1 counterpart at all, unlike this mod's own
 * {@code registry.entity.SpearEntity} (a thrown projectile with no vanilla melee-AI goal to launch
 * it from a Pillager's hand). Reproducing {@code SpearUseGoal} from scratch would be new AI-goal
 * construction, not a mixin retarget, and is out of scope here — so {@code initSpearEquipment} keeps
 * only its Crossbow branch (pillagers always get a crossbow, as they would with no mod installed)
 * and {@code spearGoal} is dropped outright.
 */
@Mixin(Pillager.class)
public class PillagerMixin {

    // HoldGroundAttackGoal is a non-static inner class of Raider, so its bytecode constructor
    // carries a hidden leading Raider outer-instance parameter that the old ModifyArg target
    // descriptor omitted (making it unresolvable). Retargeted as a ModifyConstant on the single
    // 10.0F hold-ground radius literal in Pillager#registerGoals instead (VERIFIED
    // forge-1.20.1-mapped-src Pillager.java:68: `new Raider.HoldGroundAttackGoal(this, 10.0F)` is
    // the only 10.0F constant in the method).
    @ModifyConstant(method = "registerGoals", constant = @Constant(floatValue = 10.0f))
    private float shootFurther(float distance) {
        return 15;
    }

    // 1.20.1 Pillager.java:69 has exactly one 8.0F, as the third constructor argument of
    // `new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F)` — a ModifyConstant with ordinal = 1 can
    // never resolve since ordinal 0 is the only match. Retargeted as a ModifyArg on that
    // constructor call, index = 2 (the float parameter).
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;<init>(Lnet/minecraft/world/entity/monster/Monster;DF)V"), index = 2)
    private float shootFurther2(float distance) {
        return 12;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    protected void initSpearEquipment(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Pillager pillager = (Pillager)(Object)this;
        pillager.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        ci.cancel();
    }
}
