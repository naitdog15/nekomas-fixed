package net.greenjab.nekomasfixed.datagen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** only addTag keeps the intrinsic-holder appender (which has add(Block)); addOptionalTag drops back
 * to a plain appender that takes resource keys only - block-form adds need a fresh tag(...) call. */
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
                // no BlockTags.CONCRETE_POWDER constant on 1.20.1 - built by hand instead.
                .addOptionalTag(TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("concrete_powder")))
                .addTag(ModTags.FROGLIGHTS)
                .addOptionalTag(BlockTags.SHULKER_BOXES)
                .addOptionalTag(BlockTags.BEDS);
        // second appender: the optional-tag calls above hand back the plain, resource-key-only one.
        tag(ModTags.CAN_BE_DYED_WITH_BRUSH)
                .add(Blocks.GLASS, Blocks.GLASS_PANE, Blocks.BRICKS,
                        Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS, Blocks.BRICK_WALL);
    }
}
