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
 * A wire the redstone striker has hit reads as fully powered. Wire power lives on
 * {@code RedStoneWireBlock} itself here, and this hooks the same "how much power does this provide"
 * query that {@code SignalGetterMixin} and {@code PistonBaseBlockMixin} answer for the striker.
 */
@Mixin(RedStoneWireBlock.class)
public class RedstoneWireEvaluatorMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    protected void powerWire(BlockState state, BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (level instanceof net.minecraft.world.level.Level realLevel
                && RedstoneStrikerItem.STRUCK_WIRES.containsKey(GlobalPos.of(realLevel.dimension(), pos))) {
            cir.setReturnValue(15);
        }
    }
}
