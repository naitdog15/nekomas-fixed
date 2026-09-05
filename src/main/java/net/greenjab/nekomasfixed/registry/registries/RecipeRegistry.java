package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.recipe.KilnRecipe;
import net.greenjab.nekomasfixed.registry.recipe.TransmuteRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// serializer id "kilning" must be preserved or every kiln recipe file fails to parse
// KILN_SERIALIZER's instance lives on KilnRecipe.java - vanilla's SimpleCookingSerializer isn't
// reusable from another package, its factory interface is package-private
public class RecipeRegistry {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, NekomasFixed.NAMESPACE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NekomasFixed.NAMESPACE);

    public static final RegistryObject<RecipeType<KilnRecipe>> KILN = RECIPE_TYPES.register("kiln",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "nekomasfixed:kilning";
                }
            });

    public static final RegistryObject<RecipeSerializer<KilnRecipe>> KILN_SERIALIZER =
            RECIPE_SERIALIZERS.register("kilning", () -> KilnRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<TransmuteRecipe>> CRAFTING_TRANSMUTE =
            RECIPE_SERIALIZERS.register("crafting_transmute", () -> TransmuteRecipe.SERIALIZER);
}
