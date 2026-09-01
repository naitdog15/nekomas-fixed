package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.client.mixins.json still names
 * {@code NameTagFeatureRendererStorageMixin}). See
 * {@link NameTagFeatureRendererMixin}'s header — {@code SubmitNodeCollection} and the {@code Submit}
 * record it targets do not exist on 1.20.1 either (same 1.21.2+ deferred-render architecture), and
 * the same {@code RenderNameTagEvent} handler in place of both mixins covers
 * this file's half of the light-value sentinel trick too.
 */
@Mixin(EntityRenderer.class)
public class NameTagFeatureRendererStorageMixin {
}
