package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.block.cauldron.SoupCauldronBlock;
import net.greenjab.nekomasfixed.registry.block.entity.SoupCauldronBlockEntity;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 1.20.1 delta: the 26.2 split useItemOn(ItemStack, ...) does not exist (see BlockBehaviourMixin's
// header) — retargeted onto the combined use(BlockState, Level, BlockPos, Player, InteractionHand,
// BlockHitResult), with the item read via player.getItemInHand(hand).
@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onCauldronUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (state.getBlock() == Blocks.WATER_CAULDRON) {
            if (state.getBlock() instanceof LayeredCauldronBlock leveledCauldronBlock && leveledCauldronBlock.isFull(state)) {
                if (level.getBlockState(pos.below()).is(BlockTags.FIRE) || level.getBlockState(pos.below()).is(BlockTags.CAMPFIRES)) {
                    if (SoupCauldronBlock.FOOD_COLORS.containsKey(itemStack.getItem())) {
                        level.setBlockAndUpdate(pos, BlockRegistry.SOUP_CAULDRON.get().defaultBlockState());
                        if (level.getBlockEntity(pos) instanceof SoupCauldronBlockEntity soup ) {
                            if (soup.addInput(itemStack.copyWithCount(1)))
                                itemStack.shrink(1);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }
}