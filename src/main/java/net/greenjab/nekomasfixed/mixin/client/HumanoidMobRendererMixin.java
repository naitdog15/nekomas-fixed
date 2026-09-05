package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import org.spongepowered.asm.mixin.Mixin;

// kept empty: the slingshot's shorter draw used to be applied here, but moved to where the arm pose
// is actually built - see HumanoidModelMixin#chargeDuration - since the renderer never touches it.
@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
}
