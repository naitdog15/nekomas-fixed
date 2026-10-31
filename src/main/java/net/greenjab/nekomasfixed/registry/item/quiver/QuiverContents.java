package net.greenjab.nekomasfixed.registry.item.quiver;

import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// mutable copy of a quiver's arrows; newest stack first. written back with StackData.writeQuiver.
public class QuiverContents {
    public static final int MAX_ARROWS = 9 * 64;

    public final List<ItemStack> items;

    public QuiverContents(List<ItemStack> source) {
        this.items = new ArrayList<>(source.size());
        for (ItemStack stack : source) {
            if (!stack.isEmpty()) this.items.add(stack.copy());
        }
    }

    public static boolean canItemBeInQuiver(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem && stack.getItem().canFitInsideContainerItems();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public int getTotalArrowCount() {
        int total = 0;
        for (ItemStack stack : this.items) total += stack.getCount();
        return total;
    }

    /** moves up to the free space out of toAdd; returns how many went in. */
    public int tryInsert(ItemStack toAdd) {
        if (!canItemBeInQuiver(toAdd)) return 0;
        int amount = Math.min(toAdd.getCount(), MAX_ARROWS - getTotalArrowCount());
        if (amount <= 0) return 0;
        int inserted = 0;

        for (ItemStack existing : this.items) {
            if (amount <= 0) break;
            if (!ItemStack.isSameItemSameTags(existing, toAdd)) continue;
            int add = Math.min(amount, existing.getMaxStackSize() - existing.getCount());
            if (add > 0) {
                existing.grow(add);
                amount -= add;
                inserted += add;
            }
        }
        while (amount > 0) {
            int place = Math.min(amount, toAdd.getMaxStackSize());
            this.items.add(0, toAdd.copyWithCount(place));
            amount -= place;
            inserted += place;
        }
        toAdd.shrink(inserted);
        return inserted;
    }

    public ItemStack removeOne() {
        return this.items.isEmpty() ? ItemStack.EMPTY : this.items.remove(0);
    }
}
