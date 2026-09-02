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

/**
 * Suppresses the written-out contents list on a container tooltip, so the item-grid image drawn by
 * {@code ContainerTooltipComponent} replaces it instead of appearing underneath it.
 * <p>
 * Same purpose as before, different target. The listing used to be produced by
 * {@code ItemContainerContents#addToTooltip}, a data component that does not exist on 1.20.1
 * ({@code net.minecraft.world.item.component} as a package does not exist here at all); on this
 * version the shulker box's five item names plus {@code "container.shulkerBox.more"} come from
 * {@code ShulkerBoxBlock#appendHoverText}, reached from {@code BlockItem#appendHoverText}. That is
 * the only vanilla item on 1.20.1 that writes such a list, so it is the whole of what has to give
 * way.
 * <p>
 * The cancellation is conditional on the same {@code "Items"} list the grid is built from, which
 * keeps the two in step: a shulker box that shows no grid keeps its vanilla text. That also
 * preserves the {@code "???????"} line an unopened loot-table shulker box shows, since such a box
 * carries a {@code LootTable} and no {@code Items} at all.
 */
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
