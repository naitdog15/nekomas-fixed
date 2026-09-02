package net.greenjab.nekomasfixed.network;

import net.greenjab.nekomasfixed.ClientSyncHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Frozen wire contract. Field order is x, y, z, timer as VarInt, then hasBell, showsTime as boolean.
 * Do not reorder, retype, add or remove a field without also bumping {@link SyncHandler}'s
 * {@code PROTOCOL}.
 * <p>
 * Plain final class, not a record: {@code SimpleChannel#registerMessage} needs an instance
 * {@code encode(FriendlyByteBuf)} method reference and a static {@code decode(FriendlyByteBuf)}
 * factory reference — see {@link SyncHandler#init()}.
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
     * Two Forge-specific requirements — the work runs inside
     * {@code enqueueWork}, and {@code setPacketHandled(true)} is mandatory (omitting it logs a
     * warning per packet and, in some Forge builds, disconnects). {@link ClientSyncHandler} is
     * reached through {@link DistExecutor#unsafeRunWhenOn} — the one legitimate DistExecutor use
     * in this mod — because this handler is common code that must touch client-only state.
     * <p>
     * FROZEN HANDLER SIGNATURE. An earlier draft of this handler used
     * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSyncHandler::apply)}, which
     * supplies no decoded message and therefore drops it. The shape below is the corrected, frozen
     * one and must not be "simplified" back to a bare method reference:
     * <ul>
     *   <li>{@code handle(Supplier&lt;NetworkEvent.Context&gt;)} — instance method, matching
     *       {@code SimpleChannel#registerMessage}'s {@code BiConsumer<MSG, Supplier<Context>>};</li>
     *   <li>the decoded payload is captured as {@code this} <em>inside</em> the enqueued client-side
     *       closure, so the client receives the actual message, not a fresh/empty one;</li>
     *   <li>the {@code () -> () -> ...} double lambda is required: the outer
     *       {@code Supplier&lt;Runnable&gt;} keeps the {@code @OnlyIn(Dist.CLIENT)}
     *       {@link ClientSyncHandler} reference out of any class the dedicated server verifies;</li>
     *   <li>{@code setPacketHandled(true)} is unconditional and outside the closure.</li>
     * </ul>
     */
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSyncHandler.apply(this)));
        ctx.setPacketHandled(true);
    }
}
