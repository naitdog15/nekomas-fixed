package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.nekomasfixed.render.other.NumberParticle;
import net.greenjab.nekomasfixed.render.other.NumberParticleRenderer;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// 1.20.1's RENDER_ORDER field is built with Guava's ImmutableList.of(E,E,E,E,E) (5 args), not
// java.util.List.of(Object,Object,Object) (3 args) (VERIFIED forge-1.20.1-mapped-src
// ParticleEngine.java:75).
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static List<ParticleRenderType> addParticleToList(List<ParticleRenderType> original){
        ArrayList<ParticleRenderType> newList = new ArrayList<>(original);
        newList.add(NumberParticle.particleTextureSheet);
        return newList.stream().toList();
    }

    @Inject(method = "createParticleGroup", at = @At(value = "HEAD"), cancellable = true)
    private void addNumberParticle(ParticleRenderType type, CallbackInfoReturnable<ParticleGroup<?>> cir) {
        if (type == NumberParticle.particleTextureSheet) {
            cir.setReturnValue(new NumberParticleRenderer((ParticleEngine)(Object)this));
        }
    }
}