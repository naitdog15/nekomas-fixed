package net.greenjab.nekomasfixed.registry.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record ConfigCondition(String option) implements LootItemCondition {

    @Override
    public LootItemConditionType getType() {
        return LootConditionRegistry.CONFIG.get();
    }

    @Override
    public boolean test(LootContext context) {
        return switch (option) {
            case "magmaBreaksToLava" -> !NekomasFixedConfig.SPEC.isLoaded() || NekomasFixedConfig.MAGMA_BREAKS_TO_LAVA.get();
            default -> true;
        };
    }

    public static class ConfigSerializer implements Serializer<ConfigCondition> {
        @Override
        public void serialize(JsonObject json, ConfigCondition c, JsonSerializationContext ctx) {
            json.addProperty("option", c.option());
        }

        @Override
        public ConfigCondition deserialize(JsonObject json, JsonDeserializationContext ctx) {
            return new ConfigCondition(GsonHelper.getAsString(json, "option"));
        }
    }
}
