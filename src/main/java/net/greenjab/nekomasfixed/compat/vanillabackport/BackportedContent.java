package net.greenjab.nekomasfixed.compat.vanillabackport;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * a handle can be empty even when Vanilla Backport is present - it lets a pack turn individual
 * features off - so always ask {@code isPresent()} before using one.
 * handles are live, not fetched once: they refill on every registry rebuild, so they stay right
 * across a reload or joining a server with a different set of mods.
 */
public final class BackportedContent {

    /** Vanilla Backport registers its content into the vanilla namespace, not its own. */
    private static final String NAMESPACE = "minecraft";

    public static final RegistryObject<Item> OPEN_EYEBLOSSOM = item("open_eyeblossom");
    public static final RegistryObject<Item> COPPER_NUGGET = item("copper_nugget");
    public static final RegistryObject<Item> RESIN_CLUMP = item("resin_clump");
    public static final RegistryObject<Item> IRON_SPEAR = item("iron_spear");

    public static final RegistryObject<EntityType<?>> HAPPY_GHAST = entityType("happy_ghast");

    private BackportedContent() {
    }

    private static RegistryObject<Item> item(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.ITEMS, NekomasFixed.NAMESPACE);
    }

    private static RegistryObject<EntityType<?>> entityType(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.ENTITY_TYPES, NekomasFixed.NAMESPACE);
    }
}
