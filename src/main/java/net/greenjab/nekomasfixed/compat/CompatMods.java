package net.greenjab.nekomasfixed.compat;

import net.minecraftforge.fml.ModList;

/**
 * The companion mods Nekoma's Fixed can borrow content from, and the presence checks that decide
 * whether that content is there. Nothing in this mod imports a companion mod's classes; every
 * borrowed block, item, sound and tag is reached by id (see the compat sub-packages), so a missing
 * companion mod is an empty lookup rather than a crash.
 *
 * <p>These checks are only valid from the mod constructor onwards. Anything that runs earlier —
 * a mixin config plugin, for instance — needs a check that does not depend on ModList being built.
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
