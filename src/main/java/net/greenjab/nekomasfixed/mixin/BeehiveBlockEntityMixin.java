package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.block.cauldron.HoneyCauldronBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// anchored on the bee handing over its nectar, the one point in releaseOccupant that only runs for
// a honey delivery. the hive's stored-bee record is package-private so can't appear in this handler's
// signature - the three values needed are captured instead.
@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {

    @Inject(method = "releaseOccupant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;dropOffNectar()V"))
    private static void onReleaseBee(CallbackInfoReturnable<Boolean> cir,
                                     @Local(argsOnly = true) Level level,
                                     @Local(argsOnly = true, ordinal = 0) BlockPos hivePos,
                                     @Local(argsOnly = true) BlockState state) {
        BlockPos belowPos = hivePos.below();
        BlockState belowState = level.getBlockState(belowPos);
        int i = 0;
        while (belowState.is(Blocks.AIR) && i<3) {
            belowPos = belowPos.below();
            belowState = level.getBlockState(belowPos);
            i++;
        }
        if (belowState.getBlock() == BlockRegistry.HONEY_CAULDRON.get() && state.getValue(BeehiveBlock.HONEY_LEVEL) == 5) {
            incrementHoneyLevel(level, belowPos, belowState);
        }
        else if (belowState.getBlock() == Blocks.CAULDRON && state.getValue(BeehiveBlock.HONEY_LEVEL) == 5) {
            level.setBlockAndUpdate(belowPos, BlockRegistry.HONEY_CAULDRON.get().defaultBlockState()
                    .setValue(HoneyCauldronBlock.HONEY_LEVEL, 1));
            level.playSound(null, belowPos, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

        }
    }

    @Unique
    private static void incrementHoneyLevel(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        if (state.getBlock() != BlockRegistry.HONEY_CAULDRON.get()) return;

        int currentLevel = state.getValue(HoneyCauldronBlock.HONEY_LEVEL);
        if (currentLevel >= HoneyCauldronBlock.MAX_LEVEL) return;

        level.setBlockAndUpdate(pos, state.setValue(HoneyCauldronBlock.HONEY_LEVEL, currentLevel + 1));
        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}