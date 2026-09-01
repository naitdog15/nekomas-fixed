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
import net.greenjab.nekomasfixed.registry.worldgen.ModWorldGeneration;
import net.greenjab.nekomasfixed.util.ModTreeDecorators;
import net.greenjab.nekomasfixed.util.ModTrunkPlacers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The DeferredRegister aggregator. Each package supplies its own
 * DeferredRegister statics inside its own holder classes; this file adds exactly one
 * {@code X.register(modBus)} line per registry, called once from {@link NekomasFixed}'s
 * constructor. A missing line here is the classic silent registration failure, which
 * is why the aggregator is deliberately one file with one owner rather than every package
 * registering itself.
 * <p>
 * Initially, only the custom EntityDataSerializer registry was wired here, since no other
 * package's Forge-converted DeferredRegisters existed yet to fan out. Each package now adds its own
 * one-line {@code X.register(modBus)} call here (never in any other file) as its own cross-package
 * addition.
 */
public final class Registries {
    private Registries() {
    }

    /**
     * §6.5: Forge's own javadoc at {@code ForgeRegistries.java:109} reads, literally, "Use
     * Keys#ENTITY_DATA_SERIALIZERS to create a DeferredRegister." {@code EntityDataSerializers
     * .registerSerializer(...)} (the raw static-list mutation) must never be touched instead — it
     * assigns ids by insertion order and risks a silent client/server desync.
     * <p>
     * Originally zero entries. The one real entry this registry exists for — {@code TERMITE_STATE},
     * wrapping {@code Termite.State} via {@code EntityDataSerializer.simpleEnum(Termite.State
     * .class)} — needed {@code Termite.java}, which was excluded from an earlier restricted compile
     * path. Add it here — not in {@code Termite.java}, not in a new file — once available:
     * <pre>{@code
     * public static final RegistryObject<EntityDataSerializer<Termite.State>> TERMITE_STATE =
     *     ENTITY_DATA_SERIALIZERS.register("termite_state",
     *         () -> EntityDataSerializer.simpleEnum(Termite.State.class));
     * }</pre>
     */
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, NekomasFixed.NAMESPACE);

    /**
     * The one real entry this registry exists for: {@code Termite.STATE}'s synched-data serializer.
     * Registered here (never in {@code Termite.java}) per this class's one-owner rule; consumed by
     * {@code Termite.java:146}.
     */
    public static final RegistryObject<EntityDataSerializer<Termite.State>> TERMITE_STATE =
            ENTITY_DATA_SERIALIZERS.register("termite_state",
                    () -> EntityDataSerializer.simpleEnum(Termite.State.class));

    /**
     * One line per DeferredRegister in the mod — 20 total,
     * enumerated mechanically from {@code grep -r "DeferredRegister<...> X ="} over src/main/java so
     * that a holder class cannot be silently left unregistered.
     */
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

        // entities/AI/worldgen
        WildfireRegistrations.MEMORY_MODULE_TYPES.register(modBus);
        WildfireRegistrations.SOUND_EVENTS.register(modBus);
        ModWorldGeneration.FEATURES.register(modBus);
        ModTreeDecorators.TREE_DECORATOR_TYPES.register(modBus);
        ModTrunkPlacers.TRUNK_PLACER_TYPES.register(modBus);
    }
}
