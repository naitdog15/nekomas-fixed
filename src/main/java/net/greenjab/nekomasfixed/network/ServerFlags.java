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

    private static boolean turtleArmourAbilities = true;
    private static boolean offhandAttack = true;
    private static boolean featherKnockback = true;
    private static boolean featherFallingSavesCrops = true;

    /** set once a server has spoken, so a local config reload cannot talk over it. */
    private static boolean fromServer;

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

    /** the spec is the authority on a server, and the fallback on a client that is not connected. */
    public static void fromSpec() {
        if (fromServer || !NekomasFixedConfig.SPEC.isLoaded()) {
            return;
        }
        turtleArmourAbilities = NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES.get();
        offhandAttack = NekomasFixedConfig.OFFHAND_ATTACK.get();
        featherKnockback = NekomasFixedConfig.FEATHER_KNOCKBACK.get();
        featherFallingSavesCrops = NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS.get();
    }

    /** what the server actually sent; overrides the local file for as long as we are connected. */
    public static void fromServer(boolean turtle, boolean offhand, boolean knockback, boolean crops) {
        fromServer = true;
        turtleArmourAbilities = turtle;
        offhandAttack = offhand;
        featherKnockback = knockback;
        featherFallingSavesCrops = crops;
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
                NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS.get());
    }
}
