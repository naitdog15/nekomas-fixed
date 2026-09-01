package net.greenjab.nekomasfixed.datagen;

import com.mojang.datafixers.util.Pair;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.AllDyes;
import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.greenjab.nekomasfixed.util.ItemDyeMap;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * {@code FabricRecipeProvider} → vanilla {@code RecipeProvider} directly (no
 * Forge-side wrapper needed — the mod bus's {@code GatherDataEvent} handler adds it straight to the
 * generator). 26.2's {@code RecipeProvider} construction shape ({@code RecipeOutput} handed to the
 * constructor, stored as a field) differs from 1.20.1's real one — 1.20.1's abstract method is
 * {@code buildRecipes(RecipeOutput output)}, with {@code output} a method parameter, not a field. The
 * original body already threaded a variable literally named {@code output} through every {@code
 * .save(output)} call, so it binds onto the 1.20.1 parameter unchanged; only the class shape (no
 * Fabric double-wrap, no {@code createRecipeProvider}/anonymous-class indirection) needed fixing.
 */
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        shapeless(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.BAOBAB_PLANKS, 4)
                .requires(ModTags.BAOBAB_LOGS)
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_LOG), has(ItemRegistry.BAOBAB_LOG)).save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.BAOBAB_WOOD, 3)
                .pattern("##")
                .pattern("##")
                .define('#', ItemRegistry.BAOBAB_LOG)
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_LOG), has(ItemRegistry.BAOBAB_LOG))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.STRIPPED_BAOBAB_WOOD, 3)
                .pattern("##")
                .pattern("##")
                .define('#', ItemRegistry.STRIPPED_BAOBAB_LOG)
                .unlockedBy(getHasName(ItemRegistry.STRIPPED_BAOBAB_LOG), has(ItemRegistry.STRIPPED_BAOBAB_LOG))
                .save(output);

        woodenBoat(ItemRegistry.BAOBAB_BOAT, ItemRegistry.BAOBAB_PLANKS);
        chestBoat(ItemRegistry.BAOBAB_CHEST_BOAT, ItemRegistry.BAOBAB_PLANKS);

        fenceBuilder(ItemRegistry.BAOBAB_FENCE, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        fenceGateBuilder(ItemRegistry.BAOBAB_FENCE_GATE, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        buttonBuilder(ItemRegistry.BAOBAB_BUTTON, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        doorBuilder(ItemRegistry.BAOBAB_DOOR, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        trapdoorBuilder(ItemRegistry.BAOBAB_TRAPDOOR, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ItemRegistry.BAOBAB_PRESSURE_PLATE, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        signBuilder(ItemRegistry.BAOBAB_SIGN, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        hangingSignBuilder(ItemRegistry.BAOBAB_HANGING_SIGN, Ingredient.of(ItemRegistry.BAOBAB_LOG))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_LOG), has(ItemRegistry.BAOBAB_LOG))
                .save(output);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ItemRegistry.BAOBAB_SLAB, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);
        stairBuilder(ItemRegistry.BAOBAB_STAIRS, Ingredient.of(ItemRegistry.BAOBAB_PLANKS))
                .unlockedBy(getHasName(ItemRegistry.BAOBAB_PLANKS), has(ItemRegistry.BAOBAB_PLANKS))
                .save(output);

        for (AllDyes colour : AllDyes.values()) {
            createRingRecipe(output, RecipeCategory.MISC, ItemDyeMap.DYE.get(colour), Items.BRUSH, ItemDyeMap.BRUSH.get(colour), "dyed_brush", 1)
                    .save(output);
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICKS, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICKS.get(colour).asItem(), "dyed_bricks_dyed", 8)
                    .save(output, BlockDyeMap.BRICKS.get(colour).asItem() + "_dyed");
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_SLAB, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_SLAB.get(colour).asItem(), "dyed_brick_slab_dyed", 8)
                    .save(output, BlockDyeMap.BRICK_SLAB.get(colour).asItem() + "_dyed");
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_STAIRS, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), "dyed_brick_stairs_dyed", 8)
                    .save(output, BlockDyeMap.BRICK_STAIRS.get(colour).asItem() + "_dyed");
            createRingRecipe(output, RecipeCategory.BUILDING_BLOCKS, Items.BRICK_WALL, ItemDyeMap.DYE.get(colour), BlockDyeMap.BRICK_WALL.get(colour).asItem(), "dyed_brick_wall_dyed", 8)
                    .save(output, BlockDyeMap.BRICK_WALL.get(colour).asItem() + "_dyed");
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_SLAB.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem(), 2);
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem());
            stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_WALL.get(colour).asItem(), BlockDyeMap.BRICKS.get(colour).asItem());
            slabBuilder(RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_SLAB.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_slab").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
            stairBuilder(BlockDyeMap.BRICK_STAIRS.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_stairs").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
            wallBuilder(RecipeCategory.BUILDING_BLOCKS, BlockDyeMap.BRICK_WALL.get(colour).asItem(), Ingredient.of(BlockDyeMap.BRICKS.get(colour).asItem())).group("dyed_brick_wall").unlockedBy(getHasName(BlockDyeMap.BRICKS.get(colour).asItem()), has(BlockDyeMap.BRICKS.get(colour).asItem())).save(output);
        }
        ArrayList<Item> spottedWool = new ArrayList<>();
        BlockDyeMap.SPOTTED_WOOL.values().forEach(e -> spottedWool.add(e.asItem()));
        colorItemWithDye(output, ItemDyeMap.DYE.values().stream().toList(), spottedWool, "spotted_wool", RecipeCategory.BUILDING_BLOCKS);

        ArrayList<Item> spottedCarpet = new ArrayList<>();
        BlockDyeMap.SPOTTED_CARPET.values().forEach(e -> spottedCarpet.add(e.asItem()));
        colorItemWithDye(output, ItemDyeMap.DYE.values().stream().toList(), spottedCarpet, "spotted_carpet_dye", RecipeCategory.DECORATIONS);

        List<Pair<Item, Item>> hollows = List.of(
                Pair.of(Items.OAK_PLANKS, ItemRegistry.HOLLOW_OAK_LOG),
                Pair.of(Items.SPRUCE_PLANKS, ItemRegistry.HOLLOW_SPRUCE_LOG),
                Pair.of(Items.BIRCH_PLANKS, ItemRegistry.HOLLOW_BIRCH_LOG),
                Pair.of(Items.JUNGLE_PLANKS, ItemRegistry.HOLLOW_JUNGLE_LOG),
                Pair.of(Items.ACACIA_PLANKS, ItemRegistry.HOLLOW_ACACIA_LOG),
                Pair.of(Items.DARK_OAK_PLANKS, ItemRegistry.HOLLOW_DARK_OAK_LOG),
                Pair.of(Items.MANGROVE_PLANKS, ItemRegistry.HOLLOW_MANGROVE_LOG),
                Pair.of(Items.CHERRY_PLANKS, ItemRegistry.HOLLOW_CHERRY_LOG),
                // Items.PALE_OAK_PLANKS dropped: Pale Garden wood is a post-1.20.1 vanilla addition,
                // absent from this Minecraft version entirely (no analogue to fall back to — a real,
                // content gap, not an oversight).
                Pair.of(Items.BAMBOO_PLANKS, ItemRegistry.HOLLOW_BAMBOO_BLOCK),
                Pair.of(Items.CRIMSON_PLANKS, ItemRegistry.HOLLOW_CRIMSON_STEM),
                Pair.of(Items.WARPED_PLANKS, ItemRegistry.HOLLOW_WARPED_STEM),
                Pair.of(ItemRegistry.BAOBAB_PLANKS, ItemRegistry.HOLLOW_BAOBAB_LOG));
        for (Pair<Item, Item> hollow : hollows) {
            shapeless(RecipeCategory.BUILDING_BLOCKS, hollow.getFirst(), 1)
                    .requires(hollow.getSecond())
                    .unlockedBy(getHasName(hollow.getSecond()), has(hollow.getSecond()))
                    .save(output, hollow.getFirst() + "_from_hollow_log");
        }

        shaped(RecipeCategory.TOOLS, ItemRegistry.REDSTONE_STRIKER, 1)
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

    private ShapedRecipeBuilder createRingRecipe(RecipeOutput output, RecipeCategory category, Item outside, Item inside, Item result, String group, int num) {
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
}
