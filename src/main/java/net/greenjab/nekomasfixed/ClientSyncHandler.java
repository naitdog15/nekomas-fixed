package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.network.UpdateClockPayload;
import net.greenjab.nekomasfixed.registry.block.entity.ClockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-side receiver for {@link UpdateClockPayload}, reached only through
 * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...)} in {@code UpdateClockPayload.handle} —
 * never called directly from common code.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSyncHandler {
    private ClientSyncHandler() {
    }

    public static void apply(UpdateClockPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }
        BlockPos pos = new BlockPos(payload.x(), payload.y(), payload.z());
        if (client.level.getBlockEntity(pos) instanceof ClockBlockEntity clockBlockEntity) {
            clockBlockEntity.setTimer(payload.timer());
            clockBlockEntity.setBell(payload.hasBell());
            clockBlockEntity.setShowsTime(payload.showsTime());
        }
    }
}
