package net.greenjab.nekomasfixed.network;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;

/**
 * The settings below gate code that runs on both sides - movement, dig speed, an attack the client
 * predicts - so the two must agree or the player rubber-bands against a server that disagrees with
 * them. Forge does not send a COMMON config to anyone, and a joining client has its own copy of the
 * file, so the server states these on login and they are read from here rather than from the spec.
 *
 * <p>On a server these mirror the spec. On a client they hold whatever the server last said, until
 * it disconnects.
 */
public final class ServerFlags {

    // volatile because in singleplayer the server thread writes these and the client thread reads
    // them, with no lock between the two.
    private static volatile boolean turtleArmourAbilities = true;
    private static volatile boolean offhandAttack = true;
    private static volatile boolean featherKnockback = true;
    private static volatile boolean featherFallingSavesCrops = true;
    private static volatile boolean sickleCombo = true;
    private static volatile boolean spearInteractions = true;

    /**
     * set once a REMOTE server has spoken. it only ever guards the fallback path: a server that is
     * running here is always the authority over its own file and says so through
     * {@link #fromRunningServer()}, which ignores this.
     */
    private static volatile boolean fromServer;

    private ServerFlags() {
    }

    public static boolean turtleArmourAbilities() {
        return turtleArmourAbilities;
    }

    public static boolean offhandAttack() {
        return offhandAttack;
    }

    public static boolean featherKnockback() {
        return featherKnockback;
    }

    public static boolean featherFallingSavesCrops() {
        return featherFallingSavesCrops;
    }

    public static boolean sickleCombo() {
        return sickleCombo;
    }

    public static boolean spearInteractions() {
        return spearInteractions;
    }

    /** our own file, for a client that is not connected to anything. */
    public static void fromSpec() {
        if (fromServer) {
            return;
        }
        readSpec();
    }

    /** a server running in this process outranks anything a previous one told us. */
    public static void fromRunningServer() {
        fromServer = false;
        readSpec();
    }

    private static void readSpec() {
        if (!NekomasFixedConfig.SPEC.isLoaded()) {
            return;
        }
        turtleArmourAbilities = NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get();
        offhandAttack = NekomasFixedConfig.OFFHAND_ATTACK.get();
        featherKnockback = NekomasFixedConfig.FEATHER_KNOCKBACK.get();
        featherFallingSavesCrops = NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS.get();
        sickleCombo = NekomasFixedConfig.SICKLE_COMBO.get();
        spearInteractions = NekomasFixedConfig.SPEAR_INTERACTIONS.get();
    }

    /** what the server actually sent; overrides the local file for as long as we are connected. */
    public static void fromServer(boolean turtle, boolean offhand, boolean knockback, boolean crops,
                                  boolean combo, boolean spears) {
        turtleArmourAbilities = turtle;
        offhandAttack = offhand;
        featherKnockback = knockback;
        featherFallingSavesCrops = crops;
        sickleCombo = combo;
        spearInteractions = spears;
        // set last so a reader that sees the latch also sees every value behind it
        fromServer = true;
    }

    /** back to our own file once we leave. */
    public static void forget() {
        fromServer = false;
        fromSpec();
    }

    public static ConfigFlagsPayload asPayload() {
        return new ConfigFlagsPayload(
                NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get(),
                NekomasFixedConfig.OFFHAND_ATTACK.get(),
                NekomasFixedConfig.FEATHER_KNOCKBACK.get(),
                NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS.get(),
                NekomasFixedConfig.SICKLE_COMBO.get(),
                NekomasFixedConfig.SPEAR_INTERACTIONS.get());
    }
}
