package net.greenjab.nekomasfixed.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Everything that ends up in {@code nekomasfixed-client.toml}: the switches whose whole effect is
 * what your own game draws. They live apart from the common file because turning one off changes
 * nothing another player could see, and a dedicated server has no use for any of them.
 *
 * <p>Read the same way as the common switches — at the moment the feature draws, never cached — so
 * a change to the file shows up as soon as it is reloaded.
 */
public final class NekomasFixedClientConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue FLOATING_DAMAGE_NUMBERS;
    public static final ForgeConfigSpec.BooleanValue CONTAINER_GRID_TOOLTIPS;
    public static final ForgeConfigSpec.BooleanValue COMBO_DAMAGE_TOOLTIP;
    public static final ForgeConfigSpec.BooleanValue CUSTOM_MINECART_MODEL;

    public static final ForgeConfigSpec.BooleanValue HARNESS_RENDERING;

    public static final ForgeConfigSpec SPEC;

    private NekomasFixedClientConfig() {
    }

    static {
        BUILDER.comment("What this mod draws on your own screen. None of it affects anyone else.")
               .push("display");

        FLOATING_DAMAGE_NUMBERS = BUILDER
                .comment("Show the damage you deal as a number floating away from whatever you hit.")
                .define("floatingDamageNumbers", true);

        CONTAINER_GRID_TOOLTIPS = BUILDER
                .comment("Show a shulker box or bundle's contents as a grid of items in its tooltip",
                         "instead of a plain count.")
                .define("containerGridTooltips", true);

        COMBO_DAMAGE_TOOLTIP = BUILDER
                .comment("Show the combo a sickle has built up as a line on its tooltip.")
                .define("comboDamageTooltip", true);

        CUSTOM_MINECART_MODEL = BUILDER
                .comment("Draw minecarts with this mod's model instead of the plain one.")
                .define("customMinecartModel", true);

        BUILDER.pop();

        BUILDER.comment("Drawing that borrows from another mod. Each one needs the mod named in its",
                        "description to be installed.")
               .push("connected_mods");

        BUILDER.comment("-- Requires Vanilla Backport --").push("vanilla_backport");

        HARNESS_RENDERING = BUILDER
                .comment("Draw this mod's harnesses on a happy ghast that is wearing one.",
                         "Requires Vanilla Backport, whose ghast does the drawing. Turn it off to leave the",
                         "harness bare if a resource pack would rather supply that artwork itself.")
                .define("harnessRendering", true);

        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
