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

public class OtherRegistry {

    // not a registration - a bare EntityDataAccessor<Boolean> on Dolphin, kept verbatim
    public static final EntityDataAccessor<Boolean> IS_TROPICAL_FISH_FED =
            SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, NekomasFixed.NAMESPACE);
    public static final RegistryObject<SensorType<WildfireAttackablesSensor>> WILDFIRE_ATTACK_ENTITY_SENSOR =
            SENSOR_TYPES.register("wildfire_attack_entity_sensor", () -> new SensorType<>(WildfireAttackablesSensor::new));
}
