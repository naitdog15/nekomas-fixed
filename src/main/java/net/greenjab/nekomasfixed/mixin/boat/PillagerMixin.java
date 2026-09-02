package net.greenjab.nekomasfixed.mixin.boat;

import net.greenjab.nekomasfixed.compat.vanillabackport.BackportedContent;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
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
 * A pillager firing from the deck of a boat needs more reach than one standing in a field, so both
 * its hold-ground radius and its crossbow range go up. Equipment is pinned to the crossbow so a
 * captain's crew is always armed the same way.
 */
@Mixin(Pillager.class)
public class PillagerMixin {

    // The hold-ground radius: the only 10.0F in registerGoals, and reached by constant rather than
    // by argument because the goal is an inner class whose constructor carries a hidden outer-instance
    // parameter.
    @ModifyConstant(method = "registerGoals", constant = @Constant(floatValue = 10.0f))
    private float shootFurther(float distance) {
        return 15;
    }

    // The crossbow goal's own range, taken as the third constructor argument rather than as a
    // constant so it cannot be confused with the radius above.
    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;<init>(Lnet/minecraft/world/entity/monster/Monster;DF)V"), index = 2)
    private float shootFurther2(float distance) {
        return 12;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    protected void initSpearEquipment(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Pillager pillager = (Pillager)(Object)this;
        // One in twenty carries a spear instead, when there is a spear to carry.
        if (NekomasFixedConfig.SPEAR_INTERACTIONS.get() && BackportedContent.IRON_SPEAR.isPresent()
                && random.nextInt(20) == 0) {
            pillager.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(BackportedContent.IRON_SPEAR.get()));
        } else {
            pillager.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
        ci.cancel();
    }
}
