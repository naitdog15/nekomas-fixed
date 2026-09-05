package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * CreativeModeTabEvent does not exist in Forge 47.x - a creative tab is an ordinary registry entry
 * on the vanilla Registries.CREATIVE_MODE_TAB key (no ForgeRegistries constant for it). Registered
 * by bare String key ("nekomasfixed"/"nekomasfixedcolours"), matching the Fabric code's own ids
 * exactly - DeferredRegister.register(String,...) takes the same shape.
 * Deliberately no BuildCreativeModeTabContentsEvent handler: items missing from these two lists do
 * not get surfaced into vanilla tabs. That matches how the mod has always behaved (there is no
 * vanilla-tab injection anywhere in the source), so it is not a bug to fix here.
 */
public class ItemGroupRegistry {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NekomasFixed.NAMESPACE);

    public static final RegistryObject<CreativeModeTab> NEKOMASFIXED = TABS.register("nekomasfixed", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.nekomasfixed"))
            .icon(() -> new ItemStack(ItemRegistry.CLAM.get()))
            .displayItems(
                    (params, entries) -> {
                        entries.accept(ItemRegistry.CLAM.get());
                        entries.accept(ItemRegistry.CLAM_BLUE.get());
                        entries.accept(ItemRegistry.CLAM_PINK.get());
                        entries.accept(ItemRegistry.CLAM_PURPLE.get());
                        entries.accept(ItemRegistry.PEARL.get());
                        entries.accept(ItemRegistry.PEARL_BLOCK.get());

                        entries.accept(ItemRegistry.NAUTILUS_BLOCK.get());
                        entries.accept(ItemRegistry.ZOMBIE_NAUTILUS_BLOCK.get());
                        entries.accept(ItemRegistry.CORAL_NAUTILUS_BLOCK.get());
                        entries.accept(ItemRegistry.GLISTERING_MELON.get());
                        entries.accept(ItemRegistry.GEYSER.get());
                        entries.accept(ItemRegistry.KILN.get());
                        entries.accept(ItemRegistry.PYROTECHNICS_TABLE.get());
                        entries.accept(ItemRegistry.ENDERMAN_HEAD.get());
                        entries.accept(ItemRegistry.REDSTONE_STRIKER.get());
                        entries.accept(ItemRegistry.GLOW_TORCH.get());
                        entries.accept(ItemRegistry.TARGET_DUMMY.get());
                        entries.accept(ItemRegistry.TURTLE_CHESTPLATE.get());
                        entries.accept(ItemRegistry.TURTLE_LEGGINGS.get());
                        entries.accept(ItemRegistry.TURTLE_BOOTS.get());

                        entries.accept(ItemRegistry.MOOBLOOM_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.DRENCHED_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.RIME_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.DERELICT_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.ANCHOR.get());
                        entries.accept(ItemRegistry.SUSPICIOUS_SPIDER_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.WILDFIRE_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.NETHER_HEART.get());
                        entries.accept(ItemRegistry.WILDFIRE_TRIDENT.get());
                        entries.accept(ItemRegistry.WILDFIRE_SHIELD.get());
                        entries.accept(ItemRegistry.JEWEL_ARMOR_TRIM_SMITHING_TEMPLATE.get());

                        entries.accept(ItemRegistry.CROWN_SMITHING_TEMPLATE.get());
                        entries.accept(ItemRegistry.COPPER_CROWN.get());
                        entries.accept(ItemRegistry.IRON_CROWN.get());
                        entries.accept(ItemRegistry.GOLDEN_CROWN.get());
                        entries.accept(ItemRegistry.DIAMOND_CROWN.get());
                        entries.accept(ItemRegistry.NETHERITE_CROWN.get());

                        entries.accept(ItemRegistry.SLINGSHOT.get());
                        entries.accept(ItemRegistry.WOODEN_SICKLE.get());
                        entries.accept(ItemRegistry.STONE_SICKLE.get());
                        entries.accept(ItemRegistry.COPPER_SICKLE.get());
                        entries.accept(ItemRegistry.IRON_SICKLE.get());
                        entries.accept(ItemRegistry.GOLDEN_SICKLE.get());
                        entries.accept(ItemRegistry.DIAMOND_SICKLE.get());
                        entries.accept(ItemRegistry.NETHERITE_SICKLE.get());

                        entries.accept(ItemRegistry.SWEETBERRY_CAKE.get());
                        entries.accept(ItemRegistry.PAN_CAKE.get());
                        entries.accept(ItemRegistry.GLOWBERRY_CAKE.get());
                        entries.accept(ItemRegistry.APPLE_CAKE.get());
                        entries.accept(ItemRegistry.VANILLA_CAKE.get());
                        entries.accept(ItemRegistry.COOKIE_CAKE.get());
                        entries.accept(ItemRegistry.CHOCOLATE_CAKE.get());
                        entries.accept(ItemRegistry.BEETROOT_CAKE.get());

                        entries.accept(ItemRegistry.TERMITE_SPAWN_EGG.get());
                        entries.accept(ItemRegistry.TERMITE_HIVE.get());
                        entries.accept(ItemRegistry.TERMITE_BLOCK.get());
                        entries.accept(ItemRegistry.HOLLOW_OAK_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_SPRUCE_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_BIRCH_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_JUNGLE_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_ACACIA_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_DARK_OAK_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_MANGROVE_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_CHERRY_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_PALE_OAK_LOG.get());
                        entries.accept(ItemRegistry.HOLLOW_BAMBOO_BLOCK.get());
                        entries.accept(ItemRegistry.HOLLOW_CRIMSON_STEM.get());
                        entries.accept(ItemRegistry.HOLLOW_WARPED_STEM.get());

                        entries.accept(ItemRegistry.BOAT_UPGRADE_TEMPLATE.get());
                        entries.accept(ItemRegistry.BIG_OAK_BOAT.get());
                        entries.accept(ItemRegistry.BIG_SPRUCE_BOAT.get());
                        entries.accept(ItemRegistry.BIG_BIRCH_BOAT.get());
                        entries.accept(ItemRegistry.BIG_JUNGLE_BOAT.get());
                        entries.accept(ItemRegistry.BIG_ACACIA_BOAT.get());
                        entries.accept(ItemRegistry.BIG_DARK_OAK_BOAT.get());
                        entries.accept(ItemRegistry.BIG_MANGROVE_BOAT.get());
                        entries.accept(ItemRegistry.BIG_CHERRY_BOAT.get());
                        entries.accept(ItemRegistry.BIG_PALE_OAK_BOAT.get());
                        entries.accept(ItemRegistry.BIG_BAMBOO_BOAT.get());

                        entries.accept(ItemRegistry.HUGE_OAK_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_SPRUCE_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_BIRCH_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_JUNGLE_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_ACACIA_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_DARK_OAK_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_MANGROVE_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_CHERRY_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_PALE_OAK_BOAT.get());
                        entries.accept(ItemRegistry.HUGE_BAMBOO_BOAT.get());


                    }).build());

    public static final RegistryObject<CreativeModeTab> NEKOMASFIXEDCOLOURS = TABS.register("nekomasfixedcolours", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.nekomasfixedcolours"))
            .icon(() -> new ItemStack(ItemRegistry.AMBER_DYE.get()))
            .displayItems(
                    (params, entries) -> {

                        entries.accept(ItemRegistry.AMBER_WOOL.get());
                        entries.accept(ItemRegistry.AQUA_WOOL.get());
                        entries.accept(ItemRegistry.INDIGO_WOOL.get());
                        entries.accept(ItemRegistry.MAROON_WOOL.get());
                        entries.accept(ItemRegistry.AMBER_CARPET.get());
                        entries.accept(ItemRegistry.AQUA_CARPET.get());
                        entries.accept(ItemRegistry.INDIGO_CARPET.get());
                        entries.accept(ItemRegistry.MAROON_CARPET.get());

                        entries.accept(ItemRegistry.AMBER_TERRACOTTA.get());
                        entries.accept(ItemRegistry.AQUA_TERRACOTTA.get());
                        entries.accept(ItemRegistry.INDIGO_TERRACOTTA.get());
                        entries.accept(ItemRegistry.MAROON_TERRACOTTA.get());

                        entries.accept(ItemRegistry.AMBER_CONCRETE.get());
                        entries.accept(ItemRegistry.AQUA_CONCRETE.get());
                        entries.accept(ItemRegistry.INDIGO_CONCRETE.get());
                        entries.accept(ItemRegistry.MAROON_CONCRETE.get());
                        entries.accept(ItemRegistry.AMBER_CONCRETE_POWDER.get());
                        entries.accept(ItemRegistry.AQUA_CONCRETE_POWDER.get());
                        entries.accept(ItemRegistry.INDIGO_CONCRETE_POWDER.get());
                        entries.accept(ItemRegistry.MAROON_CONCRETE_POWDER.get());

                        entries.accept(ItemRegistry.AMBER_GLAZED_TERRACOTTA.get());
                        entries.accept(ItemRegistry.AQUA_GLAZED_TERRACOTTA.get());
                        entries.accept(ItemRegistry.INDIGO_GLAZED_TERRACOTTA.get());
                        entries.accept(ItemRegistry.MAROON_GLAZED_TERRACOTTA.get());

                        entries.accept(ItemRegistry.AMBER_STAINED_GLASS.get());
                        entries.accept(ItemRegistry.AQUA_STAINED_GLASS.get());
                        entries.accept(ItemRegistry.INDIGO_STAINED_GLASS.get());
                        entries.accept(ItemRegistry.MAROON_STAINED_GLASS.get());
                        entries.accept(ItemRegistry.AMBER_STAINED_GLASS_PANE.get());
                        entries.accept(ItemRegistry.AQUA_STAINED_GLASSS_PANE.get());
                        entries.accept(ItemRegistry.INDIGO_STAINED_GLASSS_PANE.get());
                        entries.accept(ItemRegistry.MAROON_STAINED_GLASSS_PANE.get());

                        entries.accept(ItemRegistry.AMBER_SHULKER_BOX.get());
                        entries.accept(ItemRegistry.AQUA_SHULKER_BOX.get());
                        entries.accept(ItemRegistry.INDIGO_SHULKER_BOX.get());
                        entries.accept(ItemRegistry.MAROON_SHULKER_BOX.get());

                        entries.accept(ItemRegistry.AMBER_BED.get());
                        entries.accept(ItemRegistry.AQUA_BED.get());
                        entries.accept(ItemRegistry.INDIGO_BED.get());
                        entries.accept(ItemRegistry.MAROON_BED.get());

                        entries.accept(ItemRegistry.AMBER_CANDLE.get());
                        entries.accept(ItemRegistry.AQUA_CANDLE.get());
                        entries.accept(ItemRegistry.INDIGO_CANDLE.get());
                        entries.accept(ItemRegistry.MAROON_CANDLE.get());

                        entries.accept(ItemRegistry.AMBER_BUNDLE.get());
                        entries.accept(ItemRegistry.AQUA_BUNDLE.get());
                        entries.accept(ItemRegistry.INDIGO_BUNDLE.get());
                        entries.accept(ItemRegistry.MAROON_BUNDLE.get());

                        if (NekomasFixedConfig.HARNESSES.get()) {
                            ItemRegistry.AMBER_HARNESS.ifPresent(entries::accept);
                            ItemRegistry.AQUA_HARNESS.ifPresent(entries::accept);
                            ItemRegistry.INDIGO_HARNESS.ifPresent(entries::accept);
                            ItemRegistry.MAROON_HARNESS.ifPresent(entries::accept);
                        }

                        entries.accept(ItemRegistry.AMBER_DYE.get());
                        entries.accept(ItemRegistry.AQUA_DYE.get());
                        entries.accept(ItemRegistry.INDIGO_DYE.get());
                        entries.accept(ItemRegistry.MAROON_DYE.get());


                        entries.accept(ItemRegistry.WHITE_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.GRAY_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.BLACK_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.BROWN_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.RED_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.ORANGE_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.YELLOW_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.LIME_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.GREEN_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.CYAN_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.BLUE_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.PURPLE_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.MAGENTA_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.PINK_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.AMBER_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.AQUA_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.INDIGO_DYED_BRUSH.get());
                        entries.accept(ItemRegistry.MAROON_DYED_BRUSH.get());

                        entries.accept(ItemRegistry.WHITE_BRICKS.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_BRICKS.get());
                        entries.accept(ItemRegistry.GRAY_BRICKS.get());
                        entries.accept(ItemRegistry.BLACK_BRICKS.get());
                        entries.accept(ItemRegistry.BROWN_BRICKS.get());
                        entries.accept(ItemRegistry.RED_BRICKS.get());
                        entries.accept(ItemRegistry.ORANGE_BRICKS.get());
                        entries.accept(ItemRegistry.YELLOW_BRICKS.get());
                        entries.accept(ItemRegistry.LIME_BRICKS.get());
                        entries.accept(ItemRegistry.GREEN_BRICKS.get());
                        entries.accept(ItemRegistry.CYAN_BRICKS.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_BRICKS.get());
                        entries.accept(ItemRegistry.BLUE_BRICKS.get());
                        entries.accept(ItemRegistry.PURPLE_BRICKS.get());
                        entries.accept(ItemRegistry.MAGENTA_BRICKS.get());
                        entries.accept(ItemRegistry.PINK_BRICKS.get());
                        entries.accept(ItemRegistry.AMBER_BRICKS.get());
                        entries.accept(ItemRegistry.AQUA_BRICKS.get());
                        entries.accept(ItemRegistry.INDIGO_BRICKS.get());
                        entries.accept(ItemRegistry.MAROON_BRICKS.get());

                        entries.accept(ItemRegistry.WHITE_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.GRAY_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.BLACK_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.BROWN_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.RED_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.ORANGE_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.YELLOW_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.LIME_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.GREEN_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.CYAN_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.BLUE_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.PURPLE_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.MAGENTA_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.PINK_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.AMBER_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.AQUA_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.INDIGO_BRICK_SLAB.get());
                        entries.accept(ItemRegistry.MAROON_BRICK_SLAB.get());

                        entries.accept(ItemRegistry.WHITE_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.GRAY_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.BLACK_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.BROWN_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.RED_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.ORANGE_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.YELLOW_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.LIME_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.GREEN_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.CYAN_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.BLUE_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.PURPLE_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.MAGENTA_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.PINK_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.AMBER_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.AQUA_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.INDIGO_BRICK_STAIRS.get());
                        entries.accept(ItemRegistry.MAROON_BRICK_STAIRS.get());

                        entries.accept(ItemRegistry.WHITE_BRICK_WALL.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_BRICK_WALL.get());
                        entries.accept(ItemRegistry.GRAY_BRICK_WALL.get());
                        entries.accept(ItemRegistry.BLACK_BRICK_WALL.get());
                        entries.accept(ItemRegistry.BROWN_BRICK_WALL.get());
                        entries.accept(ItemRegistry.RED_BRICK_WALL.get());
                        entries.accept(ItemRegistry.ORANGE_BRICK_WALL.get());
                        entries.accept(ItemRegistry.YELLOW_BRICK_WALL.get());
                        entries.accept(ItemRegistry.LIME_BRICK_WALL.get());
                        entries.accept(ItemRegistry.GREEN_BRICK_WALL.get());
                        entries.accept(ItemRegistry.CYAN_BRICK_WALL.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_BRICK_WALL.get());
                        entries.accept(ItemRegistry.BLUE_BRICK_WALL.get());
                        entries.accept(ItemRegistry.PURPLE_BRICK_WALL.get());
                        entries.accept(ItemRegistry.MAGENTA_BRICK_WALL.get());
                        entries.accept(ItemRegistry.PINK_BRICK_WALL.get());
                        entries.accept(ItemRegistry.AMBER_BRICK_WALL.get());
                        entries.accept(ItemRegistry.AQUA_BRICK_WALL.get());
                        entries.accept(ItemRegistry.INDIGO_BRICK_WALL.get());
                        entries.accept(ItemRegistry.MAROON_BRICK_WALL.get());
                        

                        entries.accept(ItemRegistry.CLEAR_FROGLIGHT.get());
                        entries.accept(ItemRegistry.CLOUDY_FROGLIGHT.get());
                        entries.accept(ItemRegistry.CASCADING_FROGLIGHT.get());
                        entries.accept(ItemRegistry.CLOUDBURST_FROGLIGHT.get());
                        entries.accept(ItemRegistry.CHAMOISEE_FROGLIGHT.get());
                        entries.accept(ItemRegistry.SANGUINE_FROGLIGHT.get());
                        entries.accept(ItemRegistry.VERMILION_FROGLIGHT.get());
                        entries.accept(ItemRegistry.MANDARIN_FROGLIGHT.get());
                        entries.accept(ItemRegistry.LEMON_FROGLIGHT.get());
                        entries.accept(ItemRegistry.KIWI_FROGLIGHT.get());
                        entries.accept(ItemRegistry.SEAFOAM_FROGLIGHT.get());
                        entries.accept(ItemRegistry.TEAL_FROGLIGHT.get());
                        entries.accept(ItemRegistry.CERULEAN_FROGLIGHT.get());
                        entries.accept(ItemRegistry.NAVY_FROGLIGHT.get());
                        entries.accept(ItemRegistry.LAVENDER_FROGLIGHT.get());
                        entries.accept(ItemRegistry.THULIAN_FROGLIGHT.get());
                        entries.accept(ItemRegistry.SAKURA_FROGLIGHT.get());

                        entries.accept(ItemRegistry.WHITE_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.GRAY_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.BLACK_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.BROWN_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.RED_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.ORANGE_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.YELLOW_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.LIME_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.GREEN_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.CYAN_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.BLUE_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.PURPLE_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.MAGENTA_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.PINK_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.AMBER_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.AQUA_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.INDIGO_SPOTTED_WOOL.get());
                        entries.accept(ItemRegistry.MAROON_SPOTTED_WOOL.get());

                        entries.accept(ItemRegistry.WHITE_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.LIGHT_GRAY_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.GRAY_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.BLACK_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.BROWN_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.RED_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.ORANGE_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.YELLOW_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.LIME_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.GREEN_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.CYAN_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.LIGHT_BLUE_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.BLUE_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.PURPLE_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.MAGENTA_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.PINK_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.AMBER_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.AQUA_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.INDIGO_SPOTTED_CARPET.get());
                        entries.accept(ItemRegistry.MAROON_SPOTTED_CARPET.get());
                    }).build());
}
