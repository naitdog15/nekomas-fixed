package net.greenjab.nekomasfixed.util;

public class ColorMerger {
    /** per-channel average of RGB colours; white when none. */
    public static int mix(int... colors) {
        if (colors.length == 0) return 0xFFFFFF;

        int r = 0, g = 0, b = 0;
        for (int c : colors) {
            r += (c >> 16) & 0xFF;
            g += (c >> 8) & 0xFF;
            b += c & 0xFF;
        }
        int n = colors.length;
        return ((r / n) << 16) | ((g / n) << 8) | (b / n);
    }
}
