package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.util.GlintColors;
import net.greenjab.nekomasfixed.util.ModColors;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One armour-glint colour set: the 20 dye glints plus the 10 treasure ones. A plain table rather
 * than a registry, since 1.20.1 has no synced dynamic registries and the set is fixed. The stack
 * only stores the id. */
public final class PrismGlint {

    private static final Map<String, PrismGlint> ALL = new LinkedHashMap<>();
    public static final List<PrismGlint> DYE_GLINTS;
    public static final List<PrismGlint> TREASURE_GLINTS;

    private final String id;
    private final int[] colors;
    private final int cycleTicks;
    @Nullable
    private final ResourceLocation texture;
    private final String langKey;

    private PrismGlint(String id, int[] colors, int cycleTicks, @Nullable ResourceLocation texture) {
        this.id = id;
        this.colors = colors;
        this.cycleTicks = cycleTicks;
        this.texture = texture;
        this.langKey = "glint." + NekomasFixed.NAMESPACE + "." + id;
    }

    static {
        String[] dyes = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
                "cyan", "purple", "blue", "brown", "green", "red", "black", "amber", "aqua", "indigo", "maroon"};
        List<PrismGlint> dyeList = new ArrayList<>();
        for (String dye : dyes) {
            dyeList.add(add(new PrismGlint(dye, new int[]{colorOf(dye)}, 80, null)));
        }
        DYE_GLINTS = Collections.unmodifiableList(dyeList);

        List<PrismGlint> treasure = new ArrayList<>();
        treasure.add(add(treasure("water", 80, null, -1, "light_blue", "cyan", "blue")));
        treasure.add(add(treasure("fire", 80, null, -1, "yellow", "amber", "orange", "red")));
        treasure.add(add(treasure("ender", 80, null, -1, "pink", "purple")));
        treasure.add(add(treasure("rainbow", 200, null, -1, "red", "orange", "amber", "yellow", "lime", "green", "cyan", "aqua",
                "light_blue", "blue", "indigo", "purple", "magenta", "pink", "brown", "maroon", "white", "light_gray", "gray", "black")));
        treasure.add(add(treasure("frostfire", 140, null, -1, "blue", "light_blue", "cyan", "aqua", "green", "lime", "yellow",
                "orange", "amber", "red", "pink", "purple")));
        treasure.add(add(treasure("jungle", 80, null, -1, "green", "lime")));
        treasure.add(add(treasure("void", 80, null, -1, "black", "gray", "light_gray")));
        treasure.add(add(treasure("sky", 80, null, -1, "cyan", "light_blue", "white")));
        treasure.add(add(treasure("ancient", 80, null, -1, "maroon", "amber", "indigo", "aqua")));
        treasure.add(add(treasure("sculk", 80, NekomasFixed.id("textures/misc/sculk_glint.png"), 0x0A1C23, "cyan")));
        TREASURE_GLINTS = Collections.unmodifiableList(treasure);
    }

    private static PrismGlint add(PrismGlint glint) {
        ALL.put(glint.id, glint);
        return glint;
    }

    /** extra is a raw colour appended after the dye colours, or -1 for none. */
    private static PrismGlint treasure(String id, int cycleTicks, @Nullable ResourceLocation texture, int extra, String... dyeNames) {
        int[] colors = new int[dyeNames.length + (extra >= 0 ? 1 : 0)];
        for (int i = 0; i < dyeNames.length; i++) {
            colors[i] = colorOf(dyeNames[i]);
        }
        if (extra >= 0) {
            colors[dyeNames.length] = extra;
        }
        return new PrismGlint(id, colors, cycleTicks, texture);
    }

    private static int colorOf(String dye) {
        DyeColor vanilla = DyeColor.byName(dye, null);
        if (vanilla != null) {
            return vanilla.getFireworkColor() & 0xFFFFFF;
        }
        return switch (dye) {
            case "amber" -> ModColors.AMBER.getColor() & 0xFFFFFF;
            case "aqua" -> ModColors.AQUA.getColor() & 0xFFFFFF;
            case "indigo" -> ModColors.INDIGO.getColor() & 0xFFFFFF;
            case "maroon" -> ModColors.MAROON.getColor() & 0xFFFFFF;
            default -> throw new IllegalStateException("unknown glint dye " + dye);
        };
    }

    @Nullable
    public static PrismGlint byId(String id) {
        return ALL.get(id);
    }

    @Nullable
    public static PrismGlint fromStack(ItemStack stack) {
        if (stack.isEmpty() || !StackData.contains(stack, StackData.KEY_ARMOR_GLINT)) return null;
        String id = StackData.read(stack, StackData.KEY_ARMOR_GLINT, Codec.STRING, null);
        return id == null ? null : byId(id);
    }

    /** opaque 0xFFRRGGBB at the given wall-clock time. */
    public int colorAt(long millis) {
        if (colors.length == 1) {
            return 0xFF000000 | colors[0];
        }
        long cycleMillis = cycleTicks * 50L;
        long position = millis % cycleMillis;
        int n = colors.length;
        int slots = Math.max(64, n * 8);
        int slot = (int) (position * slots / cycleMillis);
        float t = slot / (float) slots * n;
        int index = (int) t;
        float frac = t - index;
        return lerp(frac, colors[index], colors[(index + 1) % n]);
    }

    private static int lerp(float t, int from, int to) {
        int r = channel(t, from >> 16 & 0xFF, to >> 16 & 0xFF);
        int g = channel(t, from >> 8 & 0xFF, to >> 8 & 0xFF);
        int b = channel(t, from & 0xFF, to & 0xFF);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static int channel(float t, int a, int b) {
        return Math.round(a + (b - a) * t);
    }

    @Nullable
    public ResourceLocation texture() {
        return texture;
    }

    public Component description() {
        int tint = GlintColors.lift(colors[0], GlintColors.TOOLTIP_FLOOR, GlintColors.TOOLTIP_FLOOR_GRAY);
        return Component.translatable(langKey).withStyle(Style.EMPTY.withColor(tint & 0xFFFFFF));
    }

    public String id() {
        return id;
    }
}
