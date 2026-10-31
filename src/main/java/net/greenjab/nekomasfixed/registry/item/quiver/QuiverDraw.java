package net.greenjab.nekomasfixed.registry.item.quiver;

import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

// a draw hands vanilla a one-arrow copy; when the weapon shrinks that copy (Bow directly,
// Crossbow through split) the arrow is taken out of the quiver. ItemStack has identity equals,
// so the weak map is keyed by the copy itself.
public final class QuiverDraw {
    private QuiverDraw() {
    }

    private record Source(ItemStack quiver, boolean server) {
    }

    private static final Map<ItemStack, Source> DRAWN = Collections.synchronizedMap(new WeakHashMap<>());

    public static ItemStack tryDraw(Player player, ItemStack weapon) {
        if (!(weapon.getItem() instanceof ProjectileWeaponItem item)) return ItemStack.EMPTY;
        if (!ProjectileWeaponItem.getHeldProjectile(player, item.getSupportedHeldProjectiles()).isEmpty()) return ItemStack.EMPTY;
        Predicate<ItemStack> wanted = item.getAllSupportedProjectiles();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack held = player.getInventory().getItem(slot);
            if (!(held.getItem() instanceof QuiverItem)) continue;
            for (ItemStack arrow : new QuiverContents(StackData.readQuiver(held)).items) {
                if (!wanted.test(arrow)) continue;
                ItemStack drawn = arrow.copyWithCount(1);
                DRAWN.put(drawn, new Source(held, !player.level().isClientSide));
                return drawn;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onShrink(ItemStack stack, int n) {
        Source source = DRAWN.remove(stack);
        if (source == null || !source.server() || n <= 0) return;
        QuiverContents contents = new QuiverContents(StackData.readQuiver(source.quiver()));
        for (Iterator<ItemStack> it = contents.items.iterator(); it.hasNext(); ) {
            ItemStack s = it.next();
            if (!ItemStack.isSameItemSameTags(s, stack)) continue;
            s.shrink(n);
            if (s.isEmpty()) it.remove();
            break;
        }
        StackData.writeQuiver(source.quiver(), contents.items);
    }
}
