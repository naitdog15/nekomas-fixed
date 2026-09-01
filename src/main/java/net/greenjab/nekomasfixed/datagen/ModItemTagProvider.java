package net.greenjab.nekomasfixed.datagen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

/** {@code FabricTagsProvider.ItemTagsProvider} → Forge's
 * {@code net.minecraftforge.common.data.ItemTagsProvider}, which additionally needs the block-tag
 * provider's {@code contentsGetter()} (for {@code tag(blockTag)} copy-to-item-tag convenience) — wired
 * from {@code NekomasFixedDataGenerator}. {@code addTags} was already empty. */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, completableFuture, blockTags, NekomasFixed.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}
