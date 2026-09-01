package net.greenjab.nekomasfixed.render.other;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * 1.20.1 has neither a GUI sprite atlas (VERIFIED —
 * {@code ModelManager.VANILLA_ATLASES} is a nine-entry map with no {@code gui} row) nor
 * {@code GuiGraphics#blitSprite(...)} at all (that method is a 1.20.2+ addition). An audit
 * found exactly nine {@code blitSprite} call sites across the whole mod — eight in
 * {@code PyrotechnicsTableScreen}, one in {@link ContainerTooltipComponent} (this
 * package) — and a single path/UV helper covers all of them: every one of the
 * nine sprites is (pre-1.20.2) its own standalone PNG under
 * {@code assets/<namespace>/textures/gui/sprites/<path>.png}, exactly the path the atlas system would
 * have stitched it from, so a classic {@code blit} against that same path — no atlas, no stitching —
 * reproduces the call 1:1. These two overloads mirror the two {@code blitSprite} shapes actually used
 * (see {@code PyrotechnicsTableScreen} for the eight call sites this mirrors).
 */
public final class LegacySpriteBlit {
    private LegacySpriteBlit() {
    }

    /** Drop-in for the 6-arg {@code blitSprite(pipeline, sprite, x, y, width, height)} — the whole
     * PNG, 1:1. */
    public static void blitSprite(GuiGraphics gfx, ResourceLocation sprite, int x, int y, int width, int height) {
        gfx.blit(spritePath(sprite), x, y, 0, 0, width, height, width, height);
    }

    /** Drop-in for the 9-arg {@code blitSprite(pipeline, sprite, spriteWidth, spriteHeight, u, v, x,
     * y, width, height)} — {@code spriteWidth}/{@code spriteHeight} are the PNG's own pixel size (for
     * UV normalisation), {@code u}/{@code v} pick a sub-rectangle out of it. Maps directly onto the
     * classic {@code blit(texture, x, y, u, v, width, height, textureWidth, textureHeight)}. */
    public static void blitSprite(GuiGraphics gfx, ResourceLocation sprite, int spriteWidth, int spriteHeight, int u, int v, int x, int y, int width, int height) {
        gfx.blit(spritePath(sprite), x, y, u, v, width, height, spriteWidth, spriteHeight);
    }

    private static ResourceLocation spritePath(ResourceLocation sprite) {
        return new ResourceLocation(sprite.getNamespace(), "textures/gui/sprites/" + sprite.getPath() + ".png");
    }
}
