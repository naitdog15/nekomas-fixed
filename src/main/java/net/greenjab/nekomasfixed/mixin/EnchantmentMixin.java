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


// 1.20.1 delta: isPrimaryItem/isSupportedItem (1.21+ data-driven enchantment predicates) do not
// exist here — enchantability is a single check, canEnchant(ItemStack) (VERIFIED
// forge-1.20.1-mapped-src Enchantment.java:114), which internally tests the EquipmentCategory this
// enchantment was constructed with. Both pristine branches already only called canEnchant/checked
// description text, so retargeting onto that one method loses no behaviour.
@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Item item = stack.getItem();
        if (item instanceof SickleItem) {
            cir.setReturnValue(enchantment.canEnchant(Items.DIAMOND_SWORD.getDefaultInstance()) && enchantment.getMaxLevel()!=5 && !enchantment.description().plainCopy().toString().contains("sweeping"));
            cir.cancel();
        }
        if (item instanceof SlingshotItem) {
            cir.setReturnValue(enchantment.canEnchant(Items.FLINT_AND_STEEL.getDefaultInstance())
                            || enchantment.description().plainCopy().toString().contains("multishot")
                            || enchantment.description().plainCopy().toString().contains("power")
                            || enchantment.description().plainCopy().toString().contains("punch")
                            || enchantment.description().plainCopy().toString().contains("shatter")
            );
            cir.cancel();
        }
    }
}
