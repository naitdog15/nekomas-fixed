package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {

    // 1.20.1 has no precipitationType field - a cauldron carries the predicate it fills from instead,
    // so the water cauldron is the one whose predicate accepts rain. That is how the block's own
    // handlePrecipitation decides whether it fills, too.
    @Shadow @Final private Predicate<Biome.Precipitation> fillPredicate;

    @Inject(method = "handlePrecipitation", at = @At(value = "HEAD"), cancellable = true)
    private void turnIntoIce(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        if (fillPredicate.test(Biome.Precipitation.RAIN) && precipitation == Biome.Precipitation.SNOW && state.getValue(LayeredCauldronBlock.LEVEL)==3) {
            BlockState blockState = BlockRegistry.ICE_CAULDRON.get().defaultBlockState();
            level.setBlockAndUpdate(pos, blockState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
            ci.cancel();
        }
    }

}