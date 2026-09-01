package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireAttackablesSensor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The odds and ends that do not belong to any of the other registry holders, even though what they
 * register pairs with entity code elsewhere in the tree.
 * <p>
 * DebugSubscription/WildfireDebugData deleted outright (26.x-only F3 telemetry, zero gameplay).
 * SensorType -&gt; DeferredRegister on ForgeRegistries.SENSOR_TYPES (no more raw
 * {@code Registry.register(BuiltInRegistries.SENSOR_TYPE, ...)}). {@code IS_TROPICAL_FISH_FED} is
 * NOT a registration - a bare {@code EntityDataAccessor<Boolean>} on {@code Dolphin} using the
 * vanilla {@code EntityDataSerializers.BOOLEAN} - kept byte-for-byte verbatim, no change needed at
 * all.
 */
public class OtherRegistry {

    // data tracker - verbatim, not a registration (see class javadoc).
    public static final EntityDataAccessor<Boolean> IS_TROPICAL_FISH_FED =
            SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);

    // sensor
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, NekomasFixed.NAMESPACE);
    public static final RegistryObject<SensorType<WildfireAttackablesSensor>> WILDFIRE_ATTACK_ENTITY_SENSOR =
            SENSOR_TYPES.register("wildfire_attack_entity_sensor", () -> new SensorType<>(WildfireAttackablesSensor::new));
}
