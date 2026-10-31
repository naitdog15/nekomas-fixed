package net.greenjab.nekomasfixed.registry.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.item.PrismItem;
import net.greenjab.nekomasfixed.registry.other.PrismGlint;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

/** Smithing: a prism over any armour piece stores that glint id on the stack. */
public class ArmorGlintRecipe implements SmithingRecipe {

    public static final RecipeSerializer<ArmorGlintRecipe> SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final Ingredient template;
    private final Ingredient addition;

    public ArmorGlintRecipe(ResourceLocation id, Ingredient template, Ingredient addition) {
        this.id = id;
        this.template = template;
        this.addition = addition;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return isTemplateIngredient(container.getItem(0)) && isBaseIngredient(container.getItem(1))
                && isAdditionIngredient(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registries) {
        if (!(container.getItem(0).getItem() instanceof PrismItem prism)) return ItemStack.EMPTY;

        PrismGlint glint = prism.getGlint();
        ItemStack base = container.getItem(1);
        if (glint.id().equals(StackData.read(base, StackData.KEY_ARMOR_GLINT, Codec.STRING, null))) {
            return ItemStack.EMPTY; // already this glint
        }
        ItemStack result = base.copyWithCount(1);
        StackData.write(result, StackData.KEY_ARMOR_GLINT, Codec.STRING, glint.id());
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<ArmorGlintRecipe> {

        @Override
        public ArmorGlintRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient template = Ingredient.fromJson(GsonHelper.getNonNull(json, "template"));
            Ingredient addition = Ingredient.fromJson(GsonHelper.getNonNull(json, "addition"));
            return new ArmorGlintRecipe(id, template, addition);
        }

        @Override
        public ArmorGlintRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient template = Ingredient.fromNetwork(buf);
            Ingredient addition = Ingredient.fromNetwork(buf);
            return new ArmorGlintRecipe(id, template, addition);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ArmorGlintRecipe recipe) {
            recipe.template.toNetwork(buf);
            recipe.addition.toNetwork(buf);
        }
    }
}
