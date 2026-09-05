package net.greenjab.nekomasfixed.datagen;

import com.mojang.datafixers.util.Pair;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.AllDyes;
import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.greenjab.nekomasfixed.util.ItemDyeMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

/** 1.20.1's RecipeProvider only takes a PackOutput, and buildRecipes hands its results to a plain
 * {@code Consumer<FinishedRecipe>}; shaped/shapeless aren't inherited here, they're static-imported. */
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        for (AllDyes colour : AllDyes.values()) {
            createRingRecipe(output, RecipeCategory.MISC, ItemDyeMap.DYE.get(colour), Items.BRUSH, ItemDyeMap.BRUSH.get(colour), "dyed_brush", 1)
                    .save(output);
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICKS, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICKS.get(colour).asItem(), "dyed_bricks_dyed", 8)
                    .save(output, NekomasFixed.id(BlockDyeMap.BRICKS.get(colour).asItem() + "_dyed"));
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_SLAB, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_SLAB.get(colour).asItem(), "dyed_brick_slab_dyed", 8)
                    .save(output, NekomasFixed.id(BlockDyeMap.BRICK_SLAB.get(colour).asItem() + "_dyed"));
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_STAIRS, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), "dyed_brick_stairs_dyed", 8)
                    .save(output, NekomasFixed.id(BlockDyeMap.BRICK_STAIRS.get(colour).asItem() + "_dyed"));
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_WALL, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_WALL.get(colour).asItem(), "dyed_brick_wall_dyed", 8)
                    .save(output, NekomasFixed.id(BlockDyeMap.BRICK_WALL.get(colour).asItem() + "_dyed"));
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_SLAB.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem(), 2);
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem());
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_WALL.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem());
            slabBuilder(RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_SLAB.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_slab").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
            stairBuilder(BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_stairs").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
            wallBuilder(RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_WALL.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_wall").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
        }
        ArrayList<Item> spottedWool = new ArrayList<>();
        BlockDyeMap.SPOTTED_WOOL.values().forEach(e -> spottedWool.add(e.asItem()));
        colorItemWithDye(output, RecipeCategory.BUILDING_BLOCKS, ItemDyeMap.DYE.values().stream().toList(), spottedWool, "spotted_wool");

        ArrayList<Item> spottedCarpet = new ArrayList<>();
        BlockDyeMap.SPOTTED_CARPET.values().forEach(e -> spottedCarpet.add(e.asItem()));
        colorItemWithDye(output, RecipeCategory.DECORATIONS, ItemDyeMap.DYE.values().stream().toList(), spottedCarpet, "spotted_carpet_dye");

        List<Pair<Item, Item>> hollows = List.of(
                Pair.of(Items.OAK_PLANKS, ItemRegistry.HOLLOW_OAK_LOG.get()),
                Pair.of(Items.SPRUCE_PLANKS, ItemRegistry.HOLLOW_SPRUCE_LOG.get()),
                Pair.of(Items.BIRCH_PLANKS, ItemRegistry.HOLLOW_BIRCH_LOG.get()),
                Pair.of(Items.JUNGLE_PLANKS, ItemRegistry.HOLLOW_JUNGLE_LOG.get()),
                Pair.of(Items.ACACIA_PLANKS, ItemRegistry.HOLLOW_ACACIA_LOG.get()),
                Pair.of(Items.DARK_OAK_PLANKS, ItemRegistry.HOLLOW_DARK_OAK_LOG.get()),
                Pair.of(Items.MANGROVE_PLANKS, ItemRegistry.HOLLOW_MANGROVE_LOG.get()),
                Pair.of(Items.CHERRY_PLANKS, ItemRegistry.HOLLOW_CHERRY_LOG.get()),
                // Items.PALE_OAK_PLANKS dropped: Pale Garden wood doesn't exist on 1.20.1.
                Pair.of(Items.BAMBOO_PLANKS, ItemRegistry.HOLLOW_BAMBOO_BLOCK.get()),
                Pair.of(Items.CRIMSON_PLANKS, ItemRegistry.HOLLOW_CRIMSON_STEM.get()),
                Pair.of(Items.WARPED_PLANKS, ItemRegistry.HOLLOW_WARPED_STEM.get()));
        for (Pair<Item, Item> hollow : hollows) {
            shapeless(RecipeCategory.BUILDING_BLOCKS, hollow.getFirst(), 1)
                    .requires(hollow.getSecond())
                    .unlockedBy(getHasName(hollow.getSecond()), has(hollow.getSecond()))
                    .save(output, NekomasFixed.id(hollow.getFirst() + "_from_hollow_log"));
        }

        shaped(RecipeCategory.TOOLS, ItemRegistry.REDSTONE_STRIKER.get(), 1)
                .pattern("RG")
                .pattern("FR")
                .define('R', Items.REDSTONE)
                .define('G', Items.GOLD_INGOT)
                .define('F', Items.FLINT)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .save(output);
    }

    private ShapedRecipeBuilder createRingRecipe(Consumer<FinishedRecipe> output, RecipeCategory category, Item outside, Item inside, Item result, String group, int num) {
        return shaped(category, result, num)
                .pattern("###")
                .pattern("#D#")
                .pattern("###")
                .define('#', outside)
                .define('D', inside)
                .group(group)
                .unlockedBy(getHasName(outside), has(outside))
                .unlockedBy(getHasName(inside), has(inside));
    }

    // vanilla's colorBlockWithDye hardcodes BUILDING_BLOCKS; the carpet call site here needs
    // DECORATIONS, so this opens the category up as a parameter instead.
    private static void colorItemWithDye(Consumer<FinishedRecipe> output, RecipeCategory category, List<Item> dyes, List<Item> results, String group) {
        for (int i = 0; i < dyes.size(); i++) {
            Item dye = dyes.get(i);
            Item result = results.get(i);
            shapeless(category, result)
                    .requires(dye)
                    .requires(Ingredient.of(results.stream().filter(item -> !item.equals(result)).map(ItemStack::new)))
                    .group(group)
                    .unlockedBy("has_needed_dye", has(dye))
                    .save(output, NekomasFixed.id("dye_" + getItemName(result)));
        }
    }
}
