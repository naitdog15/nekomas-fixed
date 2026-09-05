package net.greenjab.nekomasfixed.network;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/** init() is called from the mod constructor, not FMLCommonSetupEvent - channel construction must
 * complete before NetworkRegistry.lock(). */
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
