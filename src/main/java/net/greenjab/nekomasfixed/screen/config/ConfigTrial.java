package net.greenjab.nekomasfixed.screen.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * every toggle reads its current value out of the toml files and writes it straight back, so the
 * screen and a hand-edited file can never disagree; closing the screen saves both.
 * categories mirror the files' own grouping, with mod-gated options collected under Connected Mods.
 */
public class ConfigTrial {

    public static Screen createConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(Component.translatable("config.nekomasfixed.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        nether(builder, entryBuilder);
        world(builder, entryBuilder);
        combat(builder, entryBuilder);
        mechanics(builder, entryBuilder);
        display(builder, entryBuilder);
        connectedMods(builder, entryBuilder);

        builder.setSavingRunnable(ConfigTrial::save);

        return builder.build();
    }

    private static void nether(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.nether"));
        heading(category, entryBuilder, "nether_improvements", 0xBA2720);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.NETHER_FOOD_ROTTING, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.MAGMA_BREAKS_TO_LAVA, 1);
    }

    private static void world(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.world"));
        heading(category, entryBuilder, "world_improvements", MapColor.COLOR_LIGHT_BLUE.col);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.COPPER_BUFF, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.CLAM_GENERATION, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.GEYSER_GENERATION, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.TERMITE_MOUND_GENERATION, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.NATURAL_MOB_SPAWNS, 2);
    }

    private static void combat(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.combat"));
        heading(category, entryBuilder, "weapons_and_armour", MapColor.COLOR_RED.col);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.SICKLE_COMBO, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.OFFHAND_ATTACK, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.FEATHER_KNOCKBACK, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.WILDFIRE_SHIELD_BLOCKING, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.EXPLOSION_RESISTANT_TEMPLATES, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.WIDE_ENCHANTMENT_TARGETS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.TARGET_DUMMY_RESISTS_EXPLOSIONS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.TARGET_DUMMY_COUNTS_AS_UNDEAD, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.WILDFIRE_BOMB_ARC, 2);

        heading(category, entryBuilder, "enchantments", MapColor.COLOR_PINK.col);
        note(category, entryBuilder, "enchantments");

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.DISMOUNT_ENCHANTMENT, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.LEECHING_ENCHANTMENT, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.SHATTER_ENCHANTMENT, 1);
    }

    private static void mechanics(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.mechanics"));
        heading(category, entryBuilder, "world_rules", MapColor.COLOR_GREEN.col);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.MESSY_BEDS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.DOLPHINS_SEEK_CORAL_REEFS, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.BEES_POLLINATE_MOOBLOOMS, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.FOXES_USE_POTIONS, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.SNIFFER_FINDS_MOD_SEEDS, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.REDSTONE_STRIKER, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.LIGHTNING_IN_A_BOTTLE, 2);
    }

    private static void display(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.display"));
        heading(category, entryBuilder, "your_own_screen", MapColor.COLOR_YELLOW.col);

        toggle(category, entryBuilder, NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.FLOATING_DAMAGE_NUMBERS, 1);
        toggle(category, entryBuilder, NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.CONTAINER_GRID_TOOLTIPS, 2);
        toggle(category, entryBuilder, NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.COMBO_DAMAGE_TOOLTIP, 1);
        toggle(category, entryBuilder, NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.CUSTOM_MINECART_MODEL, 1);
    }

    private static void connectedMods(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.nekomasfixed.category.connected_mods"));
        heading(category, entryBuilder, "vanilla_backport", MapColor.COLOR_PURPLE.col);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.HARNESSES, 1);
        restartToggle(category, entryBuilder, NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.HARNESS_RENDERING, 3);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.COPPER_ARMOUR_SET, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.EYEBLOSSOM_MOOBLOOM, 1);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.SPEAR_INTERACTIONS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.BACKPORTED_SLINGSHOT_AMMO, 2);

        heading(category, entryBuilder, "new_trials", MapColor.COLOR_CYAN.col);

        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.MACE_INTERACTIONS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.TRIAL_SPAWNERS_IN_FORTRESS_ROOMS, 2);
        toggle(category, entryBuilder, NekomasFixedConfig.SPEC, NekomasFixedConfig.BREEZE_SOUNDS, 1);
    }

    private static void heading(ConfigCategory category, ConfigEntryBuilder entryBuilder, String shortName, int colour) {
        category.addEntry(entryBuilder.startTextDescription(
                Component.translatable("config.nekomasfixed.heading." + shortName).withStyle(style -> style.withColor(colour))).build());
    }

    private static void note(ConfigCategory category, ConfigEntryBuilder entryBuilder, String shortName) {
        category.addEntry(entryBuilder.startTextDescription(Component.translatable("config.nekomasfixed.note." + shortName)).build());
    }

    private static String tomlKey(ForgeConfigSpec.BooleanValue option) {
        return option.getPath().get(option.getPath().size() - 1);
    }

    private static void toggle(ConfigCategory category, ConfigEntryBuilder entryBuilder,
                               ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option, int tooltipLines) {
        String key = tomlKey(option);
        Component[] lines = new Component[tooltipLines];
        for (int i = 0; i < tooltipLines; i++) {
            lines[i] = Component.translatable("config.nekomasfixed." + key + ".tooltip." + i);
        }
        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.nekomasfixed." + key + ".label"), read(spec, option))
                .setDefaultValue(option.getDefault())
                .setTooltip(lines)
                .setSaveConsumer(saved -> write(spec, option, saved))
                .build());
    }

    // for the handful of settings that are only read while the game starts
    private static void restartToggle(ConfigCategory category, ConfigEntryBuilder entryBuilder,
                                      ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option, int tooltipLines) {
        String key = tomlKey(option);
        Component[] lines = new Component[tooltipLines];
        for (int i = 0; i < tooltipLines; i++) {
            lines[i] = Component.translatable("config.nekomasfixed." + key + ".tooltip." + i);
        }
        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.nekomasfixed." + key + ".label"), read(spec, option))
                .setDefaultValue(option.getDefault())
                .setTooltip(lines)
                .setSaveConsumer(saved -> write(spec, option, saved))
                .requireRestart()
                .build());
    }

    // isLoaded check first means a value is never demanded before the spec exists.
    private static boolean read(ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option) {
        return spec.isLoaded() ? option.get() : option.getDefault();
    }

    private static void write(ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option, boolean saved) {
        if (spec.isLoaded()) {
            option.set(saved);
        }
    }

    private static void save() {
        if (NekomasFixedConfig.SPEC.isLoaded()) {
            NekomasFixedConfig.SPEC.save();
        }
        if (NekomasFixedClientConfig.SPEC.isLoaded()) {
            NekomasFixedClientConfig.SPEC.save();
        }
    }
}
