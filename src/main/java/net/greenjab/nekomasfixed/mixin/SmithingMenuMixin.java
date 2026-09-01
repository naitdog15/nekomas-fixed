package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.1's {@code SmithingMenu#createResult()} has no {@code SmithingRecipeInput}/{@code
 * RecipeHolder}/{@code Optional.ifPresentOrElse} pattern at all — it queries
 * {@code List<SmithingRecipe>} directly and populates {@code resultSlots} imperatively (VERIFIED
 * forge-1.20.1-mapped-src SmithingMenu.java:88-102), so this retargets from a
 * {@code @WrapOperation} on that (absent) call to a cancellable {@code @Inject} at {@code HEAD}
 * instead, reproducing the "no result" branch's own {@code resultSlots.setItem(0, ItemStack.EMPTY)}
 * so no stale item is left showing.
 */
@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

    @Shadow protected Container inputSlots;
    @Shadow protected ResultContainer resultSlots;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void nekomasfixed$noTurtleUpgrade(CallbackInfo ci) {
        ItemStack gear = inputSlots.getItem(1);
        if (gear.is(ItemRegistry.TURTLE_CHESTPLATE.get()) || gear.is(ItemRegistry.TURTLE_LEGGINGS.get())
                || gear.is(ItemRegistry.TURTLE_BOOTS.get())) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            ci.cancel();
        }
    }
}
