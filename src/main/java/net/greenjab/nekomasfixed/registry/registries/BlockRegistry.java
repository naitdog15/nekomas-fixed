package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.block.*;
import net.greenjab.nekomasfixed.registry.block.cauldron.*;
import net.greenjab.nekomasfixed.registry.block.enums.ClamType;
import net.greenjab.nekomasfixed.registry.block.enums.NautilusBlockType;
import net.greenjab.nekomasfixed.registry.worldgen.ModConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wholesale DeferredRegister conversion.
 * RegistryObject fields keep every original name exactly; call sites elsewhere gain `.get()`.
 * Two DeferredRegisters: {@link #BLOCKS} (nekomasfixed namespace) and {@link #VANILLA_BLOCKS}
 * (CLOCK/WALL_CLOCK stay minecraft: so existing worlds' placed clocks are not orphaned).
 * <p>
 * Vanilla's own {@code Blocks.leaves(...)}/{@code Blocks.woodenButton(...)}/{@code Blocks.shulkerBox(...)}
 * property recipes are private, as are {@code Blocks.never}/{@code Blocks.ocelotOrParrot}; the
 * equivalents are spelled out in this file's helpers instead of reached for through an access
 * transformer, so the properties stay readable next to the blocks that use them.
 */
public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NekomasFixed.NAMESPACE);
    public static final DeferredRegister<Block> VANILLA_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");

    public static final RegistryObject<Block> CLAM = register("clam", settings -> new ClamBlock(ClamType.REGULAR, settings),
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> CLAM_BLUE = register("clam_blue", settings -> new ClamBlock(ClamType.BLUE, settings),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> CLAM_PINK = register("clam_pink", settings -> new ClamBlock(ClamType.PINK, settings),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> CLAM_PURPLE = register("clam_purple", settings -> new ClamBlock(ClamType.PURPLE, settings),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> PEARL_BLOCK = register("pearl_block", BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.CALCITE).requiresCorrectToolForDrops().strength(0.75F));

    public static final RegistryObject<Block> NAUTILUS_BLOCK = register("nautilus_block", settings -> new NautilusBlock(NautilusBlockType.REGULAR, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1F).sound(SoundType.CORAL_BLOCK).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> ZOMBIE_NAUTILUS_BLOCK = register("zombie_nautilus_block", settings -> new NautilusBlock(NautilusBlockType.ZOMBIE, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1F).sound(SoundType.CORAL_BLOCK).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> CORAL_NAUTILUS_BLOCK = register("coral_nautilus_block",settings -> new NautilusBlock(NautilusBlockType.CORAL, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1F).sound(SoundType.CORAL_BLOCK).pushReaction(PushReaction.DESTROY));
    // Fully qualified: this file wildcard-imports both net.minecraft.world.level.block and the mod's
    // own registry.block package, and MelonBlock exists in both.
    public static final RegistryObject<Block> GLISTERING_MELON = register("glistering_melon", settings -> new net.greenjab.nekomasfixed.registry.block.MelonBlock(true, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> GEYSER = register("geyser", GeyserBlock::new , BlockBehaviour.Properties.of().randomTicks().strength(0.5f, 0.5f).lightLevel(ignored -> 15));
    public static final RegistryObject<Block> KILN = register("kiln", KilnBlock::new,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.GILDED_BLACKSTONE).requiresCorrectToolForDrops().strength(3.5f));
    public static final RegistryObject<Block> PYROTECHNICS_TABLE = register("pyrotechnics_table", PyrotechnicsTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
    public static final RegistryObject<Block> ENDERMAN_HEAD = register("enderman_head", FloorEndermanHeadHead::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY).instrument(NoteBlockInstrument.CUSTOM_HEAD));
    // copyLootTable(...) reads ENDERMAN_HEAD.get() (etc.) internally, so - same as the walls/stairs
    // above - the whole declaration must build inside one deferred lambda, not the eager 3-arg form.
    public static final RegistryObject<Block> WALL_ENDERMAN_HEAD = register("wall_enderman_head",
            () -> new WallEndermanHeadHead(copyLootTable(ENDERMAN_HEAD).mapColor(MapColor.COLOR_BLACK).strength(1F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> GLOW_TORCH = register(
            "glow_torch",
            GlowTorchBlock::new,
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> state.getValue(BlockStateProperties.WATERLOGGED) ? 13 : 0)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final RegistryObject<Block> GLOW_WALL_TORCH = register(
            "glow_wall_torch",
            () -> new WallGlowTorchBlock(
                    copyLootTable(GLOW_TORCH)
                            .noCollission()
                            .instabreak()
                            .lightLevel(state -> state.getValue(BlockStateProperties.WATERLOGGED) ? 13 : 0)
                            .sound(SoundType.WOOD)
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final RegistryObject<Block> SWEETBERRY_CAKE = register("sweetberry_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> PAN_CAKE = register("pan_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> GLOWBERRY_CAKE = register("glowberry_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> APPLE_CAKE = register("apple_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> VANILLA_CAKE = register("vanilla_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> COOKIE_CAKE = register("cookie_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> CHOCOLATE_CAKE = register("chocolate_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));
    public static final RegistryObject<Block> BEETROOT_CAKE = register("beetroot_cake", StackedCakeBlock::new, BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(StackedCakeBlock.LIT)?3:0));

    static BlockSetType BAOBAB_BLOCKSETTYPE = BlockSetType.register(new BlockSetType("baobab"));
    static WoodType BAOBAB_WOODTYPE = WoodType.register(new WoodType("baobab", BAOBAB_BLOCKSETTYPE));
    // 1.20.1's sapling growth hook is an AbstractTreeGrower subclass handing back a configured-feature
    // key; the level looks the key up in its own registry when the sapling actually grows, so holding
    // the plain ResourceKey here is safe at class-init time.
    static final AbstractTreeGrower BAOBAB_TREE_GROWER = new AbstractTreeGrower() {
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
            return ModConfiguredFeatures.BAOBAB_KEY;
        }
    };
    // StrippableBlockRegistry.register(...) / FireBlock.setFlammable(...) have
    // no Forge equivalent (both mod-block-only, so both stay entirely inside these block classes -
    // no AT, no map mutation, no mixin, no enqueueWork). Pre-existing source quirk preserved
    // verbatim per parity: the Fabric registerBlocks() set STRIPPED_BAOBAB_LOG's flammability twice
    // and never set STRIPPED_BAOBAB_WOOD's (5,5) - so stripped_baobab_wood stays non-flammable here
    // too, exactly as it always has been.
    // NOTE: `STRIPPED_BAOBAB_LOG::get` (a bound instance method reference) would capture that
    // field's value - still null - at THIS lambda's creation time and NPE. `() -> ...get()` instead
    // reads the static field lazily, when the supplier is actually invoked (well after all
    // RegistryObjects exist). The class-qualified form is required: a *simple* name would be an
    // illegal forward reference to a field declared further down this same class.
    public static final RegistryObject<Block> BAOBAB_LOG = register("baobab_log",
            settings -> new FlammableRotatedPillarBlock(settings, 5, 5, () -> BlockRegistry.STRIPPED_BAOBAB_LOG.get()),
            BlockBehaviour.Properties.copy(Blocks.OAK_LOG).mapColor(MapColor.WOOD));
    public static final RegistryObject<Block> BAOBAB_WOOD = register("baobab_wood",
            settings -> new FlammableRotatedPillarBlock(settings, 5, 5, () -> BlockRegistry.STRIPPED_BAOBAB_WOOD.get()),
            BlockBehaviour.Properties.copy(Blocks.OAK_WOOD));
    public static final RegistryObject<Block> STRIPPED_BAOBAB_LOG = register(
            "stripped_baobab_log", settings -> new FlammableRotatedPillarBlock(settings, 5, 5, null),
            BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG));
    public static final RegistryObject<Block> STRIPPED_BAOBAB_WOOD = register(
            "stripped_baobab_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD));
    public static final RegistryObject<Block> BAOBAB_PLANKS = register("baobab_planks",
            settings -> new FlammableBlock(settings, 5, 20), BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
    public static final RegistryObject<Block> BAOBAB_STAIRS = register("baobab_stairs",
            () -> new FlammableStairBlock(BAOBAB_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BAOBAB_PLANKS.get()), 5, 20));
    public static final RegistryObject<Block> BAOBAB_SLAB = register(
            "baobab_slab",
            settings -> new FlammableSlabBlock(settings, 5, 20),
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_FENCE = register(
            "baobab_fence",
            settings -> new FlammableFenceBlock(settings, 5, 20),
            BlockBehaviour.Properties.of()
                    .mapColor(state -> BAOBAB_PLANKS.get().defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_FENCE_GATE = register(
            "baobab_fence_gate",
            settings -> new FlammableFenceGateBlock(BAOBAB_WOODTYPE, settings, 5, 20),
            BlockBehaviour.Properties.of().mapColor(state -> BAOBAB_PLANKS.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_DOOR = register(
            "baobab_door",
            settings -> new DoorBlock(settings, BAOBAB_BLOCKSETTYPE),
            BlockBehaviour.Properties.of()
                    .mapColor(state -> BAOBAB_PLANKS.get().defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_TRAPDOOR = register(
            "baobab_trapdoor",
            settings -> new TrapDoorBlock(settings, BAOBAB_BLOCKSETTYPE),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .noOcclusion()
                    .isValidSpawn(BlockRegistry::neverSpawn)
                    .sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_PRESSURE_PLATE = register(
            "baobab_pressure_plate",
            settings -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, settings, BAOBAB_BLOCKSETTYPE),
            BlockBehaviour.Properties.of()
                    .mapColor(state -> BAOBAB_PLANKS.get().defaultMapColor())
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(0.5F)
                    .pushReaction(PushReaction.DESTROY).ignitedByLava()
    );
    // Wooden buttons on 1.20.1: 30-tick press, arrows can press them (vanilla's woodenButton recipe).
    public static final RegistryObject<Block> BAOBAB_BUTTON = register(
            "baobab_button",settings -> new ButtonBlock(settings, BAOBAB_BLOCKSETTYPE, 30, true), woodenButtonProperties().ignitedByLava()
    );
    // Leaves particles are a 1.21.4+ block type; 1.20.1's leaves are the plain LeavesBlock, which
    // drops the falling-leaf particle and its tint colour with nothing to attach them to.
    public static final RegistryObject<Block> BAOBAB_LEAVES = register("baobab_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
    public static final RegistryObject<Block> BAOBAB_SAPLING = register("baobab_sapling",(settings) -> new SaplingBlock(BAOBAB_TREE_GROWER,  settings), BlockBehaviour.Properties.copy(Blocks.DARK_OAK_SAPLING));
    public static final RegistryObject<Block> BAOBAB_FRUIT = register("baobab_fruit", BaobabFruitBlock::new, BlockBehaviour.Properties.of().randomTicks().strength(0.2f).isViewBlocking(BlockRegistry::never).ignitedByLava().instabreak());
    public static final RegistryObject<Block> ROPE = register("rope", RopeBlock::new, BlockBehaviour.Properties.of().strength(0.2f).isRedstoneConductor(BlockRegistry::never).ignitedByLava().noCollission());
    public static final RegistryObject<Block> BAOBAB_SHELF = register(
            "baobab_shelf",
            BaobabShelfBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(state -> BAOBAB_PLANKS.get().defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2f,3.0F)
                    .sound(SoundType.WOOD).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_SIGN = register(
            "baobab_sign",
            settings -> new StandingSignBlock(settings, BAOBAB_WOODTYPE),
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_WALL_SIGN = register(
            "baobab_wall_sign",
            () -> new WallSignBlock(
                    copyLootTable(BAOBAB_SIGN).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava(),
                    BAOBAB_WOODTYPE)
    );
    public static final RegistryObject<Block> BAOBAB_HANGING_SIGN = register(
            "baobab_hanging_sign",
            settings -> new CeilingHangingSignBlock(settings, BAOBAB_WOODTYPE),
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava()
    );
    public static final RegistryObject<Block> BAOBAB_WALL_HANGING_SIGN = register(
            "baobab_wall_hanging_sign",
            () -> new WallHangingSignBlock(
                    copyLootTable(BAOBAB_HANGING_SIGN).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava(),
                    BAOBAB_WOODTYPE)
    );

    public static final RegistryObject<Block> TERMITE_BLOCK = register("termite_block", BlockBehaviour.Properties.of().strength(1f));
    public static final RegistryObject<Block> TERMITE_HIVE = register("termite_hive", TermitehiveBlock::new, BlockBehaviour.Properties.of().strength(1f));
    public static final RegistryObject<Block> HOLLOW_OAK_LOG = register("hollow_oak_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.OAK_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_SPRUCE_LOG = register("hollow_spruce_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_BIRCH_LOG = register("hollow_birch_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.BIRCH_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_JUNGLE_LOG = register("hollow_jungle_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.JUNGLE_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_ACACIA_LOG = register("hollow_acacia_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_DARK_OAK_LOG = register("hollow_dark_oak_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.DARK_OAK_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_MANGROVE_LOG = register("hollow_mangrove_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.MANGROVE_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_CHERRY_LOG = register("hollow_cherry_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.CHERRY_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    // Pale oak arrives with 1.21.4; the block keeps its id, item and textures and borrows dark oak's
    // material properties so worlds and recipes referencing it still work on 1.20.1.
    public static final RegistryObject<Block> HOLLOW_PALE_OAK_LOG = register("hollow_pale_oak_log", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.DARK_OAK_LOG).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_BAMBOO_BLOCK = register("hollow_bamboo_block", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.BAMBOO_BLOCK).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_CRIMSON_STEM = register("hollow_crimson_stem", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.CRIMSON_HYPHAE).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_WARPED_STEM = register("hollow_warped_stem", HollowLogBlock::new , BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL)));
    public static final RegistryObject<Block> HOLLOW_BAOBAB_LOG = register("hollow_baobab_log", () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BAOBAB_LOG.get()).lightLevel(state -> state.getValue(HollowLogBlock.LIGHT_LEVEL))));


    public static final RegistryObject<Block> GOAT_HORN = register("horn", GoatHornBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).lightLevel(state -> state.getValue(GoatHornBlock.TORCH).getLight()).strength(0.2F).sound(SoundType.TUFF).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> CLOCK = registerVanilla("clock", FloorClockBlock::new, BlockBehaviour.Properties.of().noCollission().mapColor(MapColor.COLOR_YELLOW).strength(0.2F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY));
    public static final RegistryObject<Block> WALL_CLOCK = registerVanilla("wall_clock",
            () -> new WallClockBlock(copyLootTable(CLOCK).noCollission().mapColor(MapColor.COLOR_YELLOW).strength(0.2F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> HONEY_CAULDRON = register("honey_cauldron", HoneyCauldronBlock::new, BlockBehaviour.Properties.copy(Blocks.CAULDRON));
    public static final RegistryObject<Block> MAGMA_CAULDRON = register("magma_cauldron", MagmaCauldronBlock::new, BlockBehaviour.Properties.copy(Blocks.CAULDRON));
    public static final RegistryObject<Block> SLIME_CAULDRON = register("slime_cauldron", SlimeCauldronBlock::new, BlockBehaviour.Properties.copy(Blocks.CAULDRON));
    public static final RegistryObject<Block> ICE_CAULDRON = register("ice_cauldron", IceCauldronBlock::new, BlockBehaviour.Properties.copy(Blocks.CAULDRON));
    public static final RegistryObject<Block> SOUP_CAULDRON = register("soup_cauldron", SoupCauldronBlock::new, BlockBehaviour.Properties.copy(Blocks.CAULDRON));




    public static final RegistryObject<Block> AMBER_WOOL = register("amber_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AQUA_WOOL = register("aqua_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> INDIGO_WOOL = register("indigo_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAROON_WOOL = register("maroon_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AMBER_CARPET = register("amber_carpet", (settings) -> new WoolCarpetBlock(DyeColor.YELLOW, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AQUA_CARPET = register("aqua_carpet", (settings) -> new WoolCarpetBlock(DyeColor.LIGHT_BLUE, settings),BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> INDIGO_CARPET = register("indigo_carpet", (settings) -> new WoolCarpetBlock(DyeColor.MAGENTA, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAROON_CARPET = register("maroon_carpet", (settings) -> new WoolCarpetBlock(DyeColor.RED, settings), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());

    public static final RegistryObject<Block> AMBER_TERRACOTTA = register("amber_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).strength(0.70F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> AQUA_TERRACOTTA = register("aqua_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.70F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> INDIGO_TERRACOTTA = register("indigo_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.70F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> MAROON_TERRACOTTA = register("maroon_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).strength(0.70F).explosionResistance(4.2F).requiresCorrectToolForDrops());

    public static final RegistryObject<Block> AMBER_CONCRETE = register("amber_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
    public static final RegistryObject<Block> AQUA_CONCRETE = register("aqua_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
    public static final RegistryObject<Block> INDIGO_CONCRETE = register("indigo_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
    public static final RegistryObject<Block> MAROON_CONCRETE = register("maroon_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
    public static final RegistryObject<Block> AMBER_CONCRETE_POWDER = register("amber_concrete_powder", (settings) -> new ConcretePowderBlock(AMBER_CONCRETE.get(), settings), BlockBehaviour.Properties.of().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
    public static final RegistryObject<Block> AQUA_CONCRETE_POWDER = register("aqua_concrete_powder", (settings) -> new ConcretePowderBlock(AQUA_CONCRETE.get(), settings), BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
    public static final RegistryObject<Block> MAROON_CONCRETE_POWDER = register("maroon_concrete_powder", (settings) -> new ConcretePowderBlock(MAROON_CONCRETE.get(), settings), BlockBehaviour.Properties.of().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
    public static final RegistryObject<Block> INDIGO_CONCRETE_POWDER = register("indigo_concrete_powder", (settings) -> new ConcretePowderBlock(INDIGO_CONCRETE.get(), settings), BlockBehaviour.Properties.of().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));

    public static final RegistryObject<Block> AMBER_GLAZED_TERRACOTTA = register("amber_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.4F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> AQUA_GLAZED_TERRACOTTA = register("aqua_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.4F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> INDIGO_GLAZED_TERRACOTTA = register("indigo_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).strength(1.4F).explosionResistance(4.2F).requiresCorrectToolForDrops());
    public static final RegistryObject<Block> MAROON_GLAZED_TERRACOTTA = register("maroon_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).strength(1.4F).explosionResistance(4.2F).requiresCorrectToolForDrops());

    public static final RegistryObject<Block> AMBER_STAINED_GLASS = registerStainedGlassBlock("amber_stained_glass", DyeColor.YELLOW);
    public static final RegistryObject<Block> AQUA_STAINED_GLASS = registerStainedGlassBlock("aqua_stained_glass", DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Block> INDIGO_STAINED_GLASS = registerStainedGlassBlock("indigo_stained_glass", DyeColor.MAGENTA);
    public static final RegistryObject<Block> MAROON_STAINED_GLASS = registerStainedGlassBlock("maroon_stained_glass", DyeColor.RED);
    public static final RegistryObject<Block> AMBER_STAINED_GLASS_PANE = registerStainedGlassPaneBlock("amber_stained_glass_pane", DyeColor.YELLOW);
    public static final RegistryObject<Block> AQUA_STAINED_GLASS_PANE = registerStainedGlassPaneBlock("aqua_stained_glass_pane", DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Block> INDIGO_STAINED_GLASS_PANE = registerStainedGlassPaneBlock("indigo_stained_glass_pane", DyeColor.MAGENTA);
    public static final RegistryObject<Block> MAROON_STAINED_GLASS_PANE = registerStainedGlassPaneBlock("maroon_stained_glass_pane", DyeColor.RED);

    public static final RegistryObject<Block> AMBER_SHULKER_BOX = registerShulkerBoxBlock("amber_shulker_box", DyeColor.YELLOW);
    public static final RegistryObject<Block> AQUA_SHULKER_BOX = registerShulkerBoxBlock("aqua_shulker_box", DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Block> INDIGO_SHULKER_BOX = registerShulkerBoxBlock("indigo_shulker_box", DyeColor.MAGENTA);
    public static final RegistryObject<Block> MAROON_SHULKER_BOX = registerShulkerBoxBlock("maroon_shulker_box", DyeColor.RED);

    public static final RegistryObject<Block> AMBER_BED = registerBedBlock("amber_bed", DyeColor.YELLOW);
    public static final RegistryObject<Block> AQUA_BED = registerBedBlock("aqua_bed", DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Block> INDIGO_BED = registerBedBlock("indigo_bed", DyeColor.MAGENTA);
    public static final RegistryObject<Block> MAROON_BED = registerBedBlock("maroon_bed", DyeColor.RED);

    public static final RegistryObject<Block> AMBER_CANDLE = register("amber_candle", CandleBlock::new, createCandleSettings(MapColor.COLOR_YELLOW));
    public static final RegistryObject<Block> AQUA_CANDLE = register("aqua_candle", CandleBlock::new, createCandleSettings(MapColor.WARPED_NYLIUM));
    public static final RegistryObject<Block> INDIGO_CANDLE = register("indigo_candle", CandleBlock::new, createCandleSettings(MapColor.ICE));
    public static final RegistryObject<Block> MAROON_CANDLE = register("maroon_candle", CandleBlock::new, createCandleSettings(MapColor.CRIMSON_HYPHAE));


    public static final RegistryObject<Block> WHITE_BRICKS = register("white_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.WHITE));
    public static final RegistryObject<Block> ORANGE_BRICKS = register("orange_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.ORANGE));
    public static final RegistryObject<Block> MAGENTA_BRICKS = register("magenta_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.MAGENTA));
    public static final RegistryObject<Block> LIGHT_BLUE_BRICKS = register("light_blue_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<Block> YELLOW_BRICKS = register("yellow_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.YELLOW));
    public static final RegistryObject<Block> LIME_BRICKS = register("lime_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.LIME));
    public static final RegistryObject<Block> PINK_BRICKS = register("pink_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.PINK));
    public static final RegistryObject<Block> GRAY_BRICKS = register("gray_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.GRAY));
    public static final RegistryObject<Block> LIGHT_GRAY_BRICKS = register("light_gray_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<Block> CYAN_BRICKS = register("cyan_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.CYAN));
    public static final RegistryObject<Block> PURPLE_BRICKS = register("purple_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.PURPLE));
    public static final RegistryObject<Block> BLUE_BRICKS = register("blue_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.BLUE));
    public static final RegistryObject<Block> BROWN_BRICKS = register("brown_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.BROWN));
    public static final RegistryObject<Block> GREEN_BRICKS = register("green_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.GREEN));
    public static final RegistryObject<Block> RED_BRICKS = register("red_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.RED));
    public static final RegistryObject<Block> BLACK_BRICKS = register("black_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.BLACK));
    public static final RegistryObject<Block> AMBER_BRICKS = register("amber_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.YELLOW));
    public static final RegistryObject<Block> AQUA_BRICKS = register("aqua_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<Block> INDIGO_BRICKS = register("indigo_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.MAGENTA));
    public static final RegistryObject<Block> MAROON_BRICKS = register("maroon_bricks", BlockBehaviour.Properties.copy(Blocks.BRICKS).mapColor(DyeColor.RED));

    public static final RegistryObject<Block> WHITE_BRICK_SLAB = register("white_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.WHITE));
    public static final RegistryObject<Block> LIGHT_GRAY_BRICK_SLAB = register("light_gray_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<Block> GRAY_BRICK_SLAB = register("gray_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.GRAY));
    public static final RegistryObject<Block> BLACK_BRICK_SLAB = register("black_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.BLACK));
    public static final RegistryObject<Block> BROWN_BRICK_SLAB = register("brown_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.BROWN));
    public static final RegistryObject<Block> RED_BRICK_SLAB = register("red_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.RED));
    public static final RegistryObject<Block> ORANGE_BRICK_SLAB = register("orange_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.ORANGE));
    public static final RegistryObject<Block> YELLOW_BRICK_SLAB = register("yellow_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.YELLOW));
    public static final RegistryObject<Block> LIME_BRICK_SLAB = register("lime_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.LIME));
    public static final RegistryObject<Block> GREEN_BRICK_SLAB = register("green_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.GREEN));
    public static final RegistryObject<Block> CYAN_BRICK_SLAB = register("cyan_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.CYAN));
    public static final RegistryObject<Block> LIGHT_BLUE_BRICK_SLAB = register("light_blue_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<Block> BLUE_BRICK_SLAB = register("blue_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.BLUE));
    public static final RegistryObject<Block> PURPLE_BRICK_SLAB = register("purple_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.PURPLE));
    public static final RegistryObject<Block> MAGENTA_BRICK_SLAB = register("magenta_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.MAGENTA));
    public static final RegistryObject<Block> PINK_BRICK_SLAB = register("pink_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.PINK));
    public static final RegistryObject<Block> AMBER_BRICK_SLAB = register("amber_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.YELLOW));
    public static final RegistryObject<Block> AQUA_BRICK_SLAB = register("aqua_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<Block> INDIGO_BRICK_SLAB = register("indigo_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.MAGENTA));
    public static final RegistryObject<Block> MAROON_BRICK_SLAB = register("maroon_brick_slab", SlabBlock::new, BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).mapColor(DyeColor.RED));

    public static final RegistryObject<Block> WHITE_BRICK_STAIRS = registerOldStairsBlock("white_brick_stairs", WHITE_BRICKS);
    public static final RegistryObject<Block> LIGHT_GRAY_BRICK_STAIRS = registerOldStairsBlock("light_gray_brick_stairs", LIGHT_GRAY_BRICKS);
    public static final RegistryObject<Block> GRAY_BRICK_STAIRS = registerOldStairsBlock("gray_brick_stairs", GRAY_BRICKS);
    public static final RegistryObject<Block> BLACK_BRICK_STAIRS = registerOldStairsBlock("black_brick_stairs", BLACK_BRICKS);
    public static final RegistryObject<Block> BROWN_BRICK_STAIRS = registerOldStairsBlock("brown_brick_stairs", BROWN_BRICKS);
    public static final RegistryObject<Block> RED_BRICK_STAIRS = registerOldStairsBlock("red_brick_stairs", RED_BRICKS);
    public static final RegistryObject<Block> ORANGE_BRICK_STAIRS = registerOldStairsBlock("orange_brick_stairs", ORANGE_BRICKS);
    public static final RegistryObject<Block> YELLOW_BRICK_STAIRS = registerOldStairsBlock("yellow_brick_stairs", YELLOW_BRICKS);
    public static final RegistryObject<Block> LIME_BRICK_STAIRS = registerOldStairsBlock("lime_brick_stairs", LIME_BRICKS);
    public static final RegistryObject<Block> GREEN_BRICK_STAIRS = registerOldStairsBlock("green_brick_stairs", GREEN_BRICKS);
    public static final RegistryObject<Block> CYAN_BRICK_STAIRS = registerOldStairsBlock("cyan_brick_stairs", CYAN_BRICKS);
    public static final RegistryObject<Block> LIGHT_BLUE_BRICK_STAIRS = registerOldStairsBlock("light_blue_brick_stairs", LIGHT_BLUE_BRICKS);
    public static final RegistryObject<Block> BLUE_BRICK_STAIRS = registerOldStairsBlock("blue_brick_stairs", BLUE_BRICKS);
    public static final RegistryObject<Block> PURPLE_BRICK_STAIRS = registerOldStairsBlock("purple_brick_stairs", PURPLE_BRICKS);
    public static final RegistryObject<Block> MAGENTA_BRICK_STAIRS = registerOldStairsBlock("magenta_brick_stairs", MAGENTA_BRICKS);
    public static final RegistryObject<Block> PINK_BRICK_STAIRS = registerOldStairsBlock("pink_brick_stairs", PINK_BRICKS);
    public static final RegistryObject<Block> AMBER_BRICK_STAIRS = registerOldStairsBlock("amber_brick_stairs", AMBER_BRICKS);
    public static final RegistryObject<Block> AQUA_BRICK_STAIRS = registerOldStairsBlock("aqua_brick_stairs", AQUA_BRICKS);
    public static final RegistryObject<Block> INDIGO_BRICK_STAIRS = registerOldStairsBlock("indigo_brick_stairs", INDIGO_BRICKS);
    public static final RegistryObject<Block> MAROON_BRICK_STAIRS = registerOldStairsBlock("maroon_brick_stairs", MAROON_BRICKS);
    
    public static final RegistryObject<Block> WHITE_BRICK_WALL = register("white_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(WHITE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> LIGHT_GRAY_BRICK_WALL = register("light_gray_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> GRAY_BRICK_WALL = register("gray_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(GRAY_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> BLACK_BRICK_WALL = register("black_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(BLACK_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> BROWN_BRICK_WALL = register("brown_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(BROWN_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> RED_BRICK_WALL = register("red_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(RED_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> ORANGE_BRICK_WALL = register("orange_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(ORANGE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> YELLOW_BRICK_WALL = register("yellow_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(YELLOW_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> LIME_BRICK_WALL = register("lime_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(LIME_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> GREEN_BRICK_WALL = register("green_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(GREEN_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> CYAN_BRICK_WALL = register("cyan_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(CYAN_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> LIGHT_BLUE_BRICK_WALL = register("light_blue_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> BLUE_BRICK_WALL = register("blue_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(BLUE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> PURPLE_BRICK_WALL = register("purple_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(PURPLE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> MAGENTA_BRICK_WALL = register("magenta_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MAGENTA_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> PINK_BRICK_WALL = register("pink_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(PINK_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> AMBER_BRICK_WALL = register("amber_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(AMBER_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> AQUA_BRICK_WALL = register("aqua_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(AQUA_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> INDIGO_BRICK_WALL = register("indigo_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(INDIGO_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> MAROON_BRICK_WALL = register("maroon_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MAROON_BRICKS.get()).forceSolidOn()));

    public static final RegistryObject<Block> CLEAR_FROGLIGHT = register("clear_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> CLOUDY_FROGLIGHT = register("cloudy_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> CASCADING_FROGLIGHT = register("cascading_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> CLOUDBURST_FROGLIGHT = register("cloudburst_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.3F).lightLevel(ignored -> 10).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> CHAMOISEE_FROGLIGHT = register("chamoisee_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> SANGUINE_FROGLIGHT = register("sanguine_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> VERMILION_FROGLIGHT = register("vermilion_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> MANDARIN_FROGLIGHT = register("mandarin_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> LEMON_FROGLIGHT = register("lemon_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> KIWI_FROGLIGHT = register("kiwi_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> SEAFOAM_FROGLIGHT = register("seafoam_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_NYLIUM).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> TEAL_FROGLIGHT = register("teal_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> CERULEAN_FROGLIGHT = register("cerulean_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> NAVY_FROGLIGHT = register("navy_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> LAVENDER_FROGLIGHT = register("lavender_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_HYPHAE).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> THULIAN_FROGLIGHT = register("thulian_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));
    public static final RegistryObject<Block> SAKURA_FROGLIGHT = register("sakura_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.3F).lightLevel(ignored -> 15).sound(SoundType.FROGLIGHT));

    public static final RegistryObject<Block> WHITE_SPOTTED_WOOL = register("white_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIGHT_GRAY_SPOTTED_WOOL = register("light_gray_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> GRAY_SPOTTED_WOOL = register("gray_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BLACK_SPOTTED_WOOL = register("black_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BROWN_SPOTTED_WOOL = register("brown_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> RED_SPOTTED_WOOL = register("red_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> ORANGE_SPOTTED_WOOL = register("orange_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> YELLOW_SPOTTED_WOOL = register("yellow_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIME_SPOTTED_WOOL = register("lime_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> GREEN_SPOTTED_WOOL = register("green_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> CYAN_SPOTTED_WOOL = register("cyan_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIGHT_BLUE_SPOTTED_WOOL = register("light_blue_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BLUE_SPOTTED_WOOL = register("blue_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> PURPLE_SPOTTED_WOOL = register("purple_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAGENTA_SPOTTED_WOOL = register("magenta_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> PINK_SPOTTED_WOOL = register("pink_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AMBER_SPOTTED_WOOL = register("amber_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AQUA_SPOTTED_WOOL = register("aqua_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> INDIGO_SPOTTED_WOOL = register("indigo_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAROON_SPOTTED_WOOL = register("maroon_spotted_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());

    public static final RegistryObject<Block> WHITE_SPOTTED_CARPET = register("white_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIGHT_GRAY_SPOTTED_CARPET = register("light_gray_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> GRAY_SPOTTED_CARPET = register("gray_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BLACK_SPOTTED_CARPET = register("black_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BROWN_SPOTTED_CARPET = register("brown_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> RED_SPOTTED_CARPET = register("red_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> ORANGE_SPOTTED_CARPET = register("orange_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> YELLOW_SPOTTED_CARPET = register("yellow_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIME_SPOTTED_CARPET = register("lime_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> GREEN_SPOTTED_CARPET = register("green_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> CYAN_SPOTTED_CARPET = register("cyan_spotted_carpet", CarpetBlock::new,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> LIGHT_BLUE_SPOTTED_CARPET = register("light_blue_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> BLUE_SPOTTED_CARPET = register("blue_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> PURPLE_SPOTTED_CARPET = register("purple_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAGENTA_SPOTTED_CARPET = register("magenta_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> PINK_SPOTTED_CARPET = register("pink_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AMBER_SPOTTED_CARPET = register("amber_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> AQUA_SPOTTED_CARPET = register("aqua_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> INDIGO_SPOTTED_CARPET = register("indigo_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> MAROON_SPOTTED_CARPET = register("maroon_spotted_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());


    // --- DeferredRegister helpers. `settings` (arg 3) is evaluated EAGERLY
    // by the caller (field-initializer time, before RegisterEvent fires), so it is safe ONLY when
    // it contains no cross-reference to another RegistryObject in this file - such sites are
    // restructured above onto the single-lambda `register(String, Supplier<Block>)` overload
    // instead, following the rule "never .get() in a field initializer".
    // 1.20.1 BlockBehaviour.Properties has no .setId(key) (that is a 1.20.5+ addition); Forge's
    // DeferredRegister associates the id via the supplier's registration key instead.
    private static RegistryObject<Block> register(String id, BlockBehaviour.Properties settings) {
        return register(id, Block::new, settings);
    }
    private static RegistryObject<Block> register(String id, Function<BlockBehaviour.Properties, ? extends Block> factory, BlockBehaviour.Properties settings) {
        return BLOCKS.register(id, () -> factory.apply(settings));
    }
    private static RegistryObject<Block> register(String id, Supplier<? extends Block> factory) {
        return BLOCKS.register(id, factory::get);
    }
    private static RegistryObject<Block> registerVanilla(String id, Function<BlockBehaviour.Properties, ? extends Block> factory, BlockBehaviour.Properties settings) {
        return VANILLA_BLOCKS.register(id, () -> factory.apply(settings));
    }
    private static RegistryObject<Block> registerVanilla(String id, Supplier<? extends Block> factory) {
        return VANILLA_BLOCKS.register(id, factory::get);
    }

    public static BlockBehaviour.Properties createCandleSettings(MapColor mapColor) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION).pushReaction(PushReaction.DESTROY);
    }
    // Takes a Supplier<Block> (every RegistryObject<Block> IS one) rather than a plain Block so
    // callers never need a bare cross-reference `.get()` at field-initializer time; Forge's
    // lootFrom(Supplier) keeps the dereference lazy all the way to loot-table lookup time.
    // 1.20.1 has no Properties-level translation-key override (that arrives with the 1.20.5
    // component rework), so each wall/floor pair now needs its own lang entry.
    private static BlockBehaviour.Properties copyLootTable(Supplier<? extends Block> block) {
        return BlockBehaviour.Properties.of().lootFrom(block);
    }

    private static RegistryObject<Block> registerStainedGlassBlock(String id, DyeColor color) {
        return register(id, (settings) -> new StainedGlassBlock(color, settings), BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(BlockRegistry::neverSpawn).isRedstoneConductor(BlockRegistry::never).isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never));
    }

    private static RegistryObject<Block> registerStainedGlassPaneBlock(String id, DyeColor color) {
        return register(id, (settings) -> new StainedGlassPaneBlock(color, settings), BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
    }
    // Vanilla's shulkerBox(...) recipe, spelled out: an open box must not suffocate or block sight,
    // and it always conducts redstone.
    private static RegistryObject<Block> registerShulkerBoxBlock(String id, DyeColor color) {
        BlockBehaviour.StatePredicate closed = (state, world, pos) ->
                !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBox) || shulkerBox.isClosed();
        return register(id, settings -> new ShulkerBoxBlock(color, settings),
                BlockBehaviour.Properties.of().mapColor(color.getMapColor()).forceSolidOn().strength(2.0F)
                        .dynamicShape().noOcclusion().isSuffocating(closed).isViewBlocking(closed)
                        .pushReaction(PushReaction.DESTROY).isRedstoneConductor(BlockRegistry::always));
    }
    // Supplier<Block>, not Block - see copyLootTable's javadoc note; base.get() only runs inside the
    // deferred lambda BLOCKS.register(...) stores, well after the base block is itself registered.
    private static RegistryObject<Block> registerOldStairsBlock(String id, Supplier<? extends Block> base) {
        return register(id, () -> new StairBlock(base.get().defaultBlockState(), BlockBehaviour.Properties.copy(base.get())));
    }
    public static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }
    private static boolean always(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }
    private static boolean neverSpawn(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return false;
    }
    private static boolean ocelotOrParrot(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }

    /** Vanilla's private Blocks.leaves(SoundType) recipe. */
    private static BlockBehaviour.Properties leavesProperties(SoundType sound) {
        return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(sound)
                .noOcclusion().isValidSpawn(BlockRegistry::ocelotOrParrot).isSuffocating(BlockRegistry::never)
                .isViewBlocking(BlockRegistry::never).ignitedByLava().pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(BlockRegistry::never);
    }

    /** Vanilla's private Blocks.woodenButton(...) property recipe. */
    private static BlockBehaviour.Properties woodenButtonProperties() {
        return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
    }

    // No bounce-restitution property on 1.20.1: BedBlock.bounceUp() hard-codes the 0.66 factor, so
    // these beds already bounce exactly like vanilla ones without declaring it.
    private static RegistryObject<Block> registerBedBlock(String id, DyeColor color) {
        return register(id,
                settings -> new BedBlock(color, settings),
                BlockBehaviour.Properties.of()
                        .mapColor(state -> state.getValue(BedBlock.PART) == BedPart.FOOT
                                ? color.getMapColor()
                                : MapColor.WOOL)
                        .sound(SoundType.WOOD)
                        .strength(0.2F)
                        .noOcclusion()
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY)
        );
    }

    // registerBlocks()/registerBlockEntityType()-style no-op entrypoint methods are gone: no file
    // in the repo called this one (grepped repo-wide), and DeferredRegister's own RegisterEvent
    // dispatch (BLOCKS.register(modBus) from the aggregator, Registries.java) is what used to
    // need this call site. The 5 cauldron RegistryObjects and the baobab-set flammability/stripping
    // (formerly this method's body) both moved up into ordinary field declarations above.
}
