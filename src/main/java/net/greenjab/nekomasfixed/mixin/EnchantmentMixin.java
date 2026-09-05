package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.item.SickleItem;
import net.greenjab.nekomasfixed.registry.item.SlingshotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


// canEnchant (anvil) and canApplyAtEnchantingTable (table) run the same test but need separate
// injectors since canApplyAtEnchantingTable is Forge's own addition and keeps its plain name in
// every environment, so it's matched with remap = false instead of a vanilla method's remapped name.
// enchantments are recognised by translation key, the only per-enchantment string exposed here.
@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void anvilCheck(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        otherChecks(stack, cir);
    }

    @Inject(method = "canApplyAtEnchantingTable", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void enchantingTableCheck(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        otherChecks(stack, cir);
    }

    @Unique
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!NekomasFixedConfig.WIDE_ENCHANTMENT_TARGETS.get()) return;
        Enchantment enchantment = (Enchantment)(Object)this;
        String id = enchantment.getDescriptionId();
        Item item = stack.getItem();
        if (item instanceof SickleItem) {
            cir.setReturnValue(enchantment.canEnchant(Items.DIAMOND_SWORD.getDefaultInstance()) && enchantment.getMaxLevel()!=5 && !id.contains("sweeping"));
            cir.cancel();
        }
        if (item instanceof SlingshotItem) {
            cir.setReturnValue(enchantment.canEnchant(Items.FLINT_AND_STEEL.getDefaultInstance())
                            || id.contains("multishot")
                            || id.contains("power")
                            || id.contains("punch")
                            || id.contains("shatter")
            );
            cir.cancel();
        }
    }
}
