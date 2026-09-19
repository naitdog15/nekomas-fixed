package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class EnchantmentRegistry {
    public static void registerEnchantments() {
        System.out.println("register Enchantments");
    }

    public static final ResourceKey<Enchantment> DISMOUNT = of("dismount");
    public static final ResourceKey<Enchantment> LEECHING = of("leeching");
    public static final ResourceKey<Enchantment> SHATTER = of("shatter");

    private static ResourceKey<Enchantment> of(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, NekomasFixed.id(id));
    }
}
