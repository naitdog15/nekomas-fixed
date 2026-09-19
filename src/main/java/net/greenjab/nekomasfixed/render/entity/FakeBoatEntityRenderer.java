package net.greenjab.nekomasfixed.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.nekomasfixed.registry.entity.FakeBoatEntity;
import net.greenjab.nekomasfixed.render.entity.state.FakeBoatEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

@Environment(EnvType.CLIENT)
public class FakeBoatEntityRenderer extends EntityRenderer<FakeBoatEntity, FakeBoatEntityRenderState> {

	public FakeBoatEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	public void submit(
			FakeBoatEntityRenderState fakeBoatEntityRenderState,
			PoseStack matrixStack,
			SubmitNodeCollector orderedRenderCommandQueue,
			CameraRenderState cameraRenderState
	) {
		super.submit(fakeBoatEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
	}

	public FakeBoatEntityRenderState createRenderState() {
		return new FakeBoatEntityRenderState();
	}
}
