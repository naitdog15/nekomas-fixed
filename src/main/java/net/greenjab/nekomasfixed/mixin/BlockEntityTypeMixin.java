package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1 delta: the target class is {@code BlockEntityType} itself (singular) — vanilla constants
 * live directly on it, the same "no separate plural holder class" shape as {@code EntityType}/{@code Item}. There is no
 * {@code BlockEntityTypeIds}-slice {@code <clinit>} indirection to widen via {@code @ModifyArg}
 * either way: each constant's valid-block set is baked into an immutable-by-construction private
 * final {@code Set<Block> validBlocks} (line 65) at {@code <clinit>} time, before any mod block is
 * registered, with no public mutator.
 * <p>
 * Retargeted onto {@code isValid(BlockGetter, BlockPos, EntityType<?>)}... no — onto
 * {@code isValid(BlockState)} ({@code return this.validBlocks.contains(state.getBlock());} is the
 * whole vanilla body) instead of trying to mutate that set: a HEAD
 * injection that returns {@code true} for this mod's own blocks achieves the identical observable
 * effect (this block entity type accepts that block) without needing to touch a private final field
 * at all — no AT, no {@code @Shadow}, no timing dependency on mod-block registration having already
 * run before some setup hook fires.
 */
@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void nekomasfixed$acceptCustomBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        if (self == BlockEntityType.SHULKER_BOX) {
            if (state.is(BlockRegistry.AMBER_SHULKER_BOX.get()) || state.is(BlockRegistry.AQUA_SHULKER_BOX.get())
                    || state.is(BlockRegistry.INDIGO_SHULKER_BOX.get()) || state.is(BlockRegistry.MAROON_SHULKER_BOX.get())) {
                cir.setReturnValue(true);
            }
        }
    }
}
