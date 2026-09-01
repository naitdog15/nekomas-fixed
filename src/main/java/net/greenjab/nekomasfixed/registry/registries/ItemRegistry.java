package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.item.*;
import net.greenjab.nekomasfixed.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * DeferredRegister&lt;Item&gt; conversion — by far the biggest file in the registry package (roughly
 * 580 fields / 370 call sites). Every {@code register(...)} overload below defers both the
 * block/entity-type cross-reference AND the id lookup into the supplier Forge invokes during
 * {@code RegisterEvent<Item>} - see BlockRegistry.java's javadoc for the exact trap
 * ("never .get() in a field initializer") this is built to avoid; {@code RegistryObject.getId()} is
 * used instead wherever an id is needed eagerly, since unlike {@code .get()} it never throws before
 * the registry freezes.
 * <p>
 * <b>What did NOT come along.</b> 1.20.1 predates the whole data-component system (1.20.5+), so
 * every {@code .component(...)}/{@code .delayedComponent(...)} call in the 26.2 source had to be
 * either (a) dropped outright where it named this mod's OWN now-deleted {@code ComponentRegistry}
 * (StackData's read-time default already reproduces the "declared default" behaviour those calls
 * expressed - see StackData.java), or (b) dropped as a real, still-unresolved parity gap where it
 * named a VANILLA post-1.20.1 component with no Properties-level substitute here (Equippable,
 * BlocksAttacks, Tool, Weapon, humanoidArmor). Each such drop is marked "DROPPED" at its call site
 * with its own reason.
 */
