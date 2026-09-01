package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    // 1.20.1 delta: 26.2's Holder<Enchantment>/registry
    // lookup (Enchantments.SILK_TOUCH is a ResourceKey/Holder there) becomes 1.20.1's plain
    // Enchantment instance; ItemStack#getEnchantments() (an ItemEnchantments view) becomes
    // EnchantmentHelper.getItemEnchantmentLevel/getEnchantments(ItemStack). playerDestroy's own
    // signature (Level, Player, BlockPos, BlockState, BlockEntity, ItemStack) is unchanged between
    // 26.2 and 1.20.1 (VERIFIED against forge-1.20.1-mapped-src Block.java:352) so the injection
    // point itself needs no retarget.
    @Inject(method = "playerDestroy", at = @At("HEAD"))
    private void customAfterBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack destroyedWith, CallbackInfo ci) {
        if (state.is(Blocks.MAGMA_BLOCK) && player != null) {
            ItemStack stack = player.getMainHandItem();
            Enchantment silkTouch = Enchantments.SILK_TOUCH;
            int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, stack);
            boolean hasSilkTouch = EnchantmentHelper.getEnchantments(stack).containsKey(silkTouch);
            if (level.getRandom().nextInt(5) == 0 && !stack.isEnchanted() && silkTouchLevel > 0 || !hasSilkTouch || player.getMainHandItem().isEmpty()) {
                player.level().setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
            }
        }
    }
}
