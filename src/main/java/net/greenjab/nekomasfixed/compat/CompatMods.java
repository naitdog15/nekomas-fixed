package net.greenjab.nekomasfixed.compat;

import net.minecraftforge.fml.ModList;

/**
 * borrowed content is reached by id (see the compat sub-packages), never by importing a companion
 * mod's classes, so a missing companion is an empty lookup rather than a crash.
 * these checks are only valid from the mod constructor onwards - anything earlier (a mixin config
 * plugin, say) needs a check that doesn't depend on ModList being built.
 */
public final class CompatMods {
    public static final String VANILLA_BACKPORT = "vanillabackport";
    public static final String NEW_TRIALS = "ntrials";
    public static final String NO_ADDED_SUGAR = "noaddedsugar";
    public static final String CLOTH_CONFIG = "cloth_config";

    private CompatMods() {
    }

    public static boolean vanillaBackportLoaded() {
        return ModList.get().isLoaded(VANILLA_BACKPORT);
    }

    public static boolean newTrialsLoaded() {
        return ModList.get().isLoaded(NEW_TRIALS);
    }
}
