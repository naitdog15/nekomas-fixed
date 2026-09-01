package net.greenjab.nekomasfixed.datagen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** {@code FabricTagsProvider.BlockTagsProvider} → Forge's
 * {@code net.minecraftforge.common.data.BlockTagsProvider}. Forge's {@code TagAppender} has a
 * convenience {@code add(T)} overload taking the registry object directly, so {@code
 * BlockDyeMap}'s plain {@code Block} values (VERIFIED by reading {@code BlockDyeMap.java} directly —
 * an {@code EnumMap<AllDyes, Block>}, no {@code .properties().blockId()} indirection needed) tag
 * straight in, replacing 26.2's {@code b.properties().blockId()} chain. */
public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ExistingFileHelper existingFileHelper) {
        super(output, registriesFuture, NekomasFixed.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        BlockDyeMap.BRICKS.values().forEach(b -> tag(ModTags.DYED_BRICKS).add(b));
        BlockDyeMap.BRICK_SLAB.values().forEach(b -> {
            tag(ModTags.DYED_BRICK_SLABS).add(b);
            tag(BlockTags.SLABS).add(b);
        });
        BlockDyeMap.BRICK_STAIRS.values().forEach(b -> {
            tag(ModTags.DYED_BRICK_STAIRS).add(b);
            tag(BlockTags.STAIRS).add(b);
        });
        BlockDyeMap.BRICK_WALL.values().forEach(b -> {
            tag(ModTags.DYED_BRICK_WALLS).add(b);
            tag(BlockTags.WALLS).add(b);
        });

        BlockDyeMap.STAINED_GLASS.values().forEach(b -> tag(ModTags.STAINED_GLASSES).add(b));
        BlockDyeMap.STAINED_GLASS_PANE.values().forEach(b -> tag(ModTags.STAINED_GLASS_PANES).add(b));

        BlockDyeMap.GLAZED_TERRACOTTA.values().forEach(b -> tag(ModTags.GLAZED_TERRACOTTAS).add(b));

        BlockDyeMap.CONCRETE.values().forEach(b -> tag(ModTags.CONCRETES).add(b));
        BlockDyeMap.CONCRETE_POWDER.values().forEach(b -> tag(ModTags.CONCRETE_POWDERS).add(b));

        BlockDyeMap.SPOTTED_WOOL.values().forEach(b -> tag(ModTags.SPOTTED_WOOLS).add(b));
        BlockDyeMap.SPOTTED_CARPET.values().forEach(b -> tag(ModTags.SPOTTED_CARPETS).add(b));

        BlockDyeMap.FROGLIGHT.values().forEach(b -> tag(ModTags.FROGLIGHTS).add(b));

        tag(ModTags.CAN_BE_DYED_WITH_BRUSH)
                .addTag(ModTags.DYED_BRICKS)
                .addTag(ModTags.DYED_BRICK_SLABS)
                .addTag(ModTags.DYED_BRICK_STAIRS)
                .addTag(ModTags.DYED_BRICK_WALLS)
                .addTag(ModTags.STAINED_GLASSES)
                .addTag(ModTags.STAINED_GLASS_PANES)
                .addOptionalTag(BlockTags.TERRACOTTA)
                .addTag(ModTags.GLAZED_TERRACOTTAS)
                .addOptionalTag(BlockTags.WOOL)
                .addOptionalTag(BlockTags.WOOL_CARPETS)
                .addOptionalTag(BlockTags.CANDLES)
                .addOptionalTag(BlockTags.CONCRETE_POWDER)
                .addTag(ModTags.FROGLIGHTS)
                .addOptionalTag(BlockTags.SHULKER_BOXES)
                .addOptionalTag(BlockTags.BEDS)
                .add(Blocks.GLASS)
                .add(Blocks.GLASS_PANE)
                .add(Blocks.BRICKS)
                .add(Blocks.BRICK_SLAB)
                .add(Blocks.BRICK_STAIRS)
                .add(Blocks.BRICK_WALL);
    }
}
