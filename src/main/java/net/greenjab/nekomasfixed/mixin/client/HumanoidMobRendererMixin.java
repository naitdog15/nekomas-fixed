package net.greenjab.nekomasfixed.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Reserved for the mob renderer. The slingshot's shorter draw, which used to be applied from here,
 * is done where the arm pose is actually built - see {@link HumanoidModelMixin#chargeDuration} -
 * because the renderer itself never touches the charge duration.
 */
@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
}
