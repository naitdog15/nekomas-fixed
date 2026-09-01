package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * 1.20.1's {@code FireworkStarRecipe#assemble(CraftingContainer, RegistryAccess)} (not
 * {@code CraftingInput} — that abstraction is newer) collects dye colours into a plain
 * {@code List<Integer>}, not fastutil's {@code IntList} (VERIFIED forge-1.20.1-mapped-src
 * FireworkStarRecipe.java:92,104: {@code List<Integer> list = Lists.newArrayList(); ...
 * list.add(((DyeItem)itemstack1.getItem()).getDyeColor().getFireworkColor());}) — same intent
 * (intercept the colour value about to be recorded), different collection type to wrap.
 */
@Mixin(FireworkStarRecipe.class)
public class FireworkStarRecipeMixin {

    @WrapOperation(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean craftCustom(List<Integer> instance, Object i, Operation<Boolean> original, @Local(ordinal = 1) ItemStack itemStack) {
        Item item = itemStack.getItem();
        Integer color = (Integer) i;
        if (item.equals(ItemRegistry.AMBER_DYE.get())) color = net.greenjab.nekomasfixed.util.ModColors.AMBER.getColor();
        if (item.equals(ItemRegistry.AQUA_DYE.get())) color = net.greenjab.nekomasfixed.util.ModColors.AQUA.getColor();
        if (item.equals(ItemRegistry.MAROON_DYE.get())) color = net.greenjab.nekomasfixed.util.ModColors.MAROON.getColor();
        if (item.equals(ItemRegistry.INDIGO_DYE.get())) color = net.greenjab.nekomasfixed.util.ModColors.INDIGO.getColor();
        return original.call(instance, color);
    }
}
