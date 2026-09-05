package net.greenjab.nekomasfixed.compat;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.ArtifactVersion;
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

    private static final ArtifactVersion HARNESS_TAG_VERSION = new DefaultArtifactVersion("1.1.6");

    private CompatMods() {
    }

    /**
     * harnesses are put on by the supplying mod, and how it picks them changed. up to 1.1.5.x only
     * its own harness item class could be worn, so a plain item like ours was craftable and could
     * never go on a ghast. from 1.1.6 the interaction reads the minecraft:harnesses tag, which ours
     * are in, and there is a handler to hand our textures to.
     */
    public static boolean vanillaBackportHarnesses() {
        return ModList.get().getModContainerById(VANILLA_BACKPORT)
                .map(container -> container.getModInfo().getVersion().compareTo(HARNESS_TAG_VERSION) >= 0)
                .orElse(false);
    }

    public static boolean vanillaBackportLoaded() {
        return ModList.get().isLoaded(VANILLA_BACKPORT);
    }

    public static boolean newTrialsLoaded() {
        return ModList.get().isLoaded(NEW_TRIALS);
    }
}
