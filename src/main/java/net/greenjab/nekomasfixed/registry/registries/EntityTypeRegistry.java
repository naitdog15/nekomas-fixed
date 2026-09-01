package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.*;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.Moobloom;
import net.greenjab.nekomasfixed.registry.entity.WildFire.FireBomb;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

/**
 * DeferredRegister&lt;EntityType&lt;?&gt;&gt; conversion. On the previous source, field initialisers
 * in ItemRegistry.java force-initialised entity types early under JLS 12.4.1 - RegistryObject.get()'s
 * laziness makes that ordering irrelevant now.
 * <p>
 * Item factories that need an actual {@code EntityType<T>} instance must never call {@code .get()}
 * outside a deferred supplier - see BlockRegistry.java's javadoc on the same trap. Every boat/
 * spawn-egg item factory below is itself a lambda, so {@code EntityTypeRegistry.X.get()} inside it
 * is safe (only evaluated when Forge invokes the stored Item supplier during
 * RegisterEvent&lt;Item&gt;, well after RegisterEvent&lt;EntityType&gt;).
 * <p>
 * 1.20.1's {@code EntityType.Builder} carries no {@code noLootTable()}, {@code eyeHeight(float)} or
 * {@code notInPeaceful()}: an entity with no loot-table JSON simply resolves to the empty table,
 * eye height comes from {@code Entity#getEyeHeight(Pose, EntityDimensions)} on the entity class, and
 * peaceful-difficulty despawning is {@code Mob#shouldDespawnInPeaceful()}. Those three builder calls
 * are therefore dropped rather than translated.
 * <p>
 * Attribute registration lives on
 * {@link net.greenjab.nekomasfixed.registry.entity.EntityAttributesAndSpawns}, which carries the
 * mod's single {@code EntityAttributeCreationEvent} handler for all eight living types.
 */
