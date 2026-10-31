package net.greenjab.nekomasfixed.registry.loottable;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class LootConditionRegistry {
    private LootConditionRegistry() {
    }

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.LOOT_CONDITION_TYPE, NekomasFixed.NAMESPACE);

    public static final RegistryObject<LootItemConditionType> CONFIG =
            LOOT_CONDITIONS.register("config", () -> new LootItemConditionType(new ConfigCondition.ConfigSerializer()));
}
