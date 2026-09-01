package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.datagen.ModAdvancementProvider;
import net.greenjab.nekomasfixed.datagen.ModBlockTagProvider;
import net.greenjab.nekomasfixed.datagen.ModItemTagProvider;
import net.greenjab.nekomasfixed.datagen.ModLootTableProvider;
import net.greenjab.nekomasfixed.datagen.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * {@code GatherDataEvent} on the mod bus replaces the Fabric
 * {@code DataGeneratorEntrypoint}. Deliberately NOT {@code @OnlyIn(Dist.CLIENT)} — {@code runData}
 * runs on the client dist, but this class is a common event handler wired from the mod bus like any
 * other. {@code ModModelProvider}/{@code ModRegistryDataGenerator}
 * are deleted outright (a different/richer API whose output is thrown away, and an empty
 * {@code configure()}, respectively); {@code ExistingFileHelper} is what makes
 * {@code --existing src/main/resources} in the {@code data} run config matter — Forge's providers
 * validate texture/model references against it and fail the build on a dangling reference.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NekomasFixedDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(output, event.getLookupProvider(), existingFileHelper,
                List.of(new ModAdvancementProvider())));

        ModBlockTagProvider blockTags = new ModBlockTagProvider(output, event.getLookupProvider(), existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, event.getLookupProvider(), blockTags.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new LootTableProvider(output, java.util.Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModLootTableProvider::new, LootContextParamSets.BLOCK))));
    }
}
