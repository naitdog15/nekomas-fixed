package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.item.RedstoneStrikerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

// getSignal/hasNeighborSignal live on SignalGetter as defaults with no override anywhere in the
// level hierarchy, so there's no method body to hook - this gives Level the overrides it never had,
// catching every block that asks a Level about redstone power in one place. wire and pistons ask
// through their own methods, see RedstoneWireEvaluatorMixin and PistonBaseBlockMixin; charges are
// put down in RedstoneStrikerItem and run out in ServerLevelMixin.
@Mixin(Level.class)
public abstract class LevelMixin implements SignalGetter {
    @Unique
    private boolean isStruck(BlockPos pos) {
        return RedstoneStrikerItem.STRUCK_WIRES.containsKey(GlobalPos.of(((Level)(Object)this).dimension(), pos));
    }

    @Override
    public int getSignal(BlockPos pos, Direction direction) {
        BlockState state = this.getBlockState(pos);
        if (this.isStruck(pos) && state.isRedstoneConductor(this, pos)) {
            return 15;
        }
        int i = state.getSignal(this, pos, direction);
        return state.shouldCheckWeakPower(this, pos, direction) ? Math.max(i, this.getDirectSignalTo(pos)) : i;
    }

    @Override
    public boolean hasNeighborSignal(BlockPos pos) {
        if (this.isStruck(pos)) {
            return true;
        }
        for (Direction direction : DIRECTIONS) {
            if (this.getSignal(pos.relative(direction), direction) > 0) {
                return true;
            }
        }
        return false;
    }
}
