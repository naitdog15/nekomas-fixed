package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// each constant's valid-block set is baked into a private final validBlocks at <clinit> time with no
// mutator, so instead of touching that field this just HEAD-injects isValid(BlockState) to return
// true for the mod's own blocks — same observable effect, no @Shadow, no timing dependency.
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
