package net.greenjab.nekomasfixed.render.other;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// extends RenderType only to reach its protected static RenderStateShard constants.
// each factory mirrors the vanilla composite state except for the texture.
final class GlintRenderTypes extends RenderType {
    private GlintRenderTypes() {
        super("nekomasfixed_unused", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {});
    }

    private static TextureStateShard tex(ResourceLocation texture) {
        return new TextureStateShard(texture, true, false);
    }

    static RenderType glint(String name, ResourceLocation texture) {
        return create("nekomasfixed_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_GLINT_SHADER).setTextureState(tex(texture))
                        .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING)
                        .createCompositeState(false));
    }

    static RenderType glintDirect(String name, ResourceLocation texture) {
        return create("nekomasfixed_glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_GLINT_DIRECT_SHADER).setTextureState(tex(texture))
                        .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING)
                        .createCompositeState(false));
    }

    static RenderType entityGlint(String name, ResourceLocation texture) {
        return create("nekomasfixed_entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(tex(texture))
                        .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET)
                        .setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    }

    static RenderType entityGlintDirect(String name, ResourceLocation texture) {
        return create("nekomasfixed_entity_glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER).setTextureState(tex(texture))
                        .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING)
                        .createCompositeState(false));
    }

    static RenderType armorEntityGlint(String name, ResourceLocation texture) {
        return create("nekomasfixed_armor_entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER).setTextureState(tex(texture))
                        .setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    }
}
