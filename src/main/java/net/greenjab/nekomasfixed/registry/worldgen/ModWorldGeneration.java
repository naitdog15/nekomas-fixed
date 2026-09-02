package net.greenjab.nekomasfixed.registry.worldgen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.worldgen.feature.ClamFeature;
import net.greenjab.nekomasfixed.registry.worldgen.feature.GeyserBlockFeature;
import net.greenjab.nekomasfixed.registry.worldgen.feature.TermiteMoundFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The mod's 3 custom {@code Feature<?>} subclasses, each reusing a vanilla configuration codec.
 * Registration only - which biomes they generate in is data, one
 * {@code data/nekomasfixed/forge/biome_modifier/} file per feature; see {@code BiomeAdditions}'s
 * javadoc for the biome, decoration step and placed feature of each.
 */
public class ModWorldGeneration {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<Feature<CountConfiguration>> CLAM_FEATURE =
            FEATURES.register("clam", () -> new ClamFeature(CountConfiguration.CODEC));
    public static final RegistryObject<Feature<SimpleBlockConfiguration>> MOUND_FEATURE =
            FEATURES.register("mound", () -> new TermiteMoundFeature(SimpleBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<SimpleBlockConfiguration>> GEYSER_FEATURE =
            FEATURES.register("geyser_feature", () -> new GeyserBlockFeature(SimpleBlockConfiguration.CODEC));
}
