package net.greenjab.nekomasfixed.network;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * The frozen wire contract. One packet, server -> client, syncing a ClockBlockEntity.
 * {@code CustomPacketPayload}/{@code StreamCodec}/{@code PayloadTypeRegistry} are all 1.20.5+ with
 * no 1.20.1 equivalent on either loader; on Forge 47.4.10 {@code SimpleChannel} is the API, not a
 * legacy fallback.
 * <p>
 * {@link #init()} is called from {@link NekomasFixed}'s constructor, not from
 * {@code FMLCommonSetupEvent} — channel construction must complete before
 * {@code NetworkRegistry.lock()}.
 */
public final class SyncHandler {
    private SyncHandler() {
    }

    /** Bump this — and only this — if the wire contract in {@link UpdateClockPayload} ever changes. */
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(NekomasFixed.id("main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    public static void init() {
        CHANNEL.registerMessage(0, UpdateClockPayload.class,
                UpdateClockPayload::encode, UpdateClockPayload::decode, UpdateClockPayload::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
