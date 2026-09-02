package net.greenjab.nekomasfixed.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Everything that ends up in {@code nekomasfixed-common.toml} — one switch per feature, all of them
 * on by default, so a pack can keep the parts of this mod it wants and turn the rest off without
 * pulling the jar out.
 *
 * <p>Every switch is read at the moment its feature runs and is never copied into a field of its
 * own, so editing the file and reloading takes effect straight away. The values do not exist yet
 * while the mod is still being constructed: registration cannot depend on them, only behaviour can.
 * Anything that borrows content from another mod lives under {@code connected_mods} and needs that
 * mod installed as well — with the mod missing the feature stays off whatever this file says.
 */
public final class NekomasFixedConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue NETHER_FOOD_ROTTING;

    public static final ForgeConfigSpec.BooleanValue COPPER_BUFF;
    public static final ForgeConfigSpec.BooleanValue CLAM_GENERATION;
    public static final ForgeConfigSpec.BooleanValue GEYSER_GENERATION;
    public static final ForgeConfigSpec.BooleanValue TERMITE_MOUND_GENERATION;
    public static final ForgeConfigSpec.BooleanValue NATURAL_MOB_SPAWNS;

    public static final ForgeConfigSpec.BooleanValue SICKLE_COMBO;
    public static final ForgeConfigSpec.BooleanValue OFFHAND_ATTACK;
    public static final ForgeConfigSpec.BooleanValue FEATHER_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue TURTLE_ARMOUR_ABILITIES;
    public static final ForgeConfigSpec.BooleanValue WILDFIRE_SHIELD_BLOCKING;
    public static final ForgeConfigSpec.BooleanValue EXPLOSION_RESISTANT_TEMPLATES;
    public static final ForgeConfigSpec.BooleanValue WIDE_ENCHANTMENT_TARGETS;
    public static final ForgeConfigSpec.BooleanValue TARGET_DUMMY_RESISTS_EXPLOSIONS;
    public static final ForgeConfigSpec.BooleanValue TARGET_DUMMY_COUNTS_AS_UNDEAD;
    public static final ForgeConfigSpec.BooleanValue WILDFIRE_BOMB_ARC;

    public static final ForgeConfigSpec.BooleanValue DISMOUNT_ENCHANTMENT;
    public static final ForgeConfigSpec.BooleanValue LEECHING_ENCHANTMENT;
    public static final ForgeConfigSpec.BooleanValue SHATTER_ENCHANTMENT;

    public static final ForgeConfigSpec.BooleanValue MESSY_BEDS;
    public static final ForgeConfigSpec.BooleanValue FEATHER_FALLING_SAVES_CROPS;
    public static final ForgeConfigSpec.BooleanValue DOLPHINS_SEEK_CORAL_REEFS;
    public static final ForgeConfigSpec.BooleanValue BEES_POLLINATE_MOOBLOOMS;
    public static final ForgeConfigSpec.BooleanValue FOXES_USE_POTIONS;
    public static final ForgeConfigSpec.BooleanValue SNIFFER_FINDS_MOD_SEEDS;
    public static final ForgeConfigSpec.BooleanValue REDSTONE_STRIKER;
    public static final ForgeConfigSpec.BooleanValue LIGHTNING_IN_A_BOTTLE;

    public static final ForgeConfigSpec.BooleanValue HARNESSES;
    public static final ForgeConfigSpec.BooleanValue COPPER_ARMOUR_SET;
    public static final ForgeConfigSpec.BooleanValue EYEBLOSSOM_MOOBLOOM;
    public static final ForgeConfigSpec.BooleanValue SPEAR_INTERACTIONS;
    public static final ForgeConfigSpec.BooleanValue BACKPORTED_SLINGSHOT_AMMO;

    public static final ForgeConfigSpec.BooleanValue MACE_INTERACTIONS;
    public static final ForgeConfigSpec.BooleanValue TRIAL_SPAWNERS_IN_FORTRESS_ROOMS;
    public static final ForgeConfigSpec.BooleanValue BREEZE_SOUNDS;

    public static final ForgeConfigSpec SPEC;

    private NekomasFixedConfig() {
    }

    static {
        BUILDER.comment("Changes to how the Nether treats what you carry into it.").push("nether_features");

        NETHER_FOOD_ROTTING = BUILDER
                .comment("All food items except for the golden ones rot in the Nether over time.")
                .define("netherFoodRotting", true);

        BUILDER.pop();

        BUILDER.comment("Things this mod grows or hides in the world itself.")
               .push("world_features");

        COPPER_BUFF = BUILDER
                .comment("Lightning striking a player wearing full copper gear gives that player Speed.",
                         "Counts this mod's own copper crown and copper sickle as well as any other copper",
                         "armour that happens to be installed.")
                .define("enableCopperBuff", true);

        CLAM_GENERATION = BUILDER
                .comment("Scatter clams across ocean floors and coral reefs as the world generates.")
                .define("clamGeneration", true);

        GEYSER_GENERATION = BUILDER
                .comment("Let geysers generate in the biomes they belong to.")
                .define("geyserGeneration", true);

        TERMITE_MOUND_GENERATION = BUILDER
                .comment("Let termite mounds, with their hive and resident termites, generate in savanna",
                         "and desert.")
                .define("termiteMoundGeneration", true);

        NATURAL_MOB_SPAWNS = BUILDER
                .comment("Let this mod's mobs spawn on their own in the biomes they belong to.",
                         "Spawn eggs and the /summon command still work with this off.")
                .define("naturalMobSpawns", true);

        BUILDER.pop();

        BUILDER.comment("Weapon, armour and shield behaviour.").push("combat");

        SICKLE_COMBO = BUILDER
                .comment("Consecutive hits with a sickle build a combo that raises the damage of the next one.")
                .define("sickleCombo", true);

        OFFHAND_ATTACK = BUILDER
                .comment("Attack with the weapon in your off hand as well as the one in your main hand.")
                .define("offhandAttack", true);

        FEATHER_KNOCKBACK = BUILDER
                .comment("Hitting a mob with a feather deals no damage but still knocks it back.")
                .define("featherKnockback", true);

        TURTLE_ARMOUR_ABILITIES = BUILDER
                .comment("The turtle set's extra abilities: no fall damage into water in the flippers,",
                         "faster mining while standing still in the knee pads, and a damage guard while",
                         "asleep in the shell.")
                .define("turtleArmourAbilities", true);

        WILDFIRE_SHIELD_BLOCKING = BUILDER
                .comment("The wildfire shield soaks far more of a blocked hit than an ordinary shield does.")
                .define("wildfireShieldBlocking", true);

        EXPLOSION_RESISTANT_TEMPLATES = BUILDER
                .comment("Smithing templates survive fire and explosions instead of burning up with the",
                         "rest of the drops.")
                .define("explosionResistantTemplates", true);

        WIDE_ENCHANTMENT_TARGETS = BUILDER
                .comment("Offer enchantments the game normally restricts — sweeping, multishot, power and",
                         "punch — on this mod's weapons.")
                .define("wideEnchantmentTargets", true);

        TARGET_DUMMY_RESISTS_EXPLOSIONS = BUILDER
                .comment("A target dummy stays planted when something explodes beside it, so its damage",
                         "readout can still be read afterwards.")
                .define("targetDummyResistsExplosions", true);

        TARGET_DUMMY_COUNTS_AS_UNDEAD = BUILDER
                .comment("A dummy built on the zombie pattern counts as undead, so Smite and Bane of",
                         "Arthropods add their bonus to the readout instead of it under-reporting what",
                         "the weapon really does to an undead mob.")
                .define("targetDummyCountsAsUndead", true);

        WILDFIRE_BOMB_ARC = BUILDER
                .comment("The wildfire lobs its fire bombs along an arc worked out for how slowly they",
                         "fall, rather than one that assumes an ordinary thrown item. With this off the",
                         "bombs sail long.")
                .define("wildfireBombArc", true);

        BUILDER.comment("The three enchantments this mod adds. Each switch turns off what the enchantment",
                        "DOES; the enchantment itself always exists, so a book or a tool that already has",
                        "one keeps its name and its levels and starts working again the moment the switch",
                        "goes back on.")
               .push("enchantments");

        DISMOUNT_ENCHANTMENT = BUILDER
                .comment("Dismount: a hit throws the target off whatever it is riding, and shakes off",
                         "anything riding the target as well.")
                .define("dismount", true);

        LEECHING_ENCHANTMENT = BUILDER
                .comment("Leeching: heals whoever swung the weapon for a small share of the damage the",
                         "hit dealt, more of it at higher levels.")
                .define("leeching", true);

        SHATTER_ENCHANTMENT = BUILDER
                .comment("Shatter: a slingshot shot bursts into five more on impact, ricocheting off",
                         "whatever it struck.")
                .define("shatter", true);

        BUILDER.pop();
        BUILDER.pop();

        BUILDER.comment("Smaller rules this mod changes around the world and its mobs.").push("mechanics");

        MESSY_BEDS = BUILDER
                .comment("Beds are left unmade after sleeping, and villagers will not claim one until it",
                         "has been made again with an empty hand.")
                .define("messyBeds", true);

        FEATHER_FALLING_SAVES_CROPS = BUILDER
                .comment("Boots enchanted with Feather Falling stop you trampling farmland.")
                .define("featherFallingSavesCrops", true);

        DOLPHINS_SEEK_CORAL_REEFS = BUILDER
                .comment("Feed a dolphin a tropical fish and it leads you to a coral reef instead of a wreck.")
                .define("dolphinsSeekCoralReefs", true);

        BEES_POLLINATE_MOOBLOOMS = BUILDER
                .comment("Bees visit mooblooms and gather nectar from the flower on their back.")
                .define("beesPollinateMooblooms", true);

        FOXES_USE_POTIONS = BUILDER
                .comment("A fox carrying a potion gets the effects of whatever it is holding.")
                .define("foxesUsePotions", true);

        SNIFFER_FINDS_MOD_SEEDS = BUILDER
                .comment("Sniffers turn up this mod's seeds alongside the torchflower and pitcher pods.")
                .define("snifferFindsModSeeds", true);

        REDSTONE_STRIKER = BUILDER
                .comment("The redstone striker charges dust and components for a few ticks before the",
                         "charge decays away again.")
                .define("redstoneStriker", true);

        LIGHTNING_IN_A_BOTTLE = BUILDER
                .comment("A brewing stand under a lightning rod catches a strike as Lightning In A Bottle,",
                         "which calls lightning down on whatever it is used on.")
                .define("lightningInABottle", true);

        BUILDER.pop();

        BUILDER.comment("Features that borrow content from another mod. Each one needs the mod named in its",
                        "description to be installed; with that mod absent the feature stays off no matter",
                        "what this file says.")
               .push("connected_mods");

        BUILDER.comment("-- Requires Vanilla Backport --").push("vanilla_backport");

        HARNESSES = BUILDER
                .comment("Let the four ancient-dye harnesses be worn by a happy ghast.",
                         "Requires Vanilla Backport, which supplies the ghast and the harness slot.")
                .define("harnesses", true);

        COPPER_ARMOUR_SET = BUILDER
                .comment("Count a full copper armour set towards the copper lightning buff, alongside this",
                         "mod's own copper crown and sickle.",
                         "Requires Vanilla Backport, which supplies the armour.")
                .define("copperArmourSet", true);

        EYEBLOSSOM_MOOBLOOM = BUILDER
                .comment("Add the open-eyeblossom moobloom, the fifteenth flower variant.",
                         "Requires Vanilla Backport, which supplies the flower.")
                .define("eyeblossomMoobloom", true);

        SPEAR_INTERACTIONS = BUILDER
                .comment("Treat backported spears as spears here — a dispenser stabs with one and pillagers",
                         "carry them on patrol.",
                         "Requires Vanilla Backport, which supplies the spears.")
                .define("spearInteractions", true);

        BACKPORTED_SLINGSHOT_AMMO = BUILDER
                .comment("Allow copper nuggets and resin clumps as slingshot ammunition, each with its own",
                         "kind of shot.",
                         "Requires Vanilla Backport, which supplies both items.")
                .define("backportedSlingshotAmmo", true);

        BUILDER.pop();

        BUILDER.comment("-- Requires New Trials --").push("new_trials");

        MACE_INTERACTIONS = BUILDER
                .comment("Turtle headgear soaks a mace smash outright, taking the whole blow in durability.",
                         "A smash is a mace swung while falling more than a block and a half.",
                         "Requires New Trials, which supplies the mace.")
                .define("maceInteractions", true);

        TRIAL_SPAWNERS_IN_FORTRESS_ROOMS = BUILDER
                .comment("Put a trial spawner in the wildfire's Nether fortress room, which is otherwise",
                         "built empty.",
                         "Requires New Trials, which supplies the spawner.")
                .define("trialSpawnersInFortressRooms", true);

        BREEZE_SOUNDS = BUILDER
                .comment("Give the wildfire a breeze's voice instead of the blaze sounds it falls back on.",
                         "Requires New Trials, which supplies the sounds.")
                .define("breezeSounds", true);

        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
