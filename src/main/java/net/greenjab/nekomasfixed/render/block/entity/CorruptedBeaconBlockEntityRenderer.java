package net.greenjab.nekomasfixed.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.nekomasfixed.registry.block.entity.CorruptedBeaconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// BeaconRenderer only takes a BeaconBlockEntity here, so this draws the same beam through its static method
public class CorruptedBeaconBlockEntityRenderer implements BlockEntityRenderer<CorruptedBeaconBlockEntity> {

    public CorruptedBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CorruptedBeaconBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        BeaconRenderer.renderBeaconBeam(poseStack, buffer, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0F, level.getGameTime(),
                0, BeaconRenderer.MAX_RENDER_Y, CorruptedBeaconBlockEntity.BEAM_COLOR, 0.2F, 0.25F);
    }

    @Override
    public boolean shouldRenderOffScreen(CorruptedBeaconBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    // horizontal distance only, like a beacon, so the beam still draws when you are looking down on it
    @Override
    public boolean shouldRender(CorruptedBeaconBlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(cameraPos.multiply(1.0D, 0.0D, 1.0D), this.getViewDistance());
    }
}
