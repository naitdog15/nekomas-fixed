package net.greenjab.nekomasfixed.render.other;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The floating damage number. 1.20.1's particle pipeline draws textured quads off a single sprite
 * sheet and hands {@link Particle#render} nothing but a raw {@code VertexConsumer} - no font, no
 * {@link MultiBufferSource} - so the digits cannot be drawn from inside the particle engine at all.
 * The particle therefore carries only the motion, the lifetime and the age/damage curve, takes
 * {@link ParticleRenderType#NO_RENDER}, and the text is drawn a stage later, from a
 * {@link RenderLevelStageEvent.Stage#AFTER_PARTICLES} handler that has a real buffer source to
 * batch glyphs into.
 *
 * <p>The billboard transform is vanilla's own name-tag one (camera rotation, then the -0.025 flip),
 * scaled by the same age/damage curve as before, and the digits are drawn with a shadow and no
 * background - which is exactly the look the old renderer got by submitting the number AS a name tag
 * and having two mixins force those two flags.
 */
public class NumberParticle extends Particle {

    /** Live numbers, in spawn order, drained as they die. Only ever touched on the client thread. */
    private static final List<NumberParticle> ACTIVE = new ArrayList<>();
    /**
     * The particle engine evicts its oldest entries once a render type holds too many, and an evicted
     * particle is never ticked to death, so the oldest here goes at the same point rather than
     * waiting for a death that will not arrive.
     */
    private static final int MAX_ACTIVE = 512;

    private final double damage;

    NumberParticle(ClientLevel level, double x, double y, double z, double damage) {
        super(level, x + level.getRandom().nextGaussian() / 5f, y + level.getRandom().nextGaussian() / 10f,
                z + level.getRandom().nextGaussian() / 5f);
        this.damage = damage;
        this.friction = 0.66F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.lifetime = (int) Math.min(20 + damage * 2, 50);
        while (ACTIVE.size() >= MAX_ACTIVE) ACTIVE.remove(0);
        ACTIVE.add(this);
    }

    private boolean isIn(ClientLevel current) {
        return this.level == current;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) this.remove();
        else this.move(0, 0.015, 0);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }

    /** Nothing goes through the particle sheet; {@link Events} draws the digits instead. */
    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
    }

    /** Rounded to one decimal, with a bare ".0" trimmed off so whole numbers read as whole numbers. */
    private String text() {
        String formatted = String.format("%.1f", Math.round(this.damage * 10) / 10.0);
        return formatted.endsWith(".0") ? formatted.substring(0, formatted.length() - 2) : formatted;
    }

    private void drawInto(PoseStack poseStack, MultiBufferSource buffers, Font font, Camera camera, float partialTicks) {
        float age = this.age + partialTicks;
        float scale = (float) (Math.sin(Math.min(age, 8) / 5) * Math.min(0.5 + this.damage / 10.0, 2));
        int alpha = (int) (Mth.clamp((this.lifetime - age) / 8f, 0f, 1f) * 255);
        if (alpha <= 0 || scale <= 0) return;

        String text = this.text();
        poseStack.pushPose();
        poseStack.translate(
                Mth.lerp(partialTicks, this.xo, this.x) - camera.getPosition().x,
                Mth.lerp(partialTicks, this.yo, this.y) - camera.getPosition().y,
                Mth.lerp(partialTicks, this.zo, this.z) - camera.getPosition().z);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025F * scale, -0.025F * scale, 0.025F * scale);
        font.drawInBatch(text, -font.width(text) / 2.0F, 0.0F, alpha << 24 | 0xFEFFFF, true,
                poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                        double x, double y, double z, double damage, double h, double i) {
            return new NumberParticle(level, x, y, z, damage);
        }
    }

    @Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || ACTIVE.isEmpty()) return;

            Minecraft minecraft = Minecraft.getInstance();
            MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
            PoseStack poseStack = event.getPoseStack();
            float partialTicks = event.getPartialTick();

            // dead particles are dropped here rather than in remove(), so a world change that throws
            // the whole particle engine away cannot leave this list holding onto an old level
            Iterator<NumberParticle> iterator = ACTIVE.iterator();
            while (iterator.hasNext()) {
                NumberParticle particle = iterator.next();
                if (!particle.isAlive() || !particle.isIn(minecraft.level)) {
                    iterator.remove();
                    continue;
                }
                particle.drawInto(poseStack, buffers, minecraft.font, event.getCamera(), partialTicks);
            }
            buffers.endBatch();
        }
    }
}
