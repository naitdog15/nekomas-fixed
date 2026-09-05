package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.goal.MoveToCoralReefGoal;
import net.greenjab.nekomasfixed.registry.registries.OtherRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Feeding a dolphin a tropical fish sets a flag it keeps for good, which the coral-reef goal below
// reads. The flag goes on the dolphin's own synched data through the public accessor, so nothing
// here has to shadow the field.
@Mixin(Dolphin.class)
public class DolphinMixin {

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void initCustomDataTracker(CallbackInfo ci) {
        ((Dolphin)(Object)this).getEntityData().define(OtherRegistry.IS_TROPICAL_FISH_FED, false);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!NekomasFixedConfig.DOLPHINS_SEEK_CORAL_REEFS.get()) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(stack.is(Items.TROPICAL_FISH)){
            Dolphin dolphin = (Dolphin)(Object)this;
            dolphin.getEntityData().set(OtherRegistry.IS_TROPICAL_FISH_FED, true);
            stack.shrink(1);
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void initCustomGoals(CallbackInfo ci){
        Dolphin dolphin = (Dolphin)(Object)this;
        dolphin.goalSelector.addGoal(7, new MoveToCoralReefGoal(dolphin));
    }
}
