package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.recipe.KilnRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * id {@code nekomasfixed:kilning} MUST be preserved on the serializer or every kiln recipe JSON
 * fails to parse. {@code RecipePropertySet}/{@code RecipeBookCategory} (both 1.21.4+) are DROPPED
 * ("no 1.20.1 registry"); both {@code KilnMenu} constructors take
 * {@code RecipeType<? extends AbstractCookingRecipe>} in place of the dropped
 * {@code RecipePropertySet} key - see {@code screen/KilnMenu.java} for that end of it.
 * <p>
 * {@link #KILN_SERIALIZER}'s actual instance lives on {@code registry/recipe/KilnRecipe.java} as a
 * {@code SimpleCookingSerializer}-shaped class of its own, vanilla's not being reusable from another
 * package (its factory interface is package-private).
 */
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

    // id "kilning" (not "kiln") preserved verbatim - see class javadoc.
    public static final RegistryObject<RecipeSerializer<KilnRecipe>> KILN_SERIALIZER =
            RECIPE_SERIALIZERS.register("kilning", () -> KilnRecipe.SERIALIZER);
}
