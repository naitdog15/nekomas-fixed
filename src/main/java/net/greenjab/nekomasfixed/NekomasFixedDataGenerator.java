package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.datagen.ModAdvancementProvider;
import net.greenjab.nekomasfixed.datagen.ModItemTagProvider;
import net.greenjab.nekomasfixed.datagen.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * deliberately not {@code @OnlyIn(Dist.CLIENT)} - {@code runData} runs on the client dist, but this
 * is a common event handler wired from the mod bus like any other.
 * {@code ExistingFileHelper} is what makes {@code --existing src/main/resources} in the data run
 * config matter - Forge's providers validate texture/model references against it and fail the
 * build on a dangling reference.
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

        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, event.getLookupProvider(), CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), existingFileHelper));
    }
}