public class EntityTypeRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<EntityType<FakeBoat>> FAKE_BOAT = register(
            "fake_boat", EntityType.Builder.of(FakeBoat::new, MobCategory.MISC)
                    .sized(1.65f, 0.5625F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BigBoat>> BIG_ACACIA_BOAT = bigBoatFactory("big_acacia_boat", () -> ItemRegistry.BIG_ACACIA_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_BAMBOO_BOAT = bigBoatFactory("big_bamboo_boat", () -> ItemRegistry.BIG_BAMBOO_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_BIRCH_BOAT = bigBoatFactory("big_birch_boat", () -> ItemRegistry.BIG_BIRCH_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_CHERRY_BOAT = bigBoatFactory("big_cherry_boat", () -> ItemRegistry.BIG_CHERRY_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_DARK_OAK_BOAT = bigBoatFactory("big_dark_oak_boat", () -> ItemRegistry.BIG_DARK_OAK_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_JUNGLE_BOAT = bigBoatFactory("big_jungle_boat", () -> ItemRegistry.BIG_JUNGLE_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_MANGROVE_BOAT = bigBoatFactory("big_mangrove_boat", () -> ItemRegistry.BIG_MANGROVE_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_OAK_BOAT = bigBoatFactory("big_oak_boat", () -> ItemRegistry.BIG_OAK_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_PALE_OAK_BOAT = bigBoatFactory("big_pale_oak_boat", () -> ItemRegistry.BIG_PALE_OAK_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_SPRUCE_BOAT = bigBoatFactory("big_spruce_boat", () -> ItemRegistry.BIG_SPRUCE_BOAT.get());
    public static final RegistryObject<EntityType<BigBoat>> BIG_BAOBAB_BOAT = bigBoatFactory("big_baobab_boat", () -> ItemRegistry.BIG_BAOBAB_BOAT.get());

    public static final RegistryObject<EntityType<HugeBoat>> HUGE_ACACIA_BOAT = hugeBoatFactory("huge_acacia_boat", () -> ItemRegistry.HUGE_ACACIA_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_BAMBOO_BOAT = hugeBoatFactory("huge_bamboo_boat", () -> ItemRegistry.HUGE_BAMBOO_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_BIRCH_BOAT = hugeBoatFactory("huge_birch_boat", () -> ItemRegistry.HUGE_BIRCH_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_CHERRY_BOAT = hugeBoatFactory("huge_cherry_boat", () -> ItemRegistry.HUGE_CHERRY_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_DARK_OAK_BOAT = hugeBoatFactory("huge_dark_oak_boat", () -> ItemRegistry.HUGE_DARK_OAK_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_JUNGLE_BOAT = hugeBoatFactory("huge_jungle_boat", () -> ItemRegistry.HUGE_JUNGLE_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_MANGROVE_BOAT = hugeBoatFactory("huge_mangrove_boat", () -> ItemRegistry.HUGE_MANGROVE_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_OAK_BOAT = hugeBoatFactory("huge_oak_boat", () -> ItemRegistry.HUGE_OAK_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_PALE_OAK_BOAT = hugeBoatFactory("huge_pale_oak_boat", () -> ItemRegistry.HUGE_PALE_OAK_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_SPRUCE_BOAT = hugeBoatFactory("huge_spruce_boat", () -> ItemRegistry.HUGE_SPRUCE_BOAT.get());
    public static final RegistryObject<EntityType<HugeBoat>> HUGE_BAOBAB_BOAT = hugeBoatFactory("huge_baobab_boat", () -> ItemRegistry.HUGE_BAOBAB_BOAT.get());

    public static final RegistryObject<EntityType<Boat>> BAOBAB_BOAT = register("baobab_boat",
            EntityType.Builder.of(getBoatFactory(() -> ItemRegistry.BAOBAB_BOAT.get()), MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<ChestBoat>> BAOBAB_CHEST_BOAT = register("baobab_chest_boat",
            EntityType.Builder.of(getChestBoatFactory(() -> ItemRegistry.BAOBAB_CHEST_BOAT.get()), MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));

    // NOTE: these two List.of(...) initialisers call .get() on this file's OWN RegistryObjects, but
    // that is safe here (unlike a field referencing ANOTHER registry file) only because BlockRegistry-
    // style ordering guarantees do NOT apply across different DeferredRegister instances the same
    // way - so these are deliberately turned into Supplier-backed lazy lists instead of eager ones,
    // matching how the rest of the registry package handles it.
    public static List<Supplier<EntityType<BigBoat>>> bigBoats() {
        return List.of(BIG_ACACIA_BOAT, BIG_BAMBOO_BOAT, BIG_BIRCH_BOAT, BIG_CHERRY_BOAT, BIG_DARK_OAK_BOAT, BIG_JUNGLE_BOAT, BIG_MANGROVE_BOAT, BIG_OAK_BOAT, BIG_PALE_OAK_BOAT, BIG_SPRUCE_BOAT, BIG_BAOBAB_BOAT);
    }
    public static List<Supplier<EntityType<HugeBoat>>> hugeBoats() {
        return List.of(HUGE_ACACIA_BOAT, HUGE_BAMBOO_BOAT, HUGE_BIRCH_BOAT, HUGE_CHERRY_BOAT, HUGE_DARK_OAK_BOAT, HUGE_JUNGLE_BOAT, HUGE_MANGROVE_BOAT, HUGE_OAK_BOAT, HUGE_PALE_OAK_BOAT, HUGE_SPRUCE_BOAT, HUGE_BAOBAB_BOAT);
    }
    // 1.20.1 has exactly two vanilla boat entity types - every wood shares them and differs only by
    // the Boat.Type variant stored on the entity - so this list is those two plus the mod's own
    // plain-tier baobab boat, not one entry per wood.
    public static List<EntityType<? extends Boat>> vanillaBoats() {
        return List.of(EntityType.BOAT, EntityType.CHEST_BOAT, BAOBAB_BOAT.get(), BAOBAB_CHEST_BOAT.get());
    }

    public static final RegistryObject<EntityType<TargetDummy>> TARGET_DUMMY = register("target_dummy",
            EntityType.Builder.of(TargetDummy::new, MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear",
            EntityType.Builder.of(SpearEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<WildfireTrident>> WILDFIRE_TRIDENT = register("wildfire_trident",
            EntityType.Builder.<WildfireTrident>of(WildfireTrident::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));

    public static final RegistryObject<EntityType<SlingshotProjectile>> SLINGSHOT_PROJECTILE = register("slingshot_projectile",
            EntityType.Builder.<SlingshotProjectile>of(SlingshotProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

    public static final RegistryObject<EntityType<FireBomb>> FIRE_BOMB = register("fire_bomb",
            EntityType.Builder.<FireBomb>of(FireBomb::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F) .clientTrackingRange(4).updateInterval(10));

    public static final RegistryObject<EntityType<SlownessSnowball>> SLOWNESS_SNOWBALL = register("slowness_snowball",
            EntityType.Builder.<SlownessSnowball>of(SlownessSnowball::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));

    public static final RegistryObject<EntityType<WildfireEntity>> WILDFIRE = register("wildfire",
            EntityType.Builder.of(WildfireEntity::new, MobCategory.MONSTER).fireImmune().sized(0.75F, 1.975F).clientTrackingRange(8));

    public static final RegistryObject<EntityType<Termite>> TERMITE = register("termite",
            EntityType.Builder.of(Termite::new, MobCategory.MONSTER).sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<Moobloom>> MOOBLOOM = register("moobloom",
            EntityType.Builder.of(Moobloom::new, MobCategory.AMBIENT).sized(1f, 1f));

    public static final RegistryObject<EntityType<SuspiciousSpider>> SUSPICIOUS_SPIDER = register("suspicious_spider",
            EntityType.Builder.of(SuspiciousSpider::new, MobCategory.MONSTER).sized(1f, 1f));

    public static final RegistryObject<EntityType<Derelict>> DERELICT = register("derelict",
            EntityType.Builder.of(Derelict::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f));

    public static final RegistryObject<EntityType<Rime>> RIME = register("rime",
            EntityType.Builder.of(Rime::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f));

    public static final RegistryObject<EntityType<Drenched>> DRENCHED = register("drenched",
            EntityType.Builder.of(Drenched::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.99f));


    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, EntityType.Builder<T> type) {
        return ENTITY_TYPES.register(id, () -> type.build(id));
    }

    private static RegistryObject<EntityType<BigBoat>> bigBoatFactory(String id, Supplier<Item> item) {
        return register(id, EntityType.Builder.of(getBigBoatFactory(item), MobCategory.MISC)
                        .sized(1.9f, 0.5625F).clientTrackingRange(10));
    }
    private static EntityType.EntityFactory<BigBoat> getBigBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new BigBoat(type, world, itemSupplier);
    }

    private static RegistryObject<EntityType<HugeBoat>> hugeBoatFactory(String id, Supplier<Item> item) {
        return register(id, EntityType.Builder.of(getHugeBoatFactory(item), MobCategory.MISC)
                        .sized(2.6f, 0.5625F).clientTrackingRange(10));
    }
    private static EntityType.EntityFactory<HugeBoat> getHugeBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new HugeBoat(type, world, itemSupplier);
    }
    // 1.20.1's Boat/ChestBoat pick their drop item off the closed Boat.Type enum, which has no
    // baobab entry and cannot gain one. Overriding getDropItem() is the only hook - it also feeds
    // getPickResult(), so middle-clicking a baobab boat still hands back the right item.
    private static EntityType.EntityFactory<Boat> getBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new Boat(type, world) {
            @Override
            public Item getDropItem() {
                return itemSupplier.get();
            }
        };
    }
    private static EntityType.EntityFactory<ChestBoat> getChestBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new ChestBoat(type, world) {
            @Override
            public Item getDropItem() {
                return itemSupplier.get();
            }
        };
    }
}
