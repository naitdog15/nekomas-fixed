package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// render-thread only. push returns the previous value for pop so nested renders restore correctly.
public final class GlintContext {
    @Nullable
    private static ItemStack current;

    private static final List<ItemStack> SCOPES = new ArrayList<>();

    private GlintContext() {
    }

    @Nullable
    public static ItemStack current() {
        return current;
    }

    @Nullable
    public static ItemStack push(@Nullable ItemStack stack) {
        ItemStack previous = current;
        current = stack;
        return previous;
    }

    public static void pop(@Nullable ItemStack previous) {
        current = previous;
    }

    public static void enter(@Nullable ItemStack stack) {
        SCOPES.add(current);
        current = stack;
    }

    public static void exit() {
        current = SCOPES.isEmpty() ? null : SCOPES.remove(SCOPES.size() - 1);
    }
}
