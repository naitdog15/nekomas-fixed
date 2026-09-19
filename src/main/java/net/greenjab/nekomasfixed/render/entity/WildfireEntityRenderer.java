package net.greenjab.nekomasfixed.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModEntityLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.render.entity.model.WildfireEntityModel;
import net.greenjab.nekomasfixed.render.entity.state.WildfireEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class WildfireEntityRenderer extends MobRenderer<WildfireEntity, WildfireEntityRenderState, WildfireEntityModel> {
	private static final Identifier TEXTURE = NekomasFixed.id("textures/entity/wildfire/default.png");
	private static final Identifier TEXTURE_SOUL = NekomasFixed.id("textures/entity/wildfire/soul.png");

	public WildfireEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new WildfireEntityModel(context.bakeLayer(ModEntityLayerRegistry.WILD_FIRE)), 0.5F);
	}

	protected int getBlockLightLevel(WildfireEntity wildFireEntity, BlockPos blockPos) {
		return 15;
	}

	@Override
	public Identifier getTextureLocation(WildfireEntityRenderState state) {
		if (state.soul) return TEXTURE_SOUL;
		return TEXTURE;
	}

	public WildfireEntityRenderState createRenderState() {
		return new WildfireEntityRenderState();
	}

	public void extractRenderState(WildfireEntity wildFireEntity, WildfireEntityRenderState wildFireEntityRenderState, float f) {
		super.extractRenderState(wildFireEntity, wildFireEntityRenderState, f);
		wildFireEntityRenderState.soul = wildFireEntity.isSoulActive();
		wildFireEntityRenderState.shields = wildFireEntity.getShieldsActive();
		wildFireEntityRenderState.shieldAngle = 1-(Mth.cos(Math.PI*Mth.clamp(wildFireEntity.clientFireTime +0.5f*(f/20f)*(wildFireEntity.isOnFire()?1:-1), 0, 1))+1)/2f;
		wildFireEntityRenderState.shieldExtraSpin = wildFireEntity.clientExtraSpin+wildFireEntityRenderState.shieldAngle*4*f;
	}
}
