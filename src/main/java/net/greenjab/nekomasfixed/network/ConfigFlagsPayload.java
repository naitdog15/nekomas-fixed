package net.greenjab.nekomasfixed.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Frozen wire contract: booleans, in the order below. Do not reorder, retype, add or remove a
 * field without also bumping {@link SyncHandler}'s PROTOCOL.
 *
 * <p>Plain final class, not a record, for the same reason as {@link UpdateClockPayload}:
 * {@code SimpleChannel#registerMessage} wants an instance encode reference and a static decode one.
 */
public final class ConfigFlagsPayload {

    private final boolean turtleArmourAbilities;
    private final boolean offhandAttack;
    private final boolean featherKnockback;
    private final boolean featherFallingSavesCrops;
    private final boolean sickleCombo;
    private final boolean spearInteractions;
    private final boolean wildfireShieldBlocking;
    private final boolean harnesses;

    public ConfigFlagsPayload(boolean turtleArmourAbilities, boolean offhandAttack,
                              boolean featherKnockback, boolean featherFallingSavesCrops,
                              boolean sickleCombo, boolean spearInteractions,
                              boolean wildfireShieldBlocking, boolean harnesses) {
        this.turtleArmourAbilities = turtleArmourAbilities;
        this.offhandAttack = offhandAttack;
        this.featherKnockback = featherKnockback;
        this.featherFallingSavesCrops = featherFallingSavesCrops;
        this.sickleCombo = sickleCombo;
        this.spearInteractions = spearInteractions;
        this.wildfireShieldBlocking = wildfireShieldBlocking;
        this.harnesses = harnesses;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(turtleArmourAbilities);
        buf.writeBoolean(offhandAttack);
        buf.writeBoolean(featherKnockback);
        buf.writeBoolean(featherFallingSavesCrops);
        buf.writeBoolean(sickleCombo);
        buf.writeBoolean(spearInteractions);
        buf.writeBoolean(wildfireShieldBlocking);
        buf.writeBoolean(harnesses);
    }

    public static ConfigFlagsPayload decode(FriendlyByteBuf buf) {
        return new ConfigFlagsPayload(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                buf.readBoolean());
    }

    /**
     * Unlike {@link UpdateClockPayload} there is nothing here that a dedicated server must not
     * load and no DistExecutor hop is needed.
     */
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> ServerFlags.fromServer(turtleArmourAbilities, offhandAttack,
                featherKnockback, featherFallingSavesCrops, sickleCombo, spearInteractions,
                wildfireShieldBlocking, harnesses));
        ctx.setPacketHandled(true);
    }
}
