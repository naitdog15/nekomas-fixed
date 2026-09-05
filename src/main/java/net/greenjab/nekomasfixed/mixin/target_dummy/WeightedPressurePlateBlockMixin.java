package net.greenjab.nekomasfixed.mixin.target_dummy;

import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(WeightedPressurePlateBlock.class)
public class WeightedPressurePlateBlockMixin {

    @Inject(method="getSignalStrength", at = @At( value = "HEAD"), cancellable = true)
    private void TargetDummyOutput(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        // 14 wide centered (1..15 sixteenths) and 4 tall (0..4 sixteenths), expressed directly as
        // fractions of a block rather than as sixteenths-mistaken-for-blocks.
        AABB box = new AABB(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 4.0 / 16.0, 15.0 / 16.0).move(pos);
        List<TargetDummy> list = level.getEntitiesOfClass(TargetDummy.class, box, EntitySelector.ENTITY_STILL_ALIVE);
        if (!list.isEmpty()) {
            cir.setReturnValue(Math.min(list.get(0).getLastDamage(), 15));
        }
    }
}
