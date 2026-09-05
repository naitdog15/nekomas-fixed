package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.registry.entity.Termite;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireRegistrations;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.registry.registries.EffectRegistry;
import net.greenjab.nekomasfixed.registry.registries.EnchantmentRegistry;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemGroupRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.OtherRegistry;
import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.greenjab.nekomasfixed.registry.registries.RecipeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.greenjab.nekomasfixed.registry.registries.SoundRegistry;
import net.greenjab.nekomasfixed.registry.worldgen.ModWorldGeneration;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * each package supplies its own DeferredRegister statics; this file adds exactly one
 * {@code X.register(modBus)} line per registry, called once from {@link NekomasFixed}'s
 * constructor. a missing line here is a silent registration failure - a new holder class gets its
 * line here, never in any other file.
 */
public final class Registries {
    private Registries() {
    }

    /**
     * Forge's own javadoc says to use Keys#ENTITY_DATA_SERIALIZERS for a DeferredRegister here -
     * never touch {@code EntityDataSerializers.registerSerializer(...)} instead, it assigns ids by
     * insertion order and risks a silent client/server desync.
     */
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, NekomasFixed.NAMESPACE);

    /** {@code Termite.State}'s synched-data serializer; consumed where {@code Termite} defines its synched data. */
    public static final RegistryObject<EntityDataSerializer<Termite.State>> TERMITE_STATE =
            ENTITY_DATA_SERIALIZERS.register("termite_state",
                    () -> EntityDataSerializer.simpleEnum(Termite.State.class));

    public static void registerAll(IEventBus modBus) {
        ENTITY_DATA_SERIALIZERS.register(modBus);

        // registry/registries/**
        BlockRegistry.BLOCKS.register(modBus);
        BlockRegistry.VANILLA_BLOCKS.register(modBus);
        ItemRegistry.ITEMS.register(modBus);
        ItemRegistry.POTIONS.register(modBus);
        BlockEntityTypeRegistry.BLOCK_ENTITY_TYPES.register(modBus);
        EntityTypeRegistry.ENTITY_TYPES.register(modBus);
        EffectRegistry.EFFECTS.register(modBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modBus);
        ParticleRegistry.PARTICLE_TYPES.register(modBus);
        RecipeRegistry.RECIPE_TYPES.register(modBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modBus);
        ScreenHandlerRegistry.MENU_TYPES.register(modBus);
        ItemGroupRegistry.TABS.register(modBus);
        OtherRegistry.SENSOR_TYPES.register(modBus);
        SoundRegistry.SOUND_EVENTS.register(modBus);

        // entities/AI/worldgen
        WildfireRegistrations.MEMORY_MODULE_TYPES.register(modBus);
        WildfireRegistrations.SOUND_EVENTS.register(modBus);
        ModWorldGeneration.FEATURES.register(modBus);
    }
}
