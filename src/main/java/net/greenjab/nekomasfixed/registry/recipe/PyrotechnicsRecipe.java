package net.greenjab.nekomasfixed.registry.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Optional;

public class PyrotechnicsRecipe implements Recipe<RecipeInput> {

    private final Ingredient dye1;
    private final Ingredient dye2;
    private final Ingredient paper;
    private final Ingredient gunpowder;
    private final ItemStack output;
    private PlacementInfo ingredientPlacement;

    public PyrotechnicsRecipe(Ingredient dye1, Ingredient dye2, Ingredient paper, Ingredient gunpowder, ItemStack output) {
        this.dye1 = dye1;
        this.dye2 = dye2;
        this.paper = paper;
        this.gunpowder = gunpowder;
        this.output = output;
    }

    public static final MapCodec<PyrotechnicsRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("dye1").forGetter(recipe -> recipe.dye1),
            Ingredient.CODEC.fieldOf("dye2").forGetter(recipe -> recipe.dye2),
            Ingredient.CODEC.fieldOf("paper").forGetter(recipe -> recipe.paper),
            Ingredient.CODEC.fieldOf("gunpowder").forGetter(recipe -> recipe.gunpowder),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output)
    ).apply(instance, PyrotechnicsRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PyrotechnicsRecipe> PACKET_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.dye1,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.dye2,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.paper,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.gunpowder,
            ItemStack.STREAM_CODEC, recipe -> recipe.output,
            PyrotechnicsRecipe::new
    );

    @Override
    public boolean matches(RecipeInput input, Level world) {
        if (input.size() < 4) return false;
        return this.dye1.test(input.getItem(0)) &&
                this.dye2.test(input.getItem(1)) &&
                this.paper.test(input.getItem(2)) &&
                this.gunpowder.test(input.getItem(3));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return this.output.copy();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = PlacementInfo.createFromOptionals(List.of(
                    Optional.of(this.dye1),
                    Optional.of(this.dye2),
                    Optional.of(this.paper),
                    Optional.of(this.gunpowder)
            ));
        }
        return this.ingredientPlacement;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<PyrotechnicsRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "pyrotechnics";
    }

    public static class Serializer implements RecipeSerializer<PyrotechnicsRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "pyrotechnics";

        @Override
        public MapCodec<PyrotechnicsRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PyrotechnicsRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}