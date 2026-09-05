package net.greenjab.nekomasfixed.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// ItemContainerContents#addToTooltip (a data component) doesn't exist on 1.20.1; the shulker box's
// item list instead comes from ShulkerBoxBlock#appendHoverText, the only vanilla item that writes one.
// cancellation is conditional on the same "Items" list the grid is built from, so a box with no grid
// keeps its vanilla text - including the "???????" line an unopened loot-table box shows.
@Mixin(ShulkerBoxBlock.class)
public class ItemContainerContentsMixin {

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void replaceContentsListWithGrid(ItemStack stack, BlockGetter level, List<Component> tooltip,
                                             TooltipFlag flag, CallbackInfo ci) {
        CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
        if (blockEntityTag == null || !blockEntityTag.contains("Items", Tag.TAG_LIST)) return;
        if (blockEntityTag.getList("Items", Tag.TAG_COMPOUND).isEmpty()) return;
        ci.cancel();
    }
}
