package net.greenjab.nekomasfixed.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

/**
 * {@code FabricAdvancementProvider} → Forge's {@code ForgeAdvancementProvider.AdvancementGenerator}.
 * Vanilla's own {@code AdvancementProvider.AdvancementGenerator} doesn't exist on 1.20.1 (that's a
 * newer-version shape, and its {@code AdvancementHolder} type doesn't exist here either - 1.20.1
 * hands raw {@code Advancement}s to the saver) - {@code net.minecraft.data.advancements
 * .AdvancementProvider} is {@code @Deprecated} on this version specifically in favour of this Forge
 * one, which is also what threads the {@code ExistingFileHelper} through. Body was already empty
 * (no advancements defined).
 */
public class ModAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
    }
}
