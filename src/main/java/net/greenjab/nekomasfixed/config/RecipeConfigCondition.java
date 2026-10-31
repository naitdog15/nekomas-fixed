package net.greenjab.nekomasfixed.config;

import com.google.gson.JsonObject;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/** Recipe condition backed by a config switch; unknown options fail open. */
public final class RecipeConfigCondition implements ICondition {
    public static final ResourceLocation NAME = NekomasFixed.id("config");

    private final String option;

    public RecipeConfigCondition(String option) {
        this.option = option;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        if ("dyedBrushRecipes".equals(option)) {
            return !NekomasFixedConfig.SPEC.isLoaded() || NekomasFixedConfig.DYED_BRUSH_RECIPES.get();
        }
        return true;
    }

    public static final class Serializer implements IConditionSerializer<RecipeConfigCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, RecipeConfigCondition value) {
            json.addProperty("option", value.option);
        }

        @Override
        public RecipeConfigCondition read(JsonObject json) {
            return new RecipeConfigCondition(GsonHelper.getAsString(json, "option"));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
