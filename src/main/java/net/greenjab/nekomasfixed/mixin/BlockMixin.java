package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "playerWillDestroy", at = @At("HEAD"))
    private void customAfterBreak(Level world, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<BlockState> cir) {
        if(state.is(Blocks.MAGMA_BLOCK) && player!=null){
            ItemStack stack = player.getMainHandItem();
            Holder<Enchantment> silkTouchEntry =
                    world.registryAccess().getOrThrow(Holder.direct(Enchantments.SILK_TOUCH).value());
            if(world.random.nextInt(5)==0&&!stack.isEnchanted() && stack.getEnchantments().getLevel(silkTouchEntry)>0 || !stack.getEnchantments().keySet().contains(silkTouchEntry) || player.getMainHandItem().isEmpty()){
                player.level().setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
            }
        }
    }
}
