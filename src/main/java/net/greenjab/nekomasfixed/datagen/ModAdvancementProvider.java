package net.greenjab.nekomasfixed.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

/** vanilla's AdvancementProvider.AdvancementGenerator (with AdvancementHolder) doesn't exist on
 * 1.20.1 - it hands raw Advancement to the saver, so use Forge's ForgeAdvancementProvider one instead. */
public class ModAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
    }
}
