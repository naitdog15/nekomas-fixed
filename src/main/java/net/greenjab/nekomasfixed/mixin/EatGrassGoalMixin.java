package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.util.SpottedSheepAccess;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// canUse and tick each make exactly one grass-block check, so the mycelium case slots into that
// same check without touching the tall-grass branch.
// spotted flag goes through SpottedSheepAccess, saved under the "Spotted" NBT key - see SheepMixin
// before renaming anything there.
@Mixin(EatBlockGoal.class)
public abstract class EatGrassGoalMixin {
    @Shadow
    @Final
    private Mob mob;

    @WrapOperation(method = {"canUse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean canStartOnMycelium(BlockState instance, Block block, Operation<Boolean> original) {
        if (instance.is(Blocks.MYCELIUM)) return true;
        return original.call(instance, block);
    }

    @WrapOperation(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean eatMycelium(BlockState instance, Block block, Operation<Boolean> original) {
        if (mob instanceof Sheep sheep) {
            if (instance.is(Blocks.MYCELIUM)) {
                if (sheep.isSheared()) ((SpottedSheepAccess) sheep).nekomasfixed$setSpotted(true);
                return true;
            } else {
                if (sheep.isSheared()) ((SpottedSheepAccess) sheep).nekomasfixed$setSpotted(false);
            }
        }
        return original.call(instance, block);
    }
}
