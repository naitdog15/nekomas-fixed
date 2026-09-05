package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.item.RedstoneStrikerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// no separate wire evaluator on this version: strength calc lives on RedStoneWireBlock itself, feeding
// updatePowerStrength's POWER property, so the strike is answered here rather than at getSignal -
// that leaves shouldSignal bookkeeping alone and lets the rest of the line fall away normally.
@Mixin(RedStoneWireBlock.class)
public class RedstoneWireEvaluatorMixin {
    @Inject(method = "calculateTargetStrength", at = @At("HEAD"), cancellable = true)
    protected void powerWire(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (RedstoneStrikerItem.STRUCK_WIRES.containsKey(GlobalPos.of(level.dimension(), pos))) {
            cir.setReturnValue(15);
        }
    }
}
