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
                .setTitle(Component.literal("Nekomas Fixed Config"));
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
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Nether Features"));
        heading(category, entryBuilder, "=== Nether Improvements ===", 0xBA2720);

        toggle(category, entryBuilder, "Do Food Rotting",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.NETHER_FOOD_ROTTING,
                "All food items except for the golden ones rot in nether over time");
    }

    private static void world(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("World Features"));
        heading(category, entryBuilder, "=== World Improvements ===", MapColor.COLOR_LIGHT_BLUE.col);

        toggle(category, entryBuilder, "Enable Copper Buffs",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.COPPER_BUFF,
                "Lightning striking a player with full copper gear would give the player speed ");
        toggle(category, entryBuilder, "Clam Generation",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.CLAM_GENERATION,
                "Scatter clams across ocean floors and coral reefs as the world generates");
        toggle(category, entryBuilder, "Geyser Generation",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.GEYSER_GENERATION,
                "Let geysers generate in the biomes they belong to");
        toggle(category, entryBuilder, "Termite Mound Generation",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.TERMITE_MOUND_GENERATION,
                "Let termite mounds, with their hive and resident termites, generate in savanna and desert");
        toggle(category, entryBuilder, "Natural Mob Spawns",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.NATURAL_MOB_SPAWNS,
                "Let this mod's mobs spawn on their own in the biomes they belong to",
                "Spawn eggs and the /summon command still work with this off");
    }

    private static void combat(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Combat"));
        heading(category, entryBuilder, "=== Weapons and Armour ===", MapColor.COLOR_RED.col);

        toggle(category, entryBuilder, "Sickle Combo",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.SICKLE_COMBO,
                "Consecutive hits with a sickle build a combo that raises the damage of the next one");
        toggle(category, entryBuilder, "Off-Hand Attacking",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.OFFHAND_ATTACK,
                "Attack with the weapon in your off hand as well as the one in your main hand");
        toggle(category, entryBuilder, "Feather Knockback",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.FEATHER_KNOCKBACK,
                "Hitting a mob with a feather deals no damage but still knocks it back");
        toggle(category, entryBuilder, "Turtle Armour Abilities",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.TURTLE_ARMOUR_ABILITIES,
                "No fall damage into water in the flippers, faster mining while standing still in the",
                "knee pads, and a damage guard while asleep in the shell");
        toggle(category, entryBuilder, "Wildfire Shield Blocking",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.WILDFIRE_SHIELD_BLOCKING,
                "The wildfire shield soaks far more of a blocked hit than an ordinary shield does");
        toggle(category, entryBuilder, "Explosion-Resistant Templates",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.EXPLOSION_RESISTANT_TEMPLATES,
                "Smithing templates survive fire and explosions instead of burning up with the rest",
                "of the drops");
        toggle(category, entryBuilder, "Wide Enchantment Targets",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.WIDE_ENCHANTMENT_TARGETS,
                "Offer enchantments the game normally restricts - sweeping, multishot, power and",
                "punch - on this mod's weapons");
        toggle(category, entryBuilder, "Target Dummy Resists Explosions",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.TARGET_DUMMY_RESISTS_EXPLOSIONS,
                "A target dummy stays planted when something explodes beside it, so its damage",
                "readout can still be read afterwards");
        toggle(category, entryBuilder, "Target Dummy Counts As Undead",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.TARGET_DUMMY_COUNTS_AS_UNDEAD,
                "A dummy built on the zombie pattern counts as undead, so Smite and Bane of",
                "Arthropods add their bonus to the readout");
        toggle(category, entryBuilder, "Wildfire Bomb Arc",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.WILDFIRE_BOMB_ARC,
                "The wildfire lobs its fire bombs along an arc worked out for how slowly they fall,",
                "rather than one that assumes an ordinary thrown item");

        heading(category, entryBuilder, "=== Enchantments ===", MapColor.COLOR_PINK.col);
        note(category, entryBuilder,
                "Each switch turns off what the enchantment does. The enchantment itself always",
                "exists, so an enchanted book or tool keeps its name and levels either way.");

        toggle(category, entryBuilder, "Dismount",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.DISMOUNT_ENCHANTMENT,
                "A hit throws the target off whatever it is riding, and shakes off anything riding",
                "the target as well");
        toggle(category, entryBuilder, "Leeching",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.LEECHING_ENCHANTMENT,
                "Heals whoever swung the weapon for a small share of the damage the hit dealt,",
                "more of it at higher levels");
        toggle(category, entryBuilder, "Shatter",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.SHATTER_ENCHANTMENT,
                "A slingshot shot bursts into five more on impact, ricocheting off whatever it struck");
    }

    private static void mechanics(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Mechanics"));
        heading(category, entryBuilder, "=== World Rules ===", MapColor.COLOR_GREEN.col);

        toggle(category, entryBuilder, "Messy Beds",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.MESSY_BEDS,
                "Beds are left unmade after sleeping, and villagers will not claim one until it has",
                "been made again with an empty hand");
        toggle(category, entryBuilder, "Feather Falling Saves Crops",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.FEATHER_FALLING_SAVES_CROPS,
                "Boots enchanted with Feather Falling stop you trampling farmland");
        toggle(category, entryBuilder, "Dolphins Seek Coral Reefs",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.DOLPHINS_SEEK_CORAL_REEFS,
                "Feed a dolphin a tropical fish and it leads you to a coral reef instead of a wreck");
        toggle(category, entryBuilder, "Bees Pollinate Mooblooms",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.BEES_POLLINATE_MOOBLOOMS,
                "Bees visit mooblooms and gather nectar from the flower on their back");
        toggle(category, entryBuilder, "Foxes Use Potions",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.FOXES_USE_POTIONS,
                "A fox carrying a potion gets the effects of whatever it is holding");
        toggle(category, entryBuilder, "Sniffers Find This Mod's Seeds",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.SNIFFER_FINDS_MOD_SEEDS,
                "Sniffers turn up this mod's seeds alongside the torchflower and pitcher pods");
        toggle(category, entryBuilder, "Redstone Striker",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.REDSTONE_STRIKER,
                "The redstone striker charges dust and components for a few ticks before the charge",
                "decays away again");
        toggle(category, entryBuilder, "Lightning In A Bottle",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.LIGHTNING_IN_A_BOTTLE,
                "A brewing stand under a lightning rod catches a strike as Lightning In A Bottle,",
                "which calls lightning down on whatever it is used on");
    }

    private static void display(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Display"));
        heading(category, entryBuilder, "=== Your Own Screen ===", MapColor.COLOR_YELLOW.col);

        toggle(category, entryBuilder, "Floating Damage Numbers",
                NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.FLOATING_DAMAGE_NUMBERS,
                "Show the damage you deal as a number floating away from whatever you hit");
        toggle(category, entryBuilder, "Container Grid Tooltips",
                NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.CONTAINER_GRID_TOOLTIPS,
                "Show a shulker box or bundle's contents as a grid of items in its tooltip instead",
                "of a plain count");
        toggle(category, entryBuilder, "Combo Damage Tooltip",
                NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.COMBO_DAMAGE_TOOLTIP,
                "Show the combo a sickle has built up as a line on its tooltip");
        toggle(category, entryBuilder, "Custom Minecart Model",
                NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.CUSTOM_MINECART_MODEL,
                "Draw minecarts with this mod's model instead of the plain one");
    }

    private static void connectedMods(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Connected Mods"));
        heading(category, entryBuilder, "=== Vanilla Backport ===", MapColor.COLOR_PURPLE.col);

        toggle(category, entryBuilder, "Harnesses",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.HARNESSES,
                "Let the four ancient-dye harnesses be worn by a happy ghast");
        restartToggle(category, entryBuilder, "Harness Rendering",
                NekomasFixedClientConfig.SPEC, NekomasFixedClientConfig.HARNESS_RENDERING,
                "Draw this mod's harnesses on a happy ghast that is wearing one",
                "Turn it off to leave the harness bare if a resource pack would rather supply that",
                "artwork itself");
        toggle(category, entryBuilder, "Copper Armour Set",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.COPPER_ARMOUR_SET,
                "Count a full copper armour set towards the copper lightning buff, alongside this",
                "mod's own copper crown and sickle");
        toggle(category, entryBuilder, "Eyeblossom Moobloom",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.EYEBLOSSOM_MOOBLOOM,
                "Add the open-eyeblossom moobloom, the fifteenth flower variant");
        toggle(category, entryBuilder, "Spear Interactions",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.SPEAR_INTERACTIONS,
                "Treat backported spears as spears here - a dispenser stabs with one and pillagers",
                "carry them on patrol");
        toggle(category, entryBuilder, "Backported Slingshot Ammo",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.BACKPORTED_SLINGSHOT_AMMO,
                "Allow copper nuggets and resin clumps as slingshot ammunition, each with its own",
                "kind of shot");

        heading(category, entryBuilder, "=== New Trials ===", MapColor.COLOR_CYAN.col);

        toggle(category, entryBuilder, "Mace Interactions",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.MACE_INTERACTIONS,
                "Turtle headgear soaks a mace smash outright, taking the whole blow in durability",
                "A smash is a mace swung while falling more than a block and a half");
        toggle(category, entryBuilder, "Trial Spawners In Fortress Rooms",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.TRIAL_SPAWNERS_IN_FORTRESS_ROOMS,
                "Put a trial spawner in the wildfire's Nether fortress room, which is otherwise built",
                "empty");
        toggle(category, entryBuilder, "Breeze Sounds",
                NekomasFixedConfig.SPEC, NekomasFixedConfig.BREEZE_SOUNDS,
                "Give the wildfire a breeze's voice instead of the blaze sounds it falls back on");
    }

    private static void heading(ConfigCategory category, ConfigEntryBuilder entryBuilder, String text, int colour) {
        category.addEntry(entryBuilder.startTextDescription(
                Component.literal(text).withStyle(style -> style.withColor(colour))).build());
    }

    // joined into one string - the widget wraps it; these args are only for readability here.
    private static void note(ConfigCategory category, ConfigEntryBuilder entryBuilder, String... text) {
        category.addEntry(entryBuilder.startTextDescription(Component.literal(String.join(" ", text))).build());
    }

    private static void toggle(ConfigCategory category, ConfigEntryBuilder entryBuilder, String label,
                               ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option, String... tooltip) {
        Component[] lines = new Component[tooltip.length];
        for (int i = 0; i < tooltip.length; i++) {
            lines[i] = Component.literal(tooltip[i]);
        }
        category.addEntry(entryBuilder.startBooleanToggle(Component.literal(label), read(spec, option))
                .setDefaultValue(option.getDefault())
                .setTooltip(lines)
                .setSaveConsumer(saved -> write(spec, option, saved))
                .build());
    }

    // for the handful of settings that are only read while the game starts
    private static void restartToggle(ConfigCategory category, ConfigEntryBuilder entryBuilder, String label,
                                      ForgeConfigSpec spec, ForgeConfigSpec.BooleanValue option, String... tooltip) {
        Component[] lines = new Component[tooltip.length];
        for (int i = 0; i < tooltip.length; i++) {
            lines[i] = Component.literal(tooltip[i]);
        }
        category.addEntry(entryBuilder.startBooleanToggle(Component.literal(label), read(spec, option))
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
