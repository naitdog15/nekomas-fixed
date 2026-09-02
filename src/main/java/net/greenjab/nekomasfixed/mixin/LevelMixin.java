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

/**
 * Redstone-striker power delivery.
 *
 * <p>{@code getSignal} and {@code hasNeighborSignal} live on {@link SignalGetter} as defaults, and
 * nothing in the level hierarchy overrides them, so there is no method body anywhere to hook. What
 * this does instead is give {@code Level} the overrides it never had - every block that asks about
 * redstone power asks a {@code Level}, so putting them here catches the lot in one place, and the
 * defaults stay untouched for anything else that implements the interface.
 *
 * <p>A struck block that conducts redstone feeds its neighbours at full strength; a struck position
 * also reports as powered from outside, which is what switches on whatever component is sitting
 * there. Wire and pistons ask through their own methods - see {@code RedstoneWireEvaluatorMixin}
 * and {@code PistonBaseBlockMixin}. Charges are put down in {@code RedstoneStrikerItem} and run out
 * in {@code ServerLevelMixin}.
 */
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
