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
 * {@code RecipePropertySet} key - that reshape is {@code screen/KilnMenu.java}'s (outside this
 * package).
 * <p>
 * <b>Cross-file dependency, uncommitted.</b> {@code registry/recipe/KilnRecipe.java} is outside this
 * package's scope ({@code registry/registries/**}/{@code registry/block/**}/
 * {@code registry/item/**} only) but is the recipe {@link #KILN} and {@link #KILN_SERIALIZER} below
 * assume exists in 1.20.1-compatible form. As things stand it is written entirely in 26.2-only
 * API: its constructor takes {@code Recipe.CommonInfo}/{@code AbstractCookingRecipe.CookingBookInfo}/
 * {@code ItemStackTemplate} (none exist on 1.20.1), {@code SERIALIZER} is built from a
 * {@code StreamCodec}-based generic {@code RecipeSerializer<>(MapCodec, StreamCodec)} constructor
 * (1.20.5+; 1.20.1 needs a {@code SimpleCookingSerializer}-style class with
 * {@code fromNetwork(FriendlyByteBuf)}/{@code toNetwork(FriendlyByteBuf, T)}), and
 * {@code recipeBookCategory()} returns the now-dropped {@code RecipeRegistry.KILNING_BLOCK}/
 * {@code KILNING_MISC}. Whoever owns {@code registry/recipe/**} needs to rewrite that class in
 * parallel with this one; until then this file's own reference to {@code KilnRecipe.SERIALIZER}
 * will not compile - an expected, named cross-file error, not a defect in this file.
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

    // id "kilning" (not "kiln") preserved verbatim - see class javadoc; KilnRecipe.SERIALIZER is the
    // one field whoever converts KilnRecipe.java must keep resolvable to a real RecipeSerializer.
    public static final RegistryObject<RecipeSerializer<KilnRecipe>> KILN_SERIALIZER =
            RECIPE_SERIALIZERS.register("kilning", () -> KilnRecipe.SERIALIZER);
}