public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NekomasFixed.NAMESPACE);

    public static final RegistryObject<Item> CLAM = register(BlockRegistry.CLAM, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CLAM_BLUE = register(BlockRegistry.CLAM_BLUE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CLAM_PINK = register(BlockRegistry.CLAM_PINK, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CLAM_PURPLE = register(BlockRegistry.CLAM_PURPLE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> PEARL = register("pearl");
    public static final RegistryObject<Item> PEARL_BLOCK = register(BlockRegistry.PEARL_BLOCK);

    // DROPPED: .component(ComponentRegistry.ANIMAL, AnimalComponent.DEFAULT) - StackData
    // .readAnimal(stack) already returns AnimalComponent.DEFAULT when nothing is stored on the
    // stack's NBT (no ROOT compound / no "animal" key), which is exactly what a Properties-level
    // default declared. Nothing else needs to change: the mixin that actually WRITES a captured
    // animal calls StackData.writeAnimal(...) directly.
    public static final RegistryObject<Item> NAUTILUS_BLOCK = register(BlockRegistry.NAUTILUS_BLOCK, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> ZOMBIE_NAUTILUS_BLOCK = register(BlockRegistry.ZOMBIE_NAUTILUS_BLOCK, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CORAL_NAUTILUS_BLOCK = register(BlockRegistry.CORAL_NAUTILUS_BLOCK, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> GLISTERING_MELON = register(BlockRegistry.GLISTERING_MELON, new Item.Properties());
    public static final RegistryObject<Item> GEYSER = register(BlockRegistry.GEYSER);
    public static final RegistryObject<Item> KILN = register(BlockRegistry.KILN);
    public static final RegistryObject<Item> PYROTECHNICS_TABLE = register(BlockRegistry.PYROTECHNICS_TABLE);
    // ENDERMAN_HEAD's factory reads WALL_ENDERMAN_HEAD.get() - single-lambda form (BlockRegistry.java
    // javadoc). DROPPED: Waypoint.addHideAttribute(...) - 26.x waypoint system, no 1.20.1
    // analogue. Wearing the head is not a Properties setting on this version: AbstractEndermanHeadBlock
    // implements Equipable and reports the HEAD slot, exactly as vanilla's own skull blocks do.
    public static final RegistryObject<Item> ENDERMAN_HEAD = register(BlockRegistry.ENDERMAN_HEAD,
            (block, settings) -> new StandingAndWallBlockItem(block, BlockRegistry.WALL_ENDERMAN_HEAD.get(), settings, Direction.DOWN),
            new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> REDSTONE_STRIKER = register("redstone_striker", RedstoneStrikerItem::new, new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> GLOW_TORCH = register(BlockRegistry.GLOW_TORCH,
            (block, settings) -> new StandingAndWallBlockItem(block, BlockRegistry.GLOW_WALL_TORCH.get(), settings, Direction.DOWN));
    public static final RegistryObject<Item> TARGET_DUMMY = register("target_dummy", TargetDummyItem::new, new Item.Properties().stacksTo(1));
    // TURTLE_CHESTPLATE/LEGGINGS/BOOTS: ArmorMaterials.TURTLE already carries real defense values
    // for all four slots on 1.20.1 (verified against forge-1.20.1-mapped-src), not just the vanilla
    // Turtle Helmet's slot - so this is a straight retarget, not new construction. DROPPED on
    // all three: .humanoidArmor(...) (1.20.5+ Properties convenience; 1.20.1 uses new ArmorItem(...)
    // directly instead).
    public static final RegistryObject<Item> TURTLE_CHESTPLATE = register("turtle_chestplate", settings -> new ArmorItem(ArmorMaterials.TURTLE, ArmorItem.Type.CHESTPLATE, settings), new Item.Properties());
    public static final RegistryObject<Item> TURTLE_LEGGINGS = register("turtle_leggings", settings -> new ArmorItem(ArmorMaterials.TURTLE, ArmorItem.Type.LEGGINGS, settings), new Item.Properties());
    public static final RegistryObject<Item> TURTLE_BOOTS = register("turtle_boots", settings -> new ArmorItem(ArmorMaterials.TURTLE, ArmorItem.Type.BOOTS, settings), new Item.Properties());

    // Sampled from each mob's own texture (background = most frequent opaque colour;
    // highlight = most frequent colour >=60 Euclidean RGB distance from background), so the hex
    // values below fall straight out of the textures. ForgeSpawnEggItem (not vanilla SpawnEggItem, which
    // indexes a static BY_ID map at construction time before modded EntityTypes exist) takes the
    // EntityType RegistryObject directly as its Supplier - no .get() needed at this call site, the
    // one place RegistryObject's supplier-ness pays for itself.
    public static final RegistryObject<Item> MOOBLOOM_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.MOOBLOOM, "moobloom", 0xBF2529, 0x70922D);
    public static final RegistryObject<Item> DRENCHED_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.DRENCHED, "drenched", 0x817F65, 0xBAAC9A);
    public static final RegistryObject<Item> RIME_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.RIME, "rime", 0x527D7E, 0x108C9B);
    public static final RegistryObject<Item> DERELICT_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.DERELICT, "derelict", 0x3E6A2A, 0x005B57);
    public static final RegistryObject<Item> ANCHOR = register("anchor", AnchorItem::new, ModItemSettings.anchor(AnchorItem.DAMAGE, AnchorItem.SPEED));
    public static final RegistryObject<Item> SUSPICIOUS_SPIDER_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.SUSPICIOUS_SPIDER, "suspicious_spider", 0x322B26, 0x605448);
    public static final RegistryObject<Item> WILDFIRE_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.WILDFIRE, "wildfire", 0x5F0201, 0xFFF847);
    // All three smithing templates below (NETHER_HEART, JEWEL_ARMOR_TRIM_SMITHING_TEMPLATE,
    // CROWN_SMITHING_TEMPLATE) lose their rarity and fire/explosion resistance: 1.20.1's
    // SmithingTemplateItem bakes its own `new Item.Properties()` and has no Properties-taking
    // constructor at all, so there is nowhere to hand those settings. They behave exactly like
    // vanilla 1.20.1's own templates. That constructor also takes one Component more than the newer
    // one - an "upgrade description" tooltip line that later versions replaced with the item's own
    // name - and nothing in the source supplies that text, so it stays empty here.
    // The empty-slot icons every template below passes are BLOCK-atlas sprite ids on 1.20.1 (Slot#
    // getNoItemIcon pairs them with InventoryMenu.BLOCK_ATLAS), not GUI-atlas ones - there is no GUI
    // sprite atlas until 1.20.2. The mod's own icons keep their container/slot/* ids and are stitched
    // in by assets/minecraft/atlases/blocks.json; vanilla's ingot icon is item/empty_slot_ingot.
    public static final RegistryObject<Item> NETHER_HEART = register(
            "nether_heart", () -> new SmithingTemplateItem(
                    Component.translatable(
                            Util.makeDescriptionId("item", NekomasFixed.id("smithing_template.ingredients_shield_trident"))
                    ).withStyle(ChatFormatting.BLUE),
                    Component.translatable(
                            Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients"))
                    ).withStyle(ChatFormatting.BLUE),
                    Component.nullToEmpty(""),
                    Component.nullToEmpty(""),
                    Component.nullToEmpty(""),
                    List.of(NekomasFixed.id("container/slot/trident"), NekomasFixed.id("container/slot/shield")),
                    List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")))
    );
    // DROPPED: .attributes(...) and .component(DataComponents.TOOL, ...)/.component(
    // DataComponents.WEAPON, ...) - attribute modifiers and mining/attack tool behaviour on 1.20.1
    // are expressed as overrides on the item class, not Properties builder calls. The trident's
    // modifiers live in WildfireTridentItem#getDefaultAttributeModifiers(EquipmentSlot).
    public static final RegistryObject<Item> WILDFIRE_TRIDENT = register("wildfire_trident", WildfireTridentItem::new, new Item.Properties()
            .rarity(Rarity.RARE).durability(1000).fireResistant());
    // .equippableUnswappable(EquipmentSlot.OFFHAND) needs nothing here: 1.20.1's ShieldItem already
    // implements Equipable and reports the OFFHAND slot, and its use() blocks rather than swapping.
    // DROPPED: .delayedComponent(DataComponents.BLOCKS_ATTACKS, ...)/.component(DataComponents
    // .BREAK_SOUND, ...) - the configurable shield-blocking-curve system is 1.21.2+ with no 1.20.1
    // analogue at all; WildfireShieldItem keeps ShieldItem's own blocking instead.
    public static final RegistryObject<Item> WILDFIRE_SHIELD = register("wildfire_shield", WildfireShieldItem::new,
            new Item.Properties().rarity(Rarity.RARE).durability(336).fireResistant());
    // The trim pattern this template applies is data/nekomasfixed/trim_pattern/jewel.json.
    public static final RegistryObject<Item> JEWEL_ARMOR_TRIM_SMITHING_TEMPLATE = register("jewel_armor_trim_smithing_template",
            () -> SmithingTemplateItem.createArmorTrimTemplate(NekomasFixed.id("jewel")));

    public static final RegistryObject<Item> CROWN_SMITHING_TEMPLATE = register("crown_smithing_template", () ->
            new SmithingTemplateItem(Component.translatable(Util.makeDescriptionId("item", NekomasFixed.id("helmets")))
                    .withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", NekomasFixed.id("nether_heart")))
                    .withStyle(ChatFormatting.BLUE), Component.nullToEmpty(""), Component.nullToEmpty(""), Component.nullToEmpty(""),
                    List.of(NekomasFixed.id("container/slot/helmet")),
                    List.of(NekomasFixed.id("container/slot/nether_heart"))));
    // Crowns: new ArmorItem(...) directly (1.20.1 has no .humanoidArmor(...) Properties method -
    // DROPPED on all five, along with .component(DataComponents.EQUIPPABLE, ...): the equip
    // SOUND now comes from each ArmorMaterial's own getEquipSound(), and the custom render layer is
    // EquipmentLayerRendererMixin (@ModifyExpressionValue on EquipmentAssetManager#get), not a
    // Properties declaration - so nothing here is actually lost, just relocated). COPPER has no
    // vanilla 1.20.1 ArmorMaterials constant (it is a new tier this mod adds) - see
    // ModArmorMaterials.java, whose COPPER numbers are provisional.
    public static final RegistryObject<Item> COPPER_CROWN = register("copper_crown", settings -> new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.HELMET, settings), new Item.Properties());
    public static final RegistryObject<Item> IRON_CROWN = register("iron_crown", settings -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, settings), new Item.Properties());
    public static final RegistryObject<Item> GOLDEN_CROWN = register("golden_crown", settings -> new ArmorItem(ArmorMaterials.GOLD, ArmorItem.Type.HELMET, settings), new Item.Properties());
    public static final RegistryObject<Item> DIAMOND_CROWN = register("diamond_crown", settings -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, settings), new Item.Properties());
    public static final RegistryObject<Item> NETHERITE_CROWN = register("netherite_crown", settings -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, settings), new Item.Properties());

    public static final RegistryObject<Item> SLINGSHOT = register("slingshot", SlingshotItem::new, new Item.Properties().durability(384));
    public static final RegistryObject<Item> WOODEN_SICKLE = register("wooden_sickle", settings -> new SickleItem(Tiers.WOOD, settings), ModItemSettings.sickle(Tiers.WOOD, SickleItem.SPEED));
    public static final RegistryObject<Item> STONE_SICKLE = register("stone_sickle", settings -> new SickleItem(Tiers.STONE, settings), ModItemSettings.sickle(Tiers.STONE, SickleItem.SPEED));
    public static final RegistryObject<Item> COPPER_SICKLE = register("copper_sickle", settings -> new SickleItem(Tiers.STONE, settings), ModItemSettings.sickle(Tiers.STONE, SickleItem.SPEED));
    public static final RegistryObject<Item> IRON_SICKLE = register("iron_sickle", settings -> new SickleItem(Tiers.IRON, settings), ModItemSettings.sickle(Tiers.IRON, SickleItem.SPEED));
    public static final RegistryObject<Item> GOLDEN_SICKLE = register("golden_sickle", settings -> new SickleItem(Tiers.GOLD, settings), ModItemSettings.sickle(Tiers.GOLD, SickleItem.SPEED));
    public static final RegistryObject<Item> DIAMOND_SICKLE = register("diamond_sickle", settings -> new SickleItem(Tiers.DIAMOND, settings), ModItemSettings.sickle(Tiers.DIAMOND, SickleItem.SPEED));
    public static final RegistryObject<Item> NETHERITE_SICKLE = register("netherite_sickle", settings -> new SickleItem(Tiers.NETHERITE, settings), ModItemSettings.sickle(Tiers.NETHERITE, SickleItem.SPEED).fireResistant());

    public static final RegistryObject<Item> SWEETBERRY_CAKE = register(BlockRegistry.SWEETBERRY_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> PAN_CAKE = register(BlockRegistry.PAN_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> GLOWBERRY_CAKE = register(BlockRegistry.GLOWBERRY_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> APPLE_CAKE = register(BlockRegistry.APPLE_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> VANILLA_CAKE = register(BlockRegistry.VANILLA_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> COOKIE_CAKE = register(BlockRegistry.COOKIE_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CHOCOLATE_CAKE = register(BlockRegistry.CHOCOLATE_CAKE, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BEETROOT_CAKE = register(BlockRegistry.BEETROOT_CAKE, new Item.Properties().stacksTo(1));

    public static final RegistryObject<Item> BAOBAB_LOG = register(BlockRegistry.BAOBAB_LOG);
    public static final RegistryObject<Item> BAOBAB_WOOD = register(BlockRegistry.BAOBAB_WOOD);
    public static final RegistryObject<Item> STRIPPED_BAOBAB_LOG = register(BlockRegistry.STRIPPED_BAOBAB_LOG);
    public static final RegistryObject<Item> STRIPPED_BAOBAB_WOOD = register(BlockRegistry.STRIPPED_BAOBAB_WOOD);
    public static final RegistryObject<Item> BAOBAB_PLANKS = register(BlockRegistry.BAOBAB_PLANKS);
    public static final RegistryObject<Item> BAOBAB_STAIRS = register(BlockRegistry.BAOBAB_STAIRS);
    public static final RegistryObject<Item> BAOBAB_SLAB = register(BlockRegistry.BAOBAB_SLAB);
    public static final RegistryObject<Item> BAOBAB_FENCE = register(BlockRegistry.BAOBAB_FENCE);
    public static final RegistryObject<Item> BAOBAB_FENCE_GATE = register(BlockRegistry.BAOBAB_FENCE_GATE);
    public static final RegistryObject<Item> BAOBAB_DOOR = register(BlockRegistry.BAOBAB_DOOR, DoubleHighBlockItem::new);
    public static final RegistryObject<Item> BAOBAB_TRAPDOOR = register(BlockRegistry.BAOBAB_TRAPDOOR);
    public static final RegistryObject<Item> BAOBAB_PRESSURE_PLATE = register(BlockRegistry.BAOBAB_PRESSURE_PLATE);
    public static final RegistryObject<Item> BAOBAB_BUTTON = register(BlockRegistry.BAOBAB_BUTTON);
    public static final RegistryObject<Item> BAOBAB_LEAVES = register(BlockRegistry.BAOBAB_LEAVES);
    public static final RegistryObject<Item> BAOBAB_SAPLING = register(BlockRegistry.BAOBAB_SAPLING);
    public static final RegistryObject<Item> BAOBAB_SEEDS = register("baobab_seeds", BaobabSeedsItem::new, new Item.Properties().stacksTo(64));
    public static final FoodProperties BAOBAB_FRUIT_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
    public static final RegistryObject<Item> BAOBAB_FRUIT = register("baobab_fruit", new Item.Properties().food(BAOBAB_FRUIT_FOOD));
    public static final RegistryObject<Item> ROPE = register(BlockRegistry.ROPE, RopeItem::new);
    public static final RegistryObject<Item> BAOBAB_SHELF = register(BlockRegistry.BAOBAB_SHELF);
    // BAOBAB_SIGN/BAOBAB_HANGING_SIGN reference their wall variants - single-lambda form.
    public static final RegistryObject<Item> BAOBAB_SIGN = register(BlockRegistry.BAOBAB_SIGN,
            (block, settings) -> new SignItem(settings, block, BlockRegistry.BAOBAB_WALL_SIGN.get()), new Item.Properties().stacksTo(16));
    public static final RegistryObject<Item> BAOBAB_HANGING_SIGN = register(BlockRegistry.BAOBAB_HANGING_SIGN,
            (block, settings) -> new HangingSignItem(block, BlockRegistry.BAOBAB_WALL_HANGING_SIGN.get(), settings), new Item.Properties().stacksTo(16));
    // Vanilla BoatItem can only place EntityType.BOAT/CHEST_BOAT with a Boat.Type variant stamped on
    // top, which cannot express any of this mod's boat entity types - ModBoatItem is the same
    // placement behaviour driven by a supplied entity type. The RegistryObject goes in as the
    // supplier itself, so nothing is dereferenced during registration.
    public static final RegistryObject<Item> BAOBAB_BOAT = register("baobab_boat", settings ->
            new ModBoatItem(EntityTypeRegistry.BAOBAB_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BAOBAB_CHEST_BOAT = register("baobab_chest_boat", settings ->
            new ModBoatItem(EntityTypeRegistry.BAOBAB_CHEST_BOAT, settings), new Item.Properties().stacksTo(1));

    public static final RegistryObject<Item> TERMITE_SPAWN_EGG = registerSpawnEgg(EntityTypeRegistry.TERMITE, "termite", 0xBB7F3D, 0xE1C080);
    public static final RegistryObject<Item> TERMITE_BLOCK = register(BlockRegistry.TERMITE_BLOCK);
    public static final RegistryObject<Item> TERMITE_HIVE = register(BlockRegistry.TERMITE_HIVE);
    public static final RegistryObject<Item> HOLLOW_OAK_LOG = register(BlockRegistry.HOLLOW_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_SPRUCE_LOG = register(BlockRegistry.HOLLOW_SPRUCE_LOG);
    public static final RegistryObject<Item> HOLLOW_BIRCH_LOG = register(BlockRegistry.HOLLOW_BIRCH_LOG);
    public static final RegistryObject<Item> HOLLOW_JUNGLE_LOG = register(BlockRegistry.HOLLOW_JUNGLE_LOG);
    public static final RegistryObject<Item> HOLLOW_ACACIA_LOG = register(BlockRegistry.HOLLOW_ACACIA_LOG);
    public static final RegistryObject<Item> HOLLOW_DARK_OAK_LOG = register(BlockRegistry.HOLLOW_DARK_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_MANGROVE_LOG = register(BlockRegistry.HOLLOW_MANGROVE_LOG);
    public static final RegistryObject<Item> HOLLOW_CHERRY_LOG = register(BlockRegistry.HOLLOW_CHERRY_LOG);
    public static final RegistryObject<Item> HOLLOW_PALE_OAK_LOG = register(BlockRegistry.HOLLOW_PALE_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_BAMBOO_BLOCK = register(BlockRegistry.HOLLOW_BAMBOO_BLOCK);
    public static final RegistryObject<Item> HOLLOW_WARPED_STEM = register(BlockRegistry.HOLLOW_WARPED_STEM);
    public static final RegistryObject<Item> HOLLOW_CRIMSON_STEM = register(BlockRegistry.HOLLOW_CRIMSON_STEM);
    public static final RegistryObject<Item> HOLLOW_BAOBAB_LOG = register(BlockRegistry.HOLLOW_BAOBAB_LOG);

    public static final RegistryObject<Item> BOAT_UPGRADE_TEMPLATE = register("boat_upgrade_template", () ->
            new SmithingTemplateItem(Component.translatable(Util.makeDescriptionId("item", NekomasFixed.id("boat")))
                    .withStyle(ChatFormatting.BLUE), Component.translatable(Util.makeDescriptionId("item", NekomasFixed.id("planks")))
                    .withStyle(ChatFormatting.BLUE), Component.nullToEmpty(""), Component.nullToEmpty(""), Component.nullToEmpty(""),
                    List.of(NekomasFixed.id("container/slot/boat")),
                    List.of(NekomasFixed.id("container/slot/planks"))));
    public static final RegistryObject<Item> BIG_OAK_BOAT = register("big_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_SPRUCE_BOAT = register("big_spruce_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_SPRUCE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_BIRCH_BOAT = register("big_birch_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_BIRCH_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_JUNGLE_BOAT = register("big_jungle_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_JUNGLE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_ACACIA_BOAT = register("big_acacia_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_ACACIA_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_DARK_OAK_BOAT = register("big_dark_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_DARK_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_MANGROVE_BOAT = register("big_mangrove_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_MANGROVE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_CHERRY_BOAT = register("big_cherry_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_CHERRY_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_PALE_OAK_BOAT = register("big_pale_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_PALE_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_BAMBOO_BOAT = register("big_bamboo_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_BAMBOO_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> BIG_BAOBAB_BOAT = register("big_baobab_boat", settings -> new ModBoatItem(EntityTypeRegistry.BIG_BAOBAB_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_OAK_BOAT = register("huge_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_SPRUCE_BOAT = register("huge_spruce_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_SPRUCE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_BIRCH_BOAT = register("huge_birch_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_BIRCH_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_JUNGLE_BOAT = register("huge_jungle_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_JUNGLE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_ACACIA_BOAT = register("huge_acacia_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_ACACIA_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_DARK_OAK_BOAT = register("huge_dark_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_DARK_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_MANGROVE_BOAT = register("huge_mangrove_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_MANGROVE_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_CHERRY_BOAT = register("huge_cherry_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_CHERRY_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_PALE_OAK_BOAT = register("huge_pale_oak_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_PALE_OAK_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_BAMBOO_BOAT = register("huge_bamboo_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_BAMBOO_BOAT, settings), new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> HUGE_BAOBAB_BOAT = register("huge_baobab_boat", settings -> new ModBoatItem(EntityTypeRegistry.HUGE_BAOBAB_BOAT, settings), new Item.Properties().stacksTo(1));

    public static final RegistryObject<Item> SPECIAL_STEW = register("special_stew", SpecialSoupItem::new, new Item.Properties().stacksTo(1).food((new FoodProperties.Builder()).nutrition(0).saturationMod(0.0F).build()).craftRemainder(Items.BOWL));
    // Potion: 1.20.1 registers a plain Potion instance (registerForHolder is post-1.20.5) -
    // a new registry entry with no 26.2 counterpart. DeferredRegister<Potion> on ForgeRegistries.POTIONS.
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, NekomasFixed.NAMESPACE);
    public static final RegistryObject<Potion> LIGHTNING = POTIONS.register("lightning",
            () -> new Potion("lightning", new MobEffectInstance(EffectRegistry.LIGHTNING.get(), 1)));


    public static final RegistryObject<Item> AMBER_WOOL = register(BlockRegistry.AMBER_WOOL);
    public static final RegistryObject<Item> AQUA_WOOL = register(BlockRegistry.AQUA_WOOL);
    public static final RegistryObject<Item> INDIGO_WOOL = register(BlockRegistry.INDIGO_WOOL);
    public static final RegistryObject<Item> MAROON_WOOL = register(BlockRegistry.MAROON_WOOL);
    public static final RegistryObject<Item> AMBER_CARPET = register(BlockRegistry.AMBER_CARPET);
    public static final RegistryObject<Item> AQUA_CARPET = register(BlockRegistry.AQUA_CARPET);
    public static final RegistryObject<Item> INDIGO_CARPET = register(BlockRegistry.INDIGO_CARPET);
    public static final RegistryObject<Item> MAROON_CARPET = register(BlockRegistry.MAROON_CARPET);

    public static final RegistryObject<Item> AMBER_TERRACOTTA = register(BlockRegistry.AMBER_TERRACOTTA);
    public static final RegistryObject<Item> AQUA_TERRACOTTA = register(BlockRegistry.AQUA_TERRACOTTA);
    public static final RegistryObject<Item> INDIGO_TERRACOTTA = register(BlockRegistry.INDIGO_TERRACOTTA);
    public static final RegistryObject<Item> MAROON_TERRACOTTA = register(BlockRegistry.MAROON_TERRACOTTA);

    public static final RegistryObject<Item> AMBER_CONCRETE = register(BlockRegistry.AMBER_CONCRETE);
    public static final RegistryObject<Item> AQUA_CONCRETE = register(BlockRegistry.AQUA_CONCRETE);
    public static final RegistryObject<Item> INDIGO_CONCRETE = register(BlockRegistry.INDIGO_CONCRETE);
    public static final RegistryObject<Item> MAROON_CONCRETE = register(BlockRegistry.MAROON_CONCRETE);
    public static final RegistryObject<Item> AMBER_CONCRETE_POWDER = register(BlockRegistry.AMBER_CONCRETE_POWDER);
    public static final RegistryObject<Item> AQUA_CONCRETE_POWDER = register(BlockRegistry.AQUA_CONCRETE_POWDER);
    public static final RegistryObject<Item> INDIGO_CONCRETE_POWDER = register(BlockRegistry.INDIGO_CONCRETE_POWDER);
    public static final RegistryObject<Item> MAROON_CONCRETE_POWDER = register(BlockRegistry.MAROON_CONCRETE_POWDER);

    public static final RegistryObject<Item> AMBER_GLAZED_TERRACOTTA = register(BlockRegistry.AMBER_GLAZED_TERRACOTTA);
    public static final RegistryObject<Item> AQUA_GLAZED_TERRACOTTA = register(BlockRegistry.AQUA_GLAZED_TERRACOTTA);
    public static final RegistryObject<Item> INDIGO_GLAZED_TERRACOTTA = register(BlockRegistry.INDIGO_GLAZED_TERRACOTTA);
    public static final RegistryObject<Item> MAROON_GLAZED_TERRACOTTA = register(BlockRegistry.MAROON_GLAZED_TERRACOTTA);

    public static final RegistryObject<Item> AMBER_STAINED_GLASS = register(BlockRegistry.AMBER_STAINED_GLASS);
    public static final RegistryObject<Item> AQUA_STAINED_GLASS = register(BlockRegistry.AQUA_STAINED_GLASS);
    public static final RegistryObject<Item> INDIGO_STAINED_GLASS = register(BlockRegistry.INDIGO_STAINED_GLASS);
    public static final RegistryObject<Item> MAROON_STAINED_GLASS = register(BlockRegistry.MAROON_STAINED_GLASS);
    public static final RegistryObject<Item> AMBER_STAINED_GLASS_PANE = register(BlockRegistry.AMBER_STAINED_GLASS_PANE);
    public static final RegistryObject<Item> AQUA_STAINED_GLASSS_PANE = register(BlockRegistry.AQUA_STAINED_GLASS_PANE);
    public static final RegistryObject<Item> INDIGO_STAINED_GLASSS_PANE = register(BlockRegistry.INDIGO_STAINED_GLASS_PANE);
    public static final RegistryObject<Item> MAROON_STAINED_GLASSS_PANE = register(BlockRegistry.MAROON_STAINED_GLASS_PANE);

    // DROPPED on all four shulker boxes: .component(DataComponents.CONTAINER,
    // ItemContainerContents.EMPTY) - same "default = absence of NBT" reasoning as NAUTILUS_BLOCK
    // above; the block entity's own inventory (opened as a container in-world) is unaffected.
    public static final RegistryObject<Item> AMBER_SHULKER_BOX = register(BlockRegistry.AMBER_SHULKER_BOX, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> AQUA_SHULKER_BOX = register(BlockRegistry.AQUA_SHULKER_BOX, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> INDIGO_SHULKER_BOX = register(BlockRegistry.INDIGO_SHULKER_BOX, new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> MAROON_SHULKER_BOX = register(BlockRegistry.MAROON_SHULKER_BOX, new Item.Properties().stacksTo(1));

    public static final RegistryObject<Item> AMBER_BED = register(BlockRegistry.AMBER_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> AQUA_BED = register(BlockRegistry.AQUA_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> INDIGO_BED = register(BlockRegistry.INDIGO_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> MAROON_BED = register(BlockRegistry.MAROON_BED, BedItem::new, (new Item.Properties()).stacksTo(1));

    public static final RegistryObject<Item> AMBER_CANDLE = register(BlockRegistry.AMBER_CANDLE);
    public static final RegistryObject<Item> AQUA_CANDLE = register(BlockRegistry.AQUA_CANDLE);
    public static final RegistryObject<Item> INDIGO_CANDLE = register(BlockRegistry.INDIGO_CANDLE);
    public static final RegistryObject<Item> MAROON_CANDLE = register(BlockRegistry.MAROON_CANDLE);

    // BUNDLE_CONTENTS component dropped (stored via ItemStack NBT directly on 1.20.1, no
    // Properties declaration needed). NOTE: on 1.20.1 bundles sit behind FeatureFlags.BUNDLE
    // (experimental); BundleItem is used as-is (no .requiredFeatures(...) added) so these register
    // exactly like every other mod item. Worth confirming in-game that they behave.
    public static final RegistryObject<Item> AMBER_BUNDLE = register("amber_bundle", BundleItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> AQUA_BUNDLE = register("aqua_bundle", BundleItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> INDIGO_BUNDLE = register("indigo_bundle", BundleItem::new, (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> MAROON_BUNDLE = register("maroon_bundle", BundleItem::new, (new Item.Properties()).stacksTo(1));

    // DROPPED on all four harnesses: .component(DataComponents.EQUIPPABLE,
    // HarnessHelper.ofHarness(...)) - Happy Ghast (the only entity a harness equips onto) does not
    // exist on 1.20.1 at all (26.x-only mob), so the items are still registered and obtainable but
    // there is nothing on this version to equip them onto. See HarnessHelper.java.
    public static final RegistryObject<Item> AMBER_HARNESS = register("amber_harness", (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> AQUA_HARNESS = register("aqua_harness", (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> INDIGO_HARNESS = register("indigo_harness", (new Item.Properties()).stacksTo(1));
    public static final RegistryObject<Item> MAROON_HARNESS = register("maroon_harness", (new Item.Properties()).stacksTo(1));

    // The DyeColor each one borrows is the same one its wool/carpet family uses in BlockRegistry -
    // see ModDyeItems for why a real DyeColor is unavoidable here.
    public static final RegistryObject<Item> AMBER_DYE = registerDye("amber_dye", DyeColor.YELLOW);
    public static final RegistryObject<Item> AQUA_DYE = registerDye("aqua_dye", DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Item> INDIGO_DYE = registerDye("indigo_dye", DyeColor.MAGENTA);
    public static final RegistryObject<Item> MAROON_DYE = registerDye("maroon_dye", DyeColor.RED);


    public static final RegistryObject<Item> WHITE_DYED_BRUSH = register("white_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.WHITE, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> LIGHT_GRAY_DYED_BRUSH = register("light_gray_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.LIGHT_GRAY, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> GRAY_DYED_BRUSH = register("gray_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.GRAY, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> BLACK_DYED_BRUSH = register("black_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.BLACK, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> BROWN_DYED_BRUSH = register("brown_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.BROWN, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> RED_DYED_BRUSH = register("red_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.RED, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> ORANGE_DYED_BRUSH = register("orange_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.ORANGE, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> YELLOW_DYED_BRUSH = register("yellow_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.YELLOW, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> LIME_DYED_BRUSH = register("lime_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.LIME, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> GREEN_DYED_BRUSH = register("green_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.GREEN, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> CYAN_DYED_BRUSH = register("cyan_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.CYAN, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> LIGHT_BLUE_DYED_BRUSH = register("light_blue_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.LIGHT_BLUE, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> BLUE_DYED_BRUSH = register("blue_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.BLUE, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> PURPLE_DYED_BRUSH = register("purple_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.PURPLE, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> MAGENTA_DYED_BRUSH = register("magenta_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.MAGENTA, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> PINK_DYED_BRUSH = register("pink_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.PINK, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> AMBER_DYED_BRUSH = register("amber_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.AMBER, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> AQUA_DYED_BRUSH = register("aqua_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.AQUA, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> INDIGO_DYED_BRUSH = register("indigo_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.INDIGO, settings), new Item.Properties().stacksTo(1).durability(64));
    public static final RegistryObject<Item> MAROON_DYED_BRUSH = register("maroon_dyed_brush", (settings) -> new DyedBrushItem(AllDyes.MAROON, settings), new Item.Properties().stacksTo(1).durability(64));

    public static final RegistryObject<Item> WHITE_BRICKS = register(BlockRegistry.WHITE_BRICKS);
    public static final RegistryObject<Item> LIGHT_GRAY_BRICKS = register(BlockRegistry.LIGHT_GRAY_BRICKS);
    public static final RegistryObject<Item> GRAY_BRICKS = register(BlockRegistry.GRAY_BRICKS);
    public static final RegistryObject<Item> BLACK_BRICKS = register(BlockRegistry.BLACK_BRICKS);
    public static final RegistryObject<Item> BROWN_BRICKS = register(BlockRegistry.BROWN_BRICKS);
    public static final RegistryObject<Item> RED_BRICKS = register(BlockRegistry.RED_BRICKS);
    public static final RegistryObject<Item> ORANGE_BRICKS = register(BlockRegistry.ORANGE_BRICKS);
    public static final RegistryObject<Item> YELLOW_BRICKS = register(BlockRegistry.YELLOW_BRICKS);
    public static final RegistryObject<Item> LIME_BRICKS = register(BlockRegistry.LIME_BRICKS);
    public static final RegistryObject<Item> GREEN_BRICKS = register(BlockRegistry.GREEN_BRICKS);
    public static final RegistryObject<Item> CYAN_BRICKS = register(BlockRegistry.CYAN_BRICKS);
    public static final RegistryObject<Item> LIGHT_BLUE_BRICKS = register(BlockRegistry.LIGHT_BLUE_BRICKS);
    public static final RegistryObject<Item> BLUE_BRICKS = register(BlockRegistry.BLUE_BRICKS);
    public static final RegistryObject<Item> PURPLE_BRICKS = register(BlockRegistry.PURPLE_BRICKS);
    public static final RegistryObject<Item> MAGENTA_BRICKS = register(BlockRegistry.MAGENTA_BRICKS);
    public static final RegistryObject<Item> PINK_BRICKS = register(BlockRegistry.PINK_BRICKS);
    public static final RegistryObject<Item> AMBER_BRICKS = register(BlockRegistry.AMBER_BRICKS);
    public static final RegistryObject<Item> AQUA_BRICKS = register(BlockRegistry.AQUA_BRICKS);
    public static final RegistryObject<Item> INDIGO_BRICKS = register(BlockRegistry.INDIGO_BRICKS);
    public static final RegistryObject<Item> MAROON_BRICKS = register(BlockRegistry.MAROON_BRICKS);

    public static final RegistryObject<Item> WHITE_BRICK_SLAB = register(BlockRegistry.WHITE_BRICK_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_BRICK_SLAB = register(BlockRegistry.LIGHT_GRAY_BRICK_SLAB);
    public static final RegistryObject<Item> GRAY_BRICK_SLAB = register(BlockRegistry.GRAY_BRICK_SLAB);
    public static final RegistryObject<Item> BLACK_BRICK_SLAB = register(BlockRegistry.BLACK_BRICK_SLAB);
    public static final RegistryObject<Item> BROWN_BRICK_SLAB = register(BlockRegistry.BROWN_BRICK_SLAB);
    public static final RegistryObject<Item> RED_BRICK_SLAB = register(BlockRegistry.RED_BRICK_SLAB);
    public static final RegistryObject<Item> ORANGE_BRICK_SLAB = register(BlockRegistry.ORANGE_BRICK_SLAB);
    public static final RegistryObject<Item> YELLOW_BRICK_SLAB = register(BlockRegistry.YELLOW_BRICK_SLAB);
    public static final RegistryObject<Item> LIME_BRICK_SLAB = register(BlockRegistry.LIME_BRICK_SLAB);
    public static final RegistryObject<Item> GREEN_BRICK_SLAB = register(BlockRegistry.GREEN_BRICK_SLAB);
    public static final RegistryObject<Item> CYAN_BRICK_SLAB = register(BlockRegistry.CYAN_BRICK_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_BRICK_SLAB = register(BlockRegistry.LIGHT_BLUE_BRICK_SLAB);
    public static final RegistryObject<Item> BLUE_BRICK_SLAB = register(BlockRegistry.BLUE_BRICK_SLAB);
    public static final RegistryObject<Item> PURPLE_BRICK_SLAB = register(BlockRegistry.PURPLE_BRICK_SLAB);
    public static final RegistryObject<Item> MAGENTA_BRICK_SLAB = register(BlockRegistry.MAGENTA_BRICK_SLAB);
    public static final RegistryObject<Item> PINK_BRICK_SLAB = register(BlockRegistry.PINK_BRICK_SLAB);
    public static final RegistryObject<Item> AMBER_BRICK_SLAB = register(BlockRegistry.AMBER_BRICK_SLAB);
    public static final RegistryObject<Item> AQUA_BRICK_SLAB = register(BlockRegistry.AQUA_BRICK_SLAB);
    public static final RegistryObject<Item> INDIGO_BRICK_SLAB = register(BlockRegistry.INDIGO_BRICK_SLAB);
    public static final RegistryObject<Item> MAROON_BRICK_SLAB = register(BlockRegistry.MAROON_BRICK_SLAB);

    public static final RegistryObject<Item> WHITE_BRICK_STAIRS = register(BlockRegistry.WHITE_BRICK_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_BRICK_STAIRS = register(BlockRegistry.LIGHT_GRAY_BRICK_STAIRS);
    public static final RegistryObject<Item> GRAY_BRICK_STAIRS = register(BlockRegistry.GRAY_BRICK_STAIRS);
    public static final RegistryObject<Item> BLACK_BRICK_STAIRS = register(BlockRegistry.BLACK_BRICK_STAIRS);
    public static final RegistryObject<Item> BROWN_BRICK_STAIRS = register(BlockRegistry.BROWN_BRICK_STAIRS);
    public static final RegistryObject<Item> RED_BRICK_STAIRS = register(BlockRegistry.RED_BRICK_STAIRS);
    public static final RegistryObject<Item> ORANGE_BRICK_STAIRS = register(BlockRegistry.ORANGE_BRICK_STAIRS);
    public static final RegistryObject<Item> YELLOW_BRICK_STAIRS = register(BlockRegistry.YELLOW_BRICK_STAIRS);
    public static final RegistryObject<Item> LIME_BRICK_STAIRS = register(BlockRegistry.LIME_BRICK_STAIRS);
    public static final RegistryObject<Item> GREEN_BRICK_STAIRS = register(BlockRegistry.GREEN_BRICK_STAIRS);
    public static final RegistryObject<Item> CYAN_BRICK_STAIRS = register(BlockRegistry.CYAN_BRICK_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_BRICK_STAIRS = register(BlockRegistry.LIGHT_BLUE_BRICK_STAIRS);
    public static final RegistryObject<Item> BLUE_BRICK_STAIRS = register(BlockRegistry.BLUE_BRICK_STAIRS);
    public static final RegistryObject<Item> PURPLE_BRICK_STAIRS = register(BlockRegistry.PURPLE_BRICK_STAIRS);
    public static final RegistryObject<Item> MAGENTA_BRICK_STAIRS = register(BlockRegistry.MAGENTA_BRICK_STAIRS);
    public static final RegistryObject<Item> PINK_BRICK_STAIRS = register(BlockRegistry.PINK_BRICK_STAIRS);
    public static final RegistryObject<Item> AMBER_BRICK_STAIRS = register(BlockRegistry.AMBER_BRICK_STAIRS);
    public static final RegistryObject<Item> AQUA_BRICK_STAIRS = register(BlockRegistry.AQUA_BRICK_STAIRS);
    public static final RegistryObject<Item> INDIGO_BRICK_STAIRS = register(BlockRegistry.INDIGO_BRICK_STAIRS);
    public static final RegistryObject<Item> MAROON_BRICK_STAIRS = register(BlockRegistry.MAROON_BRICK_STAIRS);

    public static final RegistryObject<Item> WHITE_BRICK_WALL = register(BlockRegistry.WHITE_BRICK_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_BRICK_WALL = register(BlockRegistry.LIGHT_GRAY_BRICK_WALL);
    public static final RegistryObject<Item> GRAY_BRICK_WALL = register(BlockRegistry.GRAY_BRICK_WALL);
    public static final RegistryObject<Item> BLACK_BRICK_WALL = register(BlockRegistry.BLACK_BRICK_WALL);
    public static final RegistryObject<Item> BROWN_BRICK_WALL = register(BlockRegistry.BROWN_BRICK_WALL);
    public static final RegistryObject<Item> RED_BRICK_WALL = register(BlockRegistry.RED_BRICK_WALL);
    public static final RegistryObject<Item> ORANGE_BRICK_WALL = register(BlockRegistry.ORANGE_BRICK_WALL);
    public static final RegistryObject<Item> YELLOW_BRICK_WALL = register(BlockRegistry.YELLOW_BRICK_WALL);
    public static final RegistryObject<Item> LIME_BRICK_WALL = register(BlockRegistry.LIME_BRICK_WALL);
    public static final RegistryObject<Item> GREEN_BRICK_WALL = register(BlockRegistry.GREEN_BRICK_WALL);
    public static final RegistryObject<Item> CYAN_BRICK_WALL = register(BlockRegistry.CYAN_BRICK_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_BRICK_WALL = register(BlockRegistry.LIGHT_BLUE_BRICK_WALL);
    public static final RegistryObject<Item> BLUE_BRICK_WALL = register(BlockRegistry.BLUE_BRICK_WALL);
    public static final RegistryObject<Item> PURPLE_BRICK_WALL = register(BlockRegistry.PURPLE_BRICK_WALL);
    public static final RegistryObject<Item> MAGENTA_BRICK_WALL = register(BlockRegistry.MAGENTA_BRICK_WALL);
    public static final RegistryObject<Item> PINK_BRICK_WALL = register(BlockRegistry.PINK_BRICK_WALL);
    public static final RegistryObject<Item> AMBER_BRICK_WALL = register(BlockRegistry.AMBER_BRICK_WALL);
    public static final RegistryObject<Item> AQUA_BRICK_WALL = register(BlockRegistry.AQUA_BRICK_WALL);
    public static final RegistryObject<Item> INDIGO_BRICK_WALL = register(BlockRegistry.INDIGO_BRICK_WALL);
    public static final RegistryObject<Item> MAROON_BRICK_WALL = register(BlockRegistry.MAROON_BRICK_WALL);


    public static final RegistryObject<Item> CLEAR_FROGLIGHT = register(BlockRegistry.CLEAR_FROGLIGHT);
    public static final RegistryObject<Item> CLOUDY_FROGLIGHT = register(BlockRegistry.CLOUDY_FROGLIGHT);
    public static final RegistryObject<Item> CASCADING_FROGLIGHT = register(BlockRegistry.CASCADING_FROGLIGHT);
    public static final RegistryObject<Item> CLOUDBURST_FROGLIGHT = register(BlockRegistry.CLOUDBURST_FROGLIGHT);
    public static final RegistryObject<Item> CHAMOISEE_FROGLIGHT = register(BlockRegistry.CHAMOISEE_FROGLIGHT);
    public static final RegistryObject<Item> SANGUINE_FROGLIGHT = register(BlockRegistry.SANGUINE_FROGLIGHT);
    public static final RegistryObject<Item> VERMILION_FROGLIGHT = register(BlockRegistry.VERMILION_FROGLIGHT);
    public static final RegistryObject<Item> MANDARIN_FROGLIGHT = register(BlockRegistry.MANDARIN_FROGLIGHT);
    public static final RegistryObject<Item> LEMON_FROGLIGHT = register(BlockRegistry.LEMON_FROGLIGHT);
    public static final RegistryObject<Item> KIWI_FROGLIGHT = register(BlockRegistry.KIWI_FROGLIGHT);
    public static final RegistryObject<Item> SEAFOAM_FROGLIGHT = register(BlockRegistry.SEAFOAM_FROGLIGHT);
    public static final RegistryObject<Item> TEAL_FROGLIGHT = register(BlockRegistry.TEAL_FROGLIGHT);
    public static final RegistryObject<Item> CERULEAN_FROGLIGHT = register(BlockRegistry.CERULEAN_FROGLIGHT);
    public static final RegistryObject<Item> NAVY_FROGLIGHT = register(BlockRegistry.NAVY_FROGLIGHT);
    public static final RegistryObject<Item> LAVENDER_FROGLIGHT = register(BlockRegistry.LAVENDER_FROGLIGHT);
    public static final RegistryObject<Item> THULIAN_FROGLIGHT = register(BlockRegistry.THULIAN_FROGLIGHT);
    public static final RegistryObject<Item> SAKURA_FROGLIGHT = register(BlockRegistry.SAKURA_FROGLIGHT);

    public static final RegistryObject<Item> WHITE_SPOTTED_WOOL = register(BlockRegistry.WHITE_SPOTTED_WOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_SPOTTED_WOOL = register(BlockRegistry.LIGHT_GRAY_SPOTTED_WOOL);
    public static final RegistryObject<Item> GRAY_SPOTTED_WOOL = register(BlockRegistry.GRAY_SPOTTED_WOOL);
    public static final RegistryObject<Item> BLACK_SPOTTED_WOOL = register(BlockRegistry.BLACK_SPOTTED_WOOL);
    public static final RegistryObject<Item> BROWN_SPOTTED_WOOL = register(BlockRegistry.BROWN_SPOTTED_WOOL);
    public static final RegistryObject<Item> RED_SPOTTED_WOOL = register(BlockRegistry.RED_SPOTTED_WOOL);
    public static final RegistryObject<Item> ORANGE_SPOTTED_WOOL = register(BlockRegistry.ORANGE_SPOTTED_WOOL);
    public static final RegistryObject<Item> YELLOW_SPOTTED_WOOL = register(BlockRegistry.YELLOW_SPOTTED_WOOL);
    public static final RegistryObject<Item> LIME_SPOTTED_WOOL = register(BlockRegistry.LIME_SPOTTED_WOOL);
    public static final RegistryObject<Item> GREEN_SPOTTED_WOOL = register(BlockRegistry.GREEN_SPOTTED_WOOL);
    public static final RegistryObject<Item> CYAN_SPOTTED_WOOL = register(BlockRegistry.CYAN_SPOTTED_WOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_SPOTTED_WOOL = register(BlockRegistry.LIGHT_BLUE_SPOTTED_WOOL);
    public static final RegistryObject<Item> BLUE_SPOTTED_WOOL = register(BlockRegistry.BLUE_SPOTTED_WOOL);
    public static final RegistryObject<Item> PURPLE_SPOTTED_WOOL = register(BlockRegistry.PURPLE_SPOTTED_WOOL);
    public static final RegistryObject<Item> MAGENTA_SPOTTED_WOOL = register(BlockRegistry.MAGENTA_SPOTTED_WOOL);
    public static final RegistryObject<Item> PINK_SPOTTED_WOOL = register(BlockRegistry.PINK_SPOTTED_WOOL);
    public static final RegistryObject<Item> AMBER_SPOTTED_WOOL = register(BlockRegistry.AMBER_SPOTTED_WOOL);
    public static final RegistryObject<Item> AQUA_SPOTTED_WOOL = register(BlockRegistry.AQUA_SPOTTED_WOOL);
    public static final RegistryObject<Item> INDIGO_SPOTTED_WOOL = register(BlockRegistry.INDIGO_SPOTTED_WOOL);
    public static final RegistryObject<Item> MAROON_SPOTTED_WOOL = register(BlockRegistry.MAROON_SPOTTED_WOOL);

    public static final RegistryObject<Item> WHITE_SPOTTED_CARPET = register(BlockRegistry.WHITE_SPOTTED_CARPET);
    public static final RegistryObject<Item> LIGHT_GRAY_SPOTTED_CARPET = register(BlockRegistry.LIGHT_GRAY_SPOTTED_CARPET);
    public static final RegistryObject<Item> GRAY_SPOTTED_CARPET = register(BlockRegistry.GRAY_SPOTTED_CARPET);
    public static final RegistryObject<Item> BLACK_SPOTTED_CARPET = register(BlockRegistry.BLACK_SPOTTED_CARPET);
    public static final RegistryObject<Item> BROWN_SPOTTED_CARPET = register(BlockRegistry.BROWN_SPOTTED_CARPET);
    public static final RegistryObject<Item> RED_SPOTTED_CARPET = register(BlockRegistry.RED_SPOTTED_CARPET);
    public static final RegistryObject<Item> ORANGE_SPOTTED_CARPET = register(BlockRegistry.ORANGE_SPOTTED_CARPET);
    public static final RegistryObject<Item> YELLOW_SPOTTED_CARPET = register(BlockRegistry.YELLOW_SPOTTED_CARPET);
    public static final RegistryObject<Item> LIME_SPOTTED_CARPET = register(BlockRegistry.LIME_SPOTTED_CARPET);
    public static final RegistryObject<Item> GREEN_SPOTTED_CARPET = register(BlockRegistry.GREEN_SPOTTED_CARPET);
    public static final RegistryObject<Item> CYAN_SPOTTED_CARPET = register(BlockRegistry.CYAN_SPOTTED_CARPET);
    public static final RegistryObject<Item> LIGHT_BLUE_SPOTTED_CARPET = register(BlockRegistry.LIGHT_BLUE_SPOTTED_CARPET);
    public static final RegistryObject<Item> BLUE_SPOTTED_CARPET = register(BlockRegistry.BLUE_SPOTTED_CARPET);
    public static final RegistryObject<Item> PURPLE_SPOTTED_CARPET = register(BlockRegistry.PURPLE_SPOTTED_CARPET);
    public static final RegistryObject<Item> MAGENTA_SPOTTED_CARPET = register(BlockRegistry.MAGENTA_SPOTTED_CARPET);
    public static final RegistryObject<Item> PINK_SPOTTED_CARPET = register(BlockRegistry.PINK_SPOTTED_CARPET);
    public static final RegistryObject<Item> AMBER_SPOTTED_CARPET = register(BlockRegistry.AMBER_SPOTTED_CARPET);
    public static final RegistryObject<Item> AQUA_SPOTTED_CARPET = register(BlockRegistry.AQUA_SPOTTED_CARPET);
    public static final RegistryObject<Item> INDIGO_SPOTTED_CARPET = register(BlockRegistry.INDIGO_SPOTTED_CARPET);
    public static final RegistryObject<Item> MAROON_SPOTTED_CARPET = register(BlockRegistry.MAROON_SPOTTED_CARPET);


    // --- Item-registration helpers. block (arg 1) is always a RegistryObject<Block> - every
    // caller passes a BlockRegistry.X field directly, never .get()'d, so evaluating it is always
    // safe no matter when the caller itself runs; block.get() is only ever touched inside the
    // returned supplier, invoked by Forge during RegisterEvent<Item> (well after RegisterEvent
    // <Block> has already resolved every Block RegistryObject - Item is registered after Block in
    // vanilla's own registry dependency order). block.getId().getPath() mirrors the block's own id
    // eagerly and safely (RegistryObject.getId() never throws, unlike .get()). ---

    private static RegistryObject<Item> register(RegistryObject<Block> block, BiFunction<Block, Item.Properties, Item> factory, Item.Properties settings) {
        return ITEMS.register(block.getId().getPath(), () -> {
            Item item = factory.apply(block.get(), settings);
            if (item instanceof BlockItem blockItem) {
                blockItem.registerBlocks(Item.BY_BLOCK, item);
            }
            return item;
        });
    }
    private static RegistryObject<Item> register(RegistryObject<Block> block) {
        return register(block, BlockItem::new, new Item.Properties());
    }
    private static RegistryObject<Item> register(RegistryObject<Block> block, Item.Properties settings) {
        return register(block, BlockItem::new, settings);
    }
    private static RegistryObject<Item> register(RegistryObject<Block> block, BiFunction<Block, Item.Properties, Item> factory) {
        return register(block, factory, new Item.Properties());
    }

    private static RegistryObject<Item> register(String id, Item.Properties settings) {
        return register(id, Item::new, settings);
    }
    private static RegistryObject<Item> register(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
        return ITEMS.register(id, () -> factory.apply(settings));
    }
    private static RegistryObject<Item> register(String id) {
        return register(id, Item::new, new Item.Properties());
    }
    private static RegistryObject<Item> register(String id, Supplier<? extends Item> factory) {
        return ITEMS.register(id, factory::get);
    }
    private static RegistryObject<Item> registerDye(String id, DyeColor nearestVanillaColor) {
        return ITEMS.register(id, () -> new ModDyeItems(nearestVanillaColor, new Item.Properties()));
    }

    /**
     * ForgeSpawnEggItem(Supplier&lt;? extends EntityType&lt;? extends Mob&gt;&gt;,
     * int background, int highlight, Properties) - {@code type} is a RegistryObject, passed directly
     * (it IS a Supplier), never .get()'d at this call site. The wildcard parameter type (rather than
     * this file's own EntityType<?>) is what lets every EntityTypeRegistry.X argument satisfy
     * ForgeSpawnEggItem's own Supplier<? extends EntityType<? extends Mob>> with no unchecked cast.
     */
    private static <T extends net.minecraft.world.entity.Mob> RegistryObject<Item> registerSpawnEgg(
            RegistryObject<EntityType<T>> type, String mobId, int background, int highlight) {
        return ITEMS.register(mobId + "_spawn_egg",
                () -> new ForgeSpawnEggItem(type, background, highlight, new Item.Properties()));
    }
}
