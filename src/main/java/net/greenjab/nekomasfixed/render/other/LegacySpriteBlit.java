package net.greenjab.nekomasfixed.render.other;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws one of this mod's GUI sprites by its sprite id.
 *
 * <p>Every sprite this mod draws ships as a standalone PNG under
 * {@code assets/<namespace>/textures/gui/sprites/<path>.png}, so all these helpers do is expand a
 * sprite id into that path and hand it to an ordinary {@code blit}. They only work for ids in this
 * mod's own namespace; a vanilla GUI sprite is still a region of a per-screen sheet, and those call
 * sites blit the sheet directly.
 */
public final class LegacySpriteBlit {
    private LegacySpriteBlit() {
    }

    /** The whole sprite, at its own size. */
    public static void blitSprite(GuiGraphics gfx, ResourceLocation sprite, int x, int y, int width, int height) {
        gfx.blit(spritePath(sprite), x, y, 0, 0, width, height, width, height);
    }

    /** A sub-rectangle of a sprite: {@code spriteWidth}/{@code spriteHeight} are the PNG's own pixel
     * size, {@code u}/{@code v} the corner of the piece wanted out of it. */
    public static void blitSprite(GuiGraphics gfx, ResourceLocation sprite, int spriteWidth, int spriteHeight, int u, int v, int x, int y, int width, int height) {
        gfx.blit(spritePath(sprite), x, y, u, v, width, height, spriteWidth, spriteHeight);
    }

    /** A sprite drawn at a size other than its own: take the {@code sourceWidth}×{@code
     * sourceHeight} rectangle at {@code u},{@code v} out of a {@code textureWidth}×{@code
     * textureHeight} PNG and stretch it over {@code width}×{@code height}. */
    public static void blitSpriteScaled(GuiGraphics gfx, ResourceLocation sprite, int x, int y, int width, int height,
                                        int u, int v, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight) {
        gfx.blit(spritePath(sprite), x, y, width, height, u, v, sourceWidth, sourceHeight, textureWidth, textureHeight);
    }

    private static ResourceLocation spritePath(ResourceLocation sprite) {
        return new ResourceLocation(sprite.getNamespace(), "textures/gui/sprites/" + sprite.getPath() + ".png");
    }
}
