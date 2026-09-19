package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.other.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import java.util.List;
import java.util.function.UnaryOperator;

public class ComponentRegistry {
    public static void registerComponents() {
        System.out.println("register Component");
    }

    public static final DataComponentType<AnimalComponent> ANIMAL = registerComponent(
            "animal", builder -> builder.persistent(AnimalComponent.CODEC).networkSynchronized(AnimalComponent.PACKET_CODEC).cacheEncoding());
    public static final DataComponentType<TermitesComponent> TERMITES = registerComponent(
            "termites", builder -> builder.persistent(TermitesComponent.CODEC).networkSynchronized(TermitesComponent.PACKET_CODEC).cacheEncoding());

    public static final DataComponentType<Integer> CLAM_STATE = registerComponent(
            "clam_state", builder -> builder.persistent(ExtraCodecs.intRange(0, 3)).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<StoredTimeComponent> STORED_TIME = registerComponent("stored_time", builder -> builder.persistent(StoredTimeComponent.CODEC).networkSynchronized(StoredTimeComponent.PACKET_CODEC).cacheEncoding());

    public static final DataComponentType<ComboComponent> COMBO_MULTIPLIER = registerComponent(
            "combo_multiplier", builder -> builder.persistent(ComboComponent.CODEC).networkSynchronized(ComboComponent.PACKET_CODEC).cacheEncoding());
    public static final DataComponentType<List<ItemStack>> SOUP_INGREDIENTS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, NekomasFixed.id("soup_ingredients"), DataComponentType.<List<ItemStack>>builder().persistent(ItemStack.CODEC.listOf()).networkSynchronized(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list())).build());
    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builderOperator.apply(DataComponentType.builder()).build());}
}
