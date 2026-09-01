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

// Block.column(...) (a shorthand VoxelShape/AABB builder) does not exist on 1.20.1 (VERIFIED: zero
// matches in Block.java) — the same 14x4-wide, 0-to-1-tall detection box is built directly as an
// AABB instead. getSignalStrength(Level, BlockPos) is otherwise unchanged (WeightedPressurePlateBlock
// .java:24). .getFirst() -> .get(0), since List#getFirst is Java 21+.
@Mixin(WeightedPressurePlateBlock.class)
public class WeightedPressurePlateBlockMixin {

    @Inject(method="getSignalStrength", at = @At( value = "HEAD"), cancellable = true)
    private void TargetDummyOutput(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        // Pristine used Block.column(14.0, 0.0, 4.0), whose arguments are sixteenths of a block
        // (14 wide centered -> 1..15 sixteenths, 0..4 sixteenths tall), not whole blocks. The old
        // AABB(-7.0, 0.0, -7.0, 7.0, 4.0, 7.0) treated those sixteenths as block units, producing a
        // 14x4x14-BLOCK detection volume instead of a within-block box.
        AABB box = new AABB(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 4.0 / 16.0, 15.0 / 16.0).move(pos);
        List<TargetDummy> list = level.getEntitiesOfClass(TargetDummy.class, box, EntitySelector.ENTITY_STILL_ALIVE);
        if (!list.isEmpty()) {
            cir.setReturnValue(Math.min(list.get(0).getLastDamage(), 15));
        }
    }
}
