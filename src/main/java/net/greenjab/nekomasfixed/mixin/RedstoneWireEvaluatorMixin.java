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

/**
 * A wire the redstone striker has hit reads as fully powered. There is no separate wire evaluator on
 * this version: the strength calculation lives on {@code RedStoneWireBlock} itself, and it is what
 * {@code updatePowerStrength} feeds the POWER property from, so that is where the strike is
 * answered. Doing it here rather than at the wire's own {@code getSignal} matters - it leaves the
 * block's shouldSignal bookkeeping alone, lights the struck wire itself up, and lets the rest of the
 * line fall away one step at a time the way it would from any other source.
 */
@Mixin(RedStoneWireBlock.class)
public class RedstoneWireEvaluatorMixin {
    @Inject(method = "calculateTargetStrength", at = @At("HEAD"), cancellable = true)
    protected void powerWire(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (RedstoneStrikerItem.STRUCK_WIRES.containsKey(GlobalPos.of(level.dimension(), pos))) {
            cir.setReturnValue(15);
        }
    }
}
