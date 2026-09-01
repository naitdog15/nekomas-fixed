package net.greenjab.nekomasfixed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.1 delta: {@code FarmlandBlock} is {@code FarmBlock} here (VERIFIED
 * forge-1.20.1-mapped-src: no {@code FarmlandBlock.java} exists, only {@code FarmBlock.java}), and
 * {@code fallOn}'s last parameter is {@code float}, not {@code double} (FarmBlock.java:86). The
 * 1.21+ {@code Holder}/registry-lookup enchantment API (no {@code Registries.ENCHANTMENT} on 1.20.1
 * — see BlockMixin's matching note) is replaced by direct
 * {@code EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FEATHER_FALLING, boots)}.
 */
@Mixin(FarmBlock.class)
public class FarmlandBlockMixin {

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void preventTrample(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack boots = livingEntity.getItemBySlot(EquipmentSlot.FEET);
            int eLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FEATHER_FALLING, boots);
            if (eLevel > 0) ci.cancel();
        }
    }
}
