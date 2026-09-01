package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.item.SickleItem;
import net.greenjab.nekomasfixed.registry.item.SlingshotItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


// Enchantability is one check here, canEnchant(ItemStack), which tests the category the enchantment
// was built with; there are no data-driven per-item predicates to hook. Individual enchantments are
// identified off their translation key ("enchantment.<namespace>.<id>"), which is the only stable
// per-enchantment string available from the base class.
@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
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
