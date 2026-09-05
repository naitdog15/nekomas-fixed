package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.*;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.Moobloom;
import net.greenjab.nekomasfixed.registry.entity.WildFire.FireBomb;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

/**
 * Item factories needing an actual EntityType<T> must never call .get() outside a deferred supplier
 * - see BlockRegistry's javadoc on the same trap. Every boat/spawn-egg factory below is itself a
 * lambda, so EntityTypeRegistry.X.get() inside it only runs during RegisterEvent&lt;Item&gt;, well
 * after RegisterEvent&lt;EntityType&gt;.
 * <p>
 * 1.20.1's EntityType.Builder has no noLootTable()/eyeHeight(float)/notInPeaceful() - those three
 * calls are dropped rather than translated (no loot JSON already resolves to the empty table, eye
 * height comes from the entity class, peaceful despawn is Mob#shouldDespawnInPeaceful()).
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

    // hand out the RegistryObjects as Suppliers, not resolved EntityTypes, so building the list
    // never calls .get() before RegisterEvent<EntityType> has run
    public static List<Supplier<EntityType<BigBoat>>> bigBoats() {
        return List.of(BIG_ACACIA_BOAT, BIG_BAMBOO_BOAT, BIG_BIRCH_BOAT, BIG_CHERRY_BOAT, BIG_DARK_OAK_BOAT, BIG_JUNGLE_BOAT, BIG_MANGROVE_BOAT, BIG_OAK_BOAT, BIG_PALE_OAK_BOAT, BIG_SPRUCE_BOAT);
    }
    public static List<Supplier<EntityType<HugeBoat>>> hugeBoats() {
        return List.of(HUGE_ACACIA_BOAT, HUGE_BAMBOO_BOAT, HUGE_BIRCH_BOAT, HUGE_CHERRY_BOAT, HUGE_DARK_OAK_BOAT, HUGE_JUNGLE_BOAT, HUGE_MANGROVE_BOAT, HUGE_OAK_BOAT, HUGE_PALE_OAK_BOAT, HUGE_SPRUCE_BOAT);
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
}
