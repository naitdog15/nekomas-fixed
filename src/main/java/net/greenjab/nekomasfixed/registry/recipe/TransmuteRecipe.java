package net.greenjab.nekomasfixed.registry.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** Recolours an item instead of replacing it: one stack matching input plus one matching material
 * anywhere in the grid produce a single result that carries the input stack's NBT over - a shapeless
 * recipe can't do that, since it always assembles a fresh copy of its declared result. */
public class TransmuteRecipe implements CraftingRecipe {

    public static final RecipeSerializer<TransmuteRecipe> SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final String group;
    private final CraftingBookCategory category;
    private final Ingredient input;
    private final Ingredient material;
    private final ItemStack result;

    public TransmuteRecipe(ResourceLocation id, String group, CraftingBookCategory category, Ingredient input, Ingredient material, Item result) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.input = input;
        this.material = material;
        this.result = new ItemStack(result);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasInput = false;
        boolean hasMaterial = false;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) continue;

            if (!hasInput && this.input.test(stack)) {
                hasInput = true;
            } else if (!hasMaterial && this.material.test(stack)) {
                hasMaterial = true;
            } else {
                // A third stack, or a second one of either role - not this recipe.
                return false;
            }
        }

        return hasInput && hasMaterial;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registries) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty() && this.input.test(stack)) {
                ItemStack output = this.result.copy();
                if (stack.hasTag()) {
                    output.setTag(stack.getTag().copy());
                }
                return output;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.input);
        ingredients.add(this.material);
        return ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // The same object RecipeRegistry hands to the registry, so this is the registered serializer.
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<TransmuteRecipe> {

        @Override
        public TransmuteRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
                    GsonHelper.getAsString(json, "category", (String) null), CraftingBookCategory.MISC);
            Ingredient input = ingredientFromJson(json, "input");
            Ingredient material = ingredientFromJson(json, "material");
            return new TransmuteRecipe(id, group, category, input, material, resultFromJson(json));
        }

        /** Accepts the ingredient object/array form and the {@code "#tag"} / {@code "item"} shorthand. */
        private static Ingredient ingredientFromJson(JsonObject json, String key) {
            if (!json.has(key)) {
                throw new JsonSyntaxException("Missing " + key + " for transmute recipe");
            }
            JsonElement element = json.get(key);
            if (element.isJsonPrimitive()) {
                String name = element.getAsString();
                JsonObject object = new JsonObject();
                if (name.startsWith("#")) {
                    object.addProperty("tag", name.substring(1));
                } else {
                    object.addProperty("item", name);
                }
                element = object;
            }
            return Ingredient.fromJson(element, false);
        }

        /** A bare item id, or an object naming the item under {@code id} or {@code item}. */
        private static Item resultFromJson(JsonObject json) {
            if (!json.has("result")) {
                throw new JsonSyntaxException("Missing result for transmute recipe");
            }
            JsonElement element = json.get("result");
            String name;
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                name = object.has("id") ? GsonHelper.getAsString(object, "id") : GsonHelper.getAsString(object, "item");
            } else {
                name = GsonHelper.convertToString(element, "result");
            }
            ResourceLocation resultId = new ResourceLocation(name);
            return BuiltInRegistries.ITEM.getOptional(resultId)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + name + "'"));
        }

        @Override
        public TransmuteRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient input = Ingredient.fromNetwork(buf);
            Ingredient material = Ingredient.fromNetwork(buf);
            Item result = buf.readItem().getItem();
            return new TransmuteRecipe(id, group, category, input, material, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TransmuteRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.input.toNetwork(buf);
            recipe.material.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }
}
