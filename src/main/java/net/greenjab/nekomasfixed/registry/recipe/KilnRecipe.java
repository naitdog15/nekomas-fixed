package net.greenjab.nekomasfixed.registry.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * The kiln's cooking recipe - an ordinary {@code AbstractCookingRecipe} with its own type and its own
 * serializer, shaped exactly like vanilla's {@code SmeltingRecipe}. {@link #SERIALIZER} is what
 * {@code RecipeRegistry} registers under the id {@code kilning}, and that id has to stay as it is or
 * every kiln recipe file stops parsing.
 * <p>
 * The recipe book's three kiln tabs are not set up here: sorting recipes into
 * {@code RecipeBookCategories} is a client-side concern and lives in
 * {@code screen/KilnRecipeBookClient.java}.
 */
public class KilnRecipe extends AbstractCookingRecipe {

    /**
     * Mirrors vanilla's {@code SimpleCookingSerializer}, which cannot be reused directly: its
     * {@code CookieBaker} factory interface is package-private, so a {@code new
     * SimpleCookingSerializer<>(KilnRecipe::new, 100)} from this package will not resolve.
     */
    public static final RecipeSerializer<KilnRecipe> SERIALIZER = new Serializer();

    public KilnRecipe(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeRegistry.KILN.get(), id, group, category, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemRegistry.KILN.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.KILN_SERIALIZER.get();
    }

    private Ingredient input() {
        return this.ingredient;
    }

    private ItemStack output() {
        return this.result;
    }

    public static class Serializer implements RecipeSerializer<KilnRecipe> {
        /** Every shipped kiln recipe states its own cookingtime; this only covers ones that omit it. */
        private static final int DEFAULT_COOKING_TIME = 100;

        @Override
        public KilnRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CookingBookCategory category = CookingBookCategory.CODEC.byName(
                    GsonHelper.getAsString(json, "category", (String) null), CookingBookCategory.MISC);
            JsonElement ingredientJson = GsonHelper.isArrayNode(json, "ingredient")
                    ? GsonHelper.getAsJsonArray(json, "ingredient")
                    : GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson, false);
            if (!json.has("result")) {
                throw new JsonSyntaxException("Missing result, expected to find a string or object");
            }
            ItemStack result;
            if (json.get("result").isJsonObject()) {
                // Forge's count-carrying result object, which every shipped kiln recipe uses
                result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            } else {
                String resultId = GsonHelper.getAsString(json, "result");
                result = new ItemStack(BuiltInRegistries.ITEM.getOptional(new ResourceLocation(resultId))
                        .orElseThrow(() -> new IllegalStateException("Item: " + resultId + " does not exist")));
            }
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
            int cookingTime = GsonHelper.getAsInt(json, "cookingtime", DEFAULT_COOKING_TIME);
            return new KilnRecipe(id, group, category, ingredient, result, experience, cookingTime);
        }

        @Override
        public KilnRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CookingBookCategory category = buf.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            float experience = buf.readFloat();
            int cookingTime = buf.readVarInt();
            return new KilnRecipe(id, group, category, ingredient, result, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, KilnRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            recipe.input().toNetwork(buf);
            buf.writeItem(recipe.output());
            buf.writeFloat(recipe.getExperience());
            buf.writeVarInt(recipe.getCookingTime());
        }
    }
}
