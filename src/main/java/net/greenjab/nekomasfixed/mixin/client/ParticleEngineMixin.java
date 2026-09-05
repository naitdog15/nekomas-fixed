package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.greenjab.nekomasfixed.render.other.NumberParticle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// engine is built well after registration, so the particle type is safe to pull here; it renders
// via the font at a later stage rather than a particle sheet, so no sprite list is needed.
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void addNumberParticle(CallbackInfo ci) {
        ((ParticleEngine) (Object) this).register(ParticleRegistry.NUMBER.get(), new NumberParticle.Factory());
    }
}
