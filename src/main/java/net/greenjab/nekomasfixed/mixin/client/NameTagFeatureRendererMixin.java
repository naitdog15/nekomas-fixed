package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Left empty rather than deleted (nekomasfixed.client.mixins.json still names
 * {@code NameTagFeatureRendererMixin}).
 * <p>
 * {@code net.minecraft.client.renderer.feature.NameTagFeatureRenderer} does not exist on 1.20.1
 * (VERIFIED: no such class or package anywhere in forge-1.20.1-mapped-src) — the {@code Submit}-node
 * deferred-render architecture this and NameTagFeatureRendererStorageMixin both depend on is
 * 1.21.2+. Name-tag rendering on 1.20.1 is a single {@code EntityRenderer#renderNameTag(T,
 * Component, PoseStack, MultiBufferSource, int)} call, and the correct 1.20.1 replacement for this
 * whole pair of mixins is {@code RenderNameTagEvent}, assuming the floating-damage-number feature
 * keeps full parity in this port. {@code RenderNameTagEvent} is fired natively by every
 * {@code EntityRenderer} on Forge 1.20.1 (VERIFIED: EntityRenderer.java:66-69, unconditionally, for
 * every entity), which is not a mixin target at all — it needs a {@code @SubscribeEvent} handler on
 * the FORGE event bus, i.e. a plain Java class, which is outside {@code mixin/**} by construction.
 * <p>
 * Follow-up needed from whoever owns the client Forge-bus event holder
 * ({@code ModBusClientEvents}): add a {@code RenderNameTagEvent} handler reproducing the pristine
 * pair's floating-damage-number special-casing (bold, no shadow, a distinct "this is a damage
 * number, not a real name" marker) instead of the light-value sentinel trick
 * ({@code lightCoords() == 16516350}, itself set by NameTagFeatureRendererStorageMixin catching an
 * RGB(254,255,255) marker) these two mixins used to thread through the deferred-render pipeline —
 * {@code RenderNameTagEvent} exposes the entity and the text directly, so that whole indirection is
 * no longer needed. The producer of the marked nametag itself was not found under {@code mixin/**}
 * or in {@code WildfireEntity} (checked; that class's own {@code setCustomName} is unrelated boss-bar
 * name syncing) — whoever creates the floating-damage-number text should confirm where it lives.
 */
// Targets EntityRenderer (the class that owns renderNameTag/fires RenderNameTagEvent) purely so
// this stays a loadable, harmless no-op mixin; it deliberately declares no members.
@Mixin(EntityRenderer.class)
public class NameTagFeatureRendererMixin {
}
