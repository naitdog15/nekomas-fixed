package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.greenjab.nekomasfixed.render.other.NumberParticle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the damage-number particle's provider in behind vanilla's own. The engine is built well
 * after registration, so the particle type is safe to pull here.
 *
 * <p>The particle draws its digits with the font from a later render stage rather than through a
 * particle sheet, so it needs no place in the engine's render order and no sprite list.
 */
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void addNumberParticle(CallbackInfo ci) {
        ((ParticleEngine) (Object) this).register(ParticleRegistry.NUMBER.get(), new NumberParticle.Factory());
    }
}
