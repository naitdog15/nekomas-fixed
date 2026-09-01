package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.worldgen.tree.BaobabTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** DeferredRegister<TrunkPlacerType<?>> on ForgeRegistries.TRUNK_PLACER_TYPES. */
public class ModTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(ForgeRegistries.TRUNK_PLACER_TYPES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<TrunkPlacerType<?>> BAOBAB_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("baobab_trunk_placer", () -> new TrunkPlacerType<>(BaobabTrunkPlacer.CODEC));
}
