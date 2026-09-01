package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.greenjab.nekomasfixed.render.other.NumberParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// The magic ordinal=48 anchor (the 49th register(...) call in registerProviders()) is inherently
// version-fragile — 1.20.1 registers a different set/count of vanilla particles than 26.2, so that
// exact ordinal would not land on a meaningful position here even if it resolved. Retargeted to
// TAIL, after every vanilla registration completes, which needs no ordinal at all and is exactly
// as correct for "also register one more provider."
@Mixin(ParticleResources.class)
public abstract class ParticleResourcesMixin {

    @Shadow protected abstract <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void addNumberParticle(CallbackInfo ci) {
        register(ParticleRegistry.NUMBER.get(), new NumberParticle.Factory());
    }
}