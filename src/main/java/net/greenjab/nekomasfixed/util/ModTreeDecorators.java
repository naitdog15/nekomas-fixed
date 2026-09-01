package net.greenjab.nekomasfixed.util;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.worldgen.tree.BaobabTreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** DeferredRegister<TreeDecoratorType<?>> on ForgeRegistries.TREE_DECORATOR_TYPES. */
public class ModTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES =
            DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<TreeDecoratorType<?>> BAOBAB_TREE_DECORATOR =
            TREE_DECORATOR_TYPES.register("baobab_tree_decorator", () -> new TreeDecoratorType<>(BaobabTreeDecorator.CODEC));
}
