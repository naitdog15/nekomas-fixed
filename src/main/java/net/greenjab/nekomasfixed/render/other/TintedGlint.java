/*
 * The glint texture tinting in build() is adapted from Glint Everything(26.2)
 * (https://github.com/dev-limucc/glint-everything) by Limucc-dev, used under the MIT License:
 *
 * Copyright (c) 2026 Limucc-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.greenjab.nekomasfixed.render.other;

import com.mojang.blaze3d.platform.NativeImage;
import net.greenjab.nekomasfixed.util.GlintColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// 1.20.1 port: render types are cloned vanilla glint composites (GlintRenderTypes) with the
// texture swapped for a recoloured DynamicTexture. render thread only.
public final class TintedGlint {
    private static final Logger LOGGER = LoggerFactory.getLogger("nekomasfixed/TintedGlint");

    private static final double GAMMA = 0.75;
    private static final float GAIN = 1.2f;

    private enum Kind { GLINT, GLINT_DIRECT, ENTITY_GLINT, ENTITY_GLINT_DIRECT, ARMOR_ENTITY_GLINT }

    private record TexKey(boolean armor, int color, @Nullable ResourceLocation base) {}
    private record TypeKey(Kind kind, int color, @Nullable ResourceLocation base) {}

    private static final Map<TexKey, ResourceLocation> TEXTURES = new HashMap<>();
    private static final Map<TypeKey, RenderType> TYPES = new HashMap<>();

    private TintedGlint() {}

    private static String baseTag(@Nullable ResourceLocation base) {
        return base == null ? "" : "_" + Integer.toHexString(base.hashCode());
    }

    @Nullable
    public static ResourceLocation texture(boolean armorOrEntity, int rgb, @Nullable ResourceLocation base) {
        TexKey key = new TexKey(armorOrEntity, rgb, base);
        if (TEXTURES.containsKey(key)) return TEXTURES.get(key);
        ResourceLocation result = build(armorOrEntity, rgb, base);
        TEXTURES.put(key, result);
        return result;
    }

    @Nullable
    private static RenderType type(Kind kind, int rgb, @Nullable ResourceLocation base) {
        TypeKey key = new TypeKey(kind, rgb, base);
        RenderType cached = TYPES.get(key);
        if (cached != null) return cached;
        boolean armorOrEntity = kind == Kind.ENTITY_GLINT || kind == Kind.ENTITY_GLINT_DIRECT || kind == Kind.ARMOR_ENTITY_GLINT;
        ResourceLocation tex = texture(armorOrEntity, rgb, base);
        if (tex == null) return null;
        String name = kind.name().toLowerCase(Locale.ROOT) + "_" + Integer.toHexString(rgb) + baseTag(base);
        RenderType made = switch (kind) {
            case GLINT -> GlintRenderTypes.glint(name, tex);
            case GLINT_DIRECT -> GlintRenderTypes.glintDirect(name, tex);
            case ENTITY_GLINT -> GlintRenderTypes.entityGlint(name, tex);
            case ENTITY_GLINT_DIRECT -> GlintRenderTypes.entityGlintDirect(name, tex);
            case ARMOR_ENTITY_GLINT -> GlintRenderTypes.armorEntityGlint(name, tex);
        };
        TYPES.put(key, made);
        return made;
    }

    @Nullable
    public static RenderType glint(int color, @Nullable ResourceLocation base) {
        return type(Kind.GLINT, color, base);
    }

    @Nullable
    public static RenderType glintDirect(int color, @Nullable ResourceLocation base) {
        return type(Kind.GLINT_DIRECT, color, base);
    }

    @Nullable
    public static RenderType entityGlint(int color, @Nullable ResourceLocation base) {
        return type(Kind.ENTITY_GLINT, color, base);
    }

    @Nullable
    public static RenderType entityGlintDirect(int color, @Nullable ResourceLocation base) {
        return type(Kind.ENTITY_GLINT_DIRECT, color, base);
    }

    @Nullable
    public static RenderType armorEntityGlint(int color, @Nullable ResourceLocation base) {
        return type(Kind.ARMOR_ENTITY_GLINT, color, base);
    }

    @Nullable
    private static ResourceLocation build(boolean armor, int color, @Nullable ResourceLocation base) {
        Minecraft mc = Minecraft.getInstance();

        int tint = GlintColors.lift(color, GlintColors.GLINT_FLOOR, GlintColors.GLINT_FLOOR_GRAY);
        int tintA = tint >>> 24 & 0xFF;
        if (tintA == 0) tintA = 255;
        int tintR = tint >> 16 & 0xFF;
        int tintG = tint >> 8 & 0xFF;
        int tintB = tint & 0xFF;

        ResourceLocation source = base != null ? base : (armor ? ItemRenderer.ENCHANTED_GLINT_ENTITY : ItemRenderer.ENCHANTED_GLINT_ITEM);

        try (InputStream in = mc.getResourceManager().getResourceOrThrow(source).open()) {
            NativeImage image = NativeImage.read(in);
            try {
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        // NativeImage RGBA accessors are ABGR (FastColor.ABGR32): red in the low byte
                        int pixel = image.getPixelRGBA(x, y);
                        int srcR = pixel & 0xFF;
                        int srcG = pixel >> 8 & 0xFF;
                        int srcB = pixel >> 16 & 0xFF;
                        int rawBrightness = Math.max(srcR, Math.max(srcG, srcB));
                        int brightness = Math.min(255,
                                Math.round((float) Math.pow(rawBrightness / 255.0, GAMMA) * 255f * GAIN));

                        int a = (pixel >>> 24 & 0xFF) * tintA / 255;
                        int r = brightness * tintR / 255;
                        int g = brightness * tintG / 255;
                        int b = brightness * tintB / 255;

                        image.setPixelRGBA(x, y, a << 24 | b << 16 | g << 8 | r);
                    }
                }
            } catch (RuntimeException e) {
                image.close();
                throw e;
            }

            ResourceLocation id = new ResourceLocation("nekomasfixed",
                    "tinted_glint/" + (armor ? "armor_" : "item_") + Integer.toHexString(color) + baseTag(base));
            mc.getTextureManager().register(id, new DynamicTexture(image));
            return id;
        } catch (Exception e) {
            LOGGER.warn("Could not build tinted glint texture for color {} (base {})", Integer.toHexString(color), base, e);
            return null;
        }
    }

    public static void clearCache() {
        Minecraft mc = Minecraft.getInstance();
        for (ResourceLocation id : TEXTURES.values()) {
            if (id != null) mc.getTextureManager().release(id);
        }
        TEXTURES.clear();
        TYPES.clear();
    }
}
