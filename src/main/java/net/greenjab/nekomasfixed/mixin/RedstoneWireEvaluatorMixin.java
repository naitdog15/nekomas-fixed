package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.item.RedstoneStrikerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code RedstoneWireEvaluator} (a redstone-experiments-era 26.x abstraction) does not exist on
 * 1.20.1 — wire power computation lives directly on {@code RedStoneWireBlock} itself (VERIFIED
 * forge-1.20.1-mapped-src: no such class or package). Retargeted onto
 * {@code RedStoneWireBlock#getSignal(BlockState, BlockGetter, BlockPos, Direction)}
 * (RedStoneWireBlock.java:344) — the public query neighbours call to read "how much power does the
 * wire here provide" — rather than the private internal power-calculation method, matching the same
 * query-not-calculation pattern SignalGetterMixin/PistonBaseBlockMixin already use for the same
 * STRUCK_WIRES mechanic.
 */
@Mixin(RedStoneWireBlock.class)
public class RedstoneWireEvaluatorMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    protected void powerWire(BlockState state, BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (level instanceof net.minecraft.world.level.Level realLevel
                && RedstoneStrikerItem.STRUCK_WIRES.containsKey(new GlobalPos(realLevel.dimension(), pos))) {
            cir.setReturnValue(15);
        }
    }
}
