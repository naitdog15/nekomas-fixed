package net.greenjab.nekomasfixed.network;

import net.greenjab.nekomasfixed.ClientSyncHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Frozen wire contract: x, y, z, timer as VarInt, then hasBell, showsTime as boolean. Do not
 * reorder, retype, add or remove a field without also bumping {@link SyncHandler}'s PROTOCOL.
 * <p>
 * Plain final class, not a record: {@code SimpleChannel#registerMessage} needs an instance encode
 * method reference and a static decode factory reference.
 */
public final class UpdateClockPayload {
    private final int x;
    private final int y;
    private final int z;
    private final int timer;
    private final boolean hasBell;
    private final boolean showsTime;

    public UpdateClockPayload(int x, int y, int z, int timer, boolean hasBell, boolean showsTime) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timer = timer;
        this.hasBell = hasBell;
        this.showsTime = showsTime;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public int timer() {
        return timer;
    }

    public boolean hasBell() {
        return hasBell;
    }

    public boolean showsTime() {
        return showsTime;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(x);
        buf.writeVarInt(y);
        buf.writeVarInt(z);
        buf.writeVarInt(timer);
        buf.writeBoolean(hasBell);
        buf.writeBoolean(showsTime);
    }

    public static UpdateClockPayload decode(FriendlyByteBuf buf) {
        return new UpdateClockPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readBoolean(), buf.readBoolean());
    }

    /**
     * FROZEN HANDLER SIGNATURE - do not simplify to a bare method reference. An earlier draft used
     * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSyncHandler::apply)}, which supplies
     * no decoded message and silently drops every packet. The payload must be captured as {@code this}
     * inside the enqueued closure, and the outer {@code () -> () -> ...} double lambda keeps the
     * {@code @OnlyIn(Dist.CLIENT)} {@link ClientSyncHandler} reference out of classes the dedicated
     * server verifies. {@code setPacketHandled(true)} must stay unconditional and outside the closure.
     */
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSyncHandler.apply(this)));
        ctx.setPacketHandled(true);
    }
}
