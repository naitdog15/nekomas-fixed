package net.greenjab.nekomasfixed.render.other;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * 1.20.1 has neither a GUI sprite atlas (VERIFIED —
 * {@code ModelManager.VANILLA_ATLASES} is a nine-entry map with no {@code gui} row) nor
 * {@code GuiGraphics#blitSprite(...)} at all (that method is a 1.20.2+ addition).
 *
 * <p>For this mod's <b>own</b> sprites that is a pure rename: each one still ships as a standalone PNG
 * under {@code assets/<namespace>/textures/gui/sprites/<path>.png} — exactly the path the atlas would
 * have stitched it from — so a classic {@code blit} against that path reproduces the call 1:1. These
 * overloads mirror the {@code blitSprite} shapes the screens actually used.
 *
 * <p>It does <b>not</b> work for a vanilla sprite id: 1.20.1 ships no
 * {@code assets/minecraft/textures/gui/sprites/} directory at all, because those sprites were still
 * regions of the old per-screen sheets. Those call sites blit the pre-split sheet directly instead.
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

    /** For a sprite drawn at a size other than its own — pick a {@code sourceWidth}×{@code
     * sourceHeight} rectangle out of a {@code textureWidth}×{@code textureHeight} PNG and stretch it
     * over {@code width}×{@code height}. The atlas system used to do this scaling implicitly; without
     * an atlas the source rectangle has to be spelled out. */
    public static void blitSpriteScaled(GuiGraphics gfx, ResourceLocation sprite, int x, int y, int width, int height,
                                        int u, int v, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        gfx.blit(spritePath(sprite), x, y, width, height, u, v, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    private static ResourceLocation spritePath(ResourceLocation sprite) {
        return new ResourceLocation(sprite.getNamespace(), "textures/gui/sprites/" + sprite.getPath() + ".png");
    }
}
