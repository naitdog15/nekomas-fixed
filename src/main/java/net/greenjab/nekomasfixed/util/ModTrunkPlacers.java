package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.worldgen.tree.BaobabTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * DeferredRegister&lt;TrunkPlacerType&lt;?&gt;&gt;. Forge wraps the tree-decorator registry but not the
 * trunk-placer one, so this one is keyed off vanilla's own registry key; DeferredRegister handles a
 * plain vanilla registry through RegisterEvent just the same. The key is fully qualified because
 * this mod has its own class named Registries.
 */
public class ModTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.TRUNK_PLACER_TYPE, NekomasFixed.NAMESPACE);

    public static final RegistryObject<TrunkPlacerType<?>> BAOBAB_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("baobab_trunk_placer", () -> new TrunkPlacerType<>(BaobabTrunkPlacer.CODEC));
}
