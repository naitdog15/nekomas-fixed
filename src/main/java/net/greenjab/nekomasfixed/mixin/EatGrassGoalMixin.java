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

/**
 * The single write site is EatGrassGoalMixin.java:28-39, a @WrapOperation on
 * EatBlockGoal#tick — the target class name was already 1.20.1's own (VERIFIED: {@code EatBlockGoal}
 * exists directly, no rename needed). The retarget is the anchor, not the class: 1.20.1's
 * {@code canUse()}/{@code tick()} check the tall-grass case via a {@code Predicate<BlockState>}
 * (unrelated to this mixin) and the grass-block-below case via {@code BlockState#is(Block)} — a
 * single-overload method, not the generic {@code is(Object)} 26.2 apparently has (VERIFIED
 * forge-1.20.1-mapped-src EatBlockGoal.java: both {@code canUse} and {@code tick} call
 * {@code .is(Blocks.GRASS_BLOCK)} exactly once each). Preserve the {@code "Spotted"} NBT key this
 * writes through {@link SpottedSheepAccess}.
 */
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
