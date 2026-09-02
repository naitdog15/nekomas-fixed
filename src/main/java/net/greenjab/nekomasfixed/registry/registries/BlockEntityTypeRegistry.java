package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * FabricBlockEntityTypeBuilder -&gt; vanilla BlockEntityType.Builder.of(factory, Block...), wrapped
 * in a DeferredRegister supplier. Block RegistryObjects are dereferenced with .get() only inside
 * each entry's own supplier lambda (Forge resolves RegisterEvent&lt;Block&gt; before
 * RegisterEvent&lt;BlockEntityType&gt;, the same dependency ordering Item relies on for its own
 * Block cross-references - see ItemRegistry.java's javadoc).
 */
public class BlockEntityTypeRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<BlockEntityType<ClamBlockEntity>> CLAM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("clam",
            () -> BlockEntityType.Builder.of(ClamBlockEntity::new,
                    BlockRegistry.CLAM.get(), BlockRegistry.CLAM_BLUE.get(), BlockRegistry.CLAM_PINK.get(), BlockRegistry.CLAM_PURPLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<NautilusBlockEntity>> NAUTILUS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("nautilus",
            () -> BlockEntityType.Builder.of(NautilusBlockEntity::new,
                    BlockRegistry.NAUTILUS_BLOCK.get(), BlockRegistry.ZOMBIE_NAUTILUS_BLOCK.get(), BlockRegistry.CORAL_NAUTILUS_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ClockBlockEntity>> CLOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("clock",
            () -> BlockEntityType.Builder.of(ClockBlockEntity::new, BlockRegistry.CLOCK.get(), BlockRegistry.WALL_CLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<EndermanHeadBlockEntity>> ENDERMAN_HEAD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("enderman_head",
            () -> BlockEntityType.Builder.of(EndermanHeadBlockEntity::new, BlockRegistry.ENDERMAN_HEAD.get(), BlockRegistry.WALL_ENDERMAN_HEAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<KilnBlockEntity>> KILN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("kiln",
            () -> BlockEntityType.Builder.of(KilnBlockEntity::new, BlockRegistry.KILN.get()).build(null));

    public static final RegistryObject<BlockEntityType<TermitehiveBlockEntity>> TERMITE_HIVE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("termite_hive",
            () -> BlockEntityType.Builder.of(TermitehiveBlockEntity::new, BlockRegistry.TERMITE_HIVE.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoupCauldronBlockEntity>> SOUP_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("soup_cauldron",
            () -> BlockEntityType.Builder.of(SoupCauldronBlockEntity::new, BlockRegistry.SOUP_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<HollowLogBlockEntity>> HOLLOW_LOG_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("hollow_log",
            () -> BlockEntityType.Builder.of(HollowLogBlockEntity::new,
                    BlockRegistry.HOLLOW_OAK_LOG.get(),
                    BlockRegistry.HOLLOW_SPRUCE_LOG.get(),
                    BlockRegistry.HOLLOW_BIRCH_LOG.get(),
                    BlockRegistry.HOLLOW_JUNGLE_LOG.get(),
                    BlockRegistry.HOLLOW_ACACIA_LOG.get(),
                    BlockRegistry.HOLLOW_DARK_OAK_LOG.get(),
                    BlockRegistry.HOLLOW_MANGROVE_LOG.get(),
                    BlockRegistry.HOLLOW_CHERRY_LOG.get(),
                    BlockRegistry.HOLLOW_PALE_OAK_LOG.get(),
                    BlockRegistry.HOLLOW_BAMBOO_BLOCK.get(),
                    BlockRegistry.HOLLOW_CRIMSON_STEM.get(),
                    BlockRegistry.HOLLOW_WARPED_STEM.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<StackedCakeBlockEntity>> STACKED_CAKE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("cake",
            () -> BlockEntityType.Builder.of(StackedCakeBlockEntity::new,
                    BlockRegistry.SWEETBERRY_CAKE.get(),
                    BlockRegistry.PAN_CAKE.get(),
                    BlockRegistry.GLOWBERRY_CAKE.get(),
                    BlockRegistry.APPLE_CAKE.get(),
                    BlockRegistry.VANILLA_CAKE.get(),
                    BlockRegistry.COOKIE_CAKE.get(),
                    BlockRegistry.CHOCOLATE_CAKE.get(),
                    BlockRegistry.BEETROOT_CAKE.get()
            ).build(null));
}
