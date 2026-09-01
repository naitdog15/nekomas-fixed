package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.greenjab.nekomasfixed.render.other.NumberParticle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the damage-number particle's provider in behind vanilla's own. Particle providers live
 * directly on {@link ParticleEngine} here (there is no separate resources holder), and the engine is
 * built well after mod registration, so pulling the registry object at this point is safe.
 *
 * <p>The particle takes {@code NO_RENDER} and draws its digits from a later render stage instead of
 * through a particle sheet, so nothing needs adding to the engine's render order and it needs no
 * {@code particles/*.json} sprite list.
 */
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void addNumberParticle(CallbackInfo ci) {
        ((ParticleEngine) (Object) this).register(ParticleRegistry.NUMBER.get(), new NumberParticle.Factory());
    }
}
