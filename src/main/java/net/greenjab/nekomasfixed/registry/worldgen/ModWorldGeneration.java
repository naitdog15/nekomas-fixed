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
 * 3 custom {@code Feature<?>} subclasses, all reusing vanilla config codecs, now a proper
 * {@code DeferredRegister<Feature<?>>} on {@code ForgeRegistries.FEATURES}. The 4
 * {@code BiomeModifications.addFeature(...)} biome-linking calls that used to live in
 * {@code generateModWorldGen()} are Fabric-only and are dropped from Java entirely - on Forge that
 * linkage is 4 {@code data/nekomasfixed/forge/biome_modifier/} JSON files, which is {@code data/**}
 * (not this file's scope) - whoever authors those files needs the exact
 * biome/decoration-step/placed-feature specs for each.
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
