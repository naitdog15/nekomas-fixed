package net.greenjab.nekomasfixed.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.function.Consumer;

/**
 * {@code FabricAdvancementProvider} → vanilla {@code AdvancementProvider}, which
 * on 1.20.1 takes a {@code List<AdvancementGenerator>} at construction rather than being subclassed —
 * this class now IS one such generator (constructed into that list by
 * {@code NekomasFixedDataGenerator}). Body was already empty (no advancements defined).
 */
public class ModAdvancementProvider implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
    }
}
