package net.greenjab.nekomasfixed.render.other;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * No {@code ParticleGroup}/{@code ParticleGroupRenderState}/
 * {@code SubmitNodeCollector} on 1.20.1 — in 1.20.1 each {@link Particle} renders itself directly via
 * the classic {@code render(VertexConsumer, Camera, float)}, so {@code NumberParticleRenderer} (26.2's
 * separate batch-extraction class) is folded into this one class and its state-extraction/submit split
 * deleted outright — there is nothing left for it to do.
 *
 * <p><b>Documented simplification, not a port</b>: 1.20.1's particle {@code VertexConsumer} pipeline
 * draws textured quads, not arbitrary glyph text — there is no font/{@code MultiBufferSource} access
 * inside {@code Particle#render}. The original drew the live damage number as text; this renders the
 * mod's {@code textures/particle/number.png} as a single billboarded quad instead (scaled/faded by the
 * same age/damage formula). Recovering the actual per-frame digits needs a real redesign — a
 * Forge-event-driven text overlay outside the particle engine entirely, spanning wherever damage
 * events call {@code level.addParticle(...)} and the particle's own registration — flagged here
 * rather than invented unilaterally.
 */
public class NumberParticle extends Particle {
    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/particle/number.png");
    private final double damage;

    NumberParticle(ClientLevel level, double x, double y, double z, double damage) {
        super(level, x + level.getRandom().nextGaussian() / 5f, y + level.getRandom().nextGaussian() / 10f,
                z + level.getRandom().nextGaussian() / 5f);
        this.damage = damage;
        this.friction = 0.66F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.lifetime = (int) Math.min(20 + damage * 2, 50);
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
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float age = this.age + partialTicks;
        float scale = (float) (Math.sin(Math.min(age, 8) / 5) * Math.min(0.5 + this.damage / 10.0, 2)) * this.quadSize;
        float alpha = Mth.clamp((this.lifetime - age) / 8f, 0, 1);

        Vector3f center = new Vector3f((float) (Mth.lerp(partialTicks, this.xo, this.x) - camera.getPosition().x),
                (float) (Mth.lerp(partialTicks, this.yo, this.y) - camera.getPosition().y),
                (float) (Mth.lerp(partialTicks, this.zo, this.z) - camera.getPosition().z));
        Quaternionf rotation = camera.rotation();

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float[] u = {0.0F, 0.0F, 1.0F, 1.0F};
        float[] v = {1.0F, 0.0F, 0.0F, 1.0F};
        int light = LightTexture.FULL_BRIGHT;

        for (int i = 0; i < 4; i++) {
            Vector3f c = corners[i];
            c.rotate(rotation);
            c.mul(scale);
            c.add(center);
            buffer.vertex(c.x(), c.y(), c.z())
                    .uv(u[i], v[i])
                    .color(1.0F, 1.0F, 1.0F, alpha)
                    .uv2(light)
                    .endVertex();
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                        double x, double y, double z, double damage, double h, double i, RandomSource random) {
            return new NumberParticle(level, x, y, z, damage);
        }
    }
}
