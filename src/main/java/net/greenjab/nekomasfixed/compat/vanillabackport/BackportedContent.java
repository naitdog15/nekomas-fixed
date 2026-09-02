package net.greenjab.nekomasfixed.compat.vanillabackport;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The blocks, items and sounds Vanilla Backport puts into the vanilla namespace, looked up by id so
 * this mod never has to compile against it. Every handle here is empty when Vanilla Backport is
 * absent — and can still be empty when it is present, since it lets a pack turn individual
 * features off — so always ask {@code isPresent()} before using one.
 *
 * <p>The handles are live rather than a value fetched once: they refill themselves whenever the
 * registries are rebuilt, which is what keeps them right after a reload or after joining a server
 * with a different set of mods. Declaring one costs nothing and never throws, so an id that turns
 * out to be missing simply stays empty.
 */
public final class BackportedContent {

    /** Vanilla Backport registers its content into the vanilla namespace, not its own. */
    private static final String NAMESPACE = "minecraft";

    public static final RegistryObject<Item> OPEN_EYEBLOSSOM = item("open_eyeblossom");
    public static final RegistryObject<Item> COPPER_NUGGET = item("copper_nugget");
    public static final RegistryObject<Item> RESIN_CLUMP = item("resin_clump");
    public static final RegistryObject<Item> IRON_SPEAR = item("iron_spear");

    public static final RegistryObject<EntityType<?>> HAPPY_GHAST = entityType("happy_ghast");

    public static final RegistryObject<SoundEvent> SHELF_TAKE_ITEM = sound("block.shelf.take_item");
    public static final RegistryObject<SoundEvent> SHELF_PLACE_ITEM = sound("block.shelf.place_item");
    public static final RegistryObject<SoundEvent> SHELF_SINGLE_SWAP = sound("block.shelf.single_swap");
    public static final RegistryObject<SoundEvent> SHELF_MULTI_SWAP = sound("block.shelf.multi_swap");
    public static final RegistryObject<SoundEvent> SHELF_ACTIVATE = sound("block.shelf.activate");
    public static final RegistryObject<SoundEvent> SHELF_DEACTIVATE = sound("block.shelf.deactivate");

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

    private static RegistryObject<SoundEvent> sound(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.SOUND_EVENTS, NekomasFixed.NAMESPACE);
    }
}
