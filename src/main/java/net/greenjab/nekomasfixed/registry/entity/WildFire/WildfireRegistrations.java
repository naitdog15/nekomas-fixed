package net.greenjab.nekomasfixed.registry.entity.WildFire;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * NEW CONSTRUCTION, not a port. The Wildfire AI (a Breeze-alike
 * hand-built entirely on vanilla's real Breeze memory/sound names) references 8 {@code
 * MemoryModuleType.BREEZE_*} constants and 6 {@code SoundEvents.BREEZE_*} constants that only exist
 * because vanilla's own Breeze exists - on 1.20.1, Breeze was not added until 1.21, so none of these
 * 14 constants exist on this version at all (verified: zero "BREEZE" occurrences anywhere in
 * forge-1.20.1-mapped-src/net/minecraft/sounds/SoundEvents.java). This file is their mod-owned
 * replacement, registered under the {@code nekomasfixed} namespace so the Wildfire AI files
 * (WildfireAi, WildfireJumpTask, WildfireShootTask, WildfireMeleeTask, WildfireBombTask,
 * WildfireSlideTowardsTargetTask, WildfireAttackablesSensor, WildfireEntity) can keep referencing the
 * same field names with {@code .get()} added. Value types match how each memory is actually written
 * in the ported AI (7 are {@code Unit}-valued cooldown/flag memories, 1 - BREEZE_JUMP_TARGET - carries
 * a {@code BlockPos}) so call sites type-check without an unchecked cast.
 * <p>
 * The 6 sound registrations need a {@code sounds.json} entry each ({@code data/**} is not
 * in this package's scope) - mapped from the equivalent vanilla Blaze/fire sounds. Registering a
 * SoundEvent with no sounds.json mapping does not error; it plays silence.
 */
public final class WildfireRegistrations {
    private WildfireRegistrations() {
    }

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
            DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, NekomasFixed.NAMESPACE);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NekomasFixed.NAMESPACE);

    private static RegistryObject<MemoryModuleType<Unit>> unitMemory(String name) {
        return MEMORY_MODULE_TYPES.register(name, () -> new MemoryModuleType<>(Optional.<Codec<Unit>>empty()));
    }

    // The 8 BREEZE_* memory modules the Wildfire AI needs (WildfireAi, WildfireJumpTask,
    // WildfireShootTask, WildfireMeleeTask, WildfireBombTask, WildfireSlideTowardsTargetTask) - 7
    // Unit-valued cooldown/flag memories, 1 BlockPos-valued jump target.
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT = unitMemory("breeze_shoot");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_LEAVING_WATER = unitMemory("breeze_leaving_water");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_COOLDOWN = unitMemory("breeze_shoot_cooldown");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_CHARGING = unitMemory("breeze_shoot_charging");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_RECOVERING = unitMemory("breeze_shoot_recovering");
    public static final RegistryObject<MemoryModuleType<BlockPos>> BREEZE_JUMP_TARGET =
            MEMORY_MODULE_TYPES.register("breeze_jump_target", () -> new MemoryModuleType<>(Optional.<Codec<BlockPos>>empty()));
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_COOLDOWN = unitMemory("breeze_jump_cooldown");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_INHALING = unitMemory("breeze_jump_inhaling");

    // The 6 BREEZE_* sounds, mapped from the equivalent vanilla Blaze/fire sounds.
    public static final RegistryObject<SoundEvent> BREEZE_SLIDE = SOUND_EVENTS.register("breeze_slide",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_slide")));
    public static final RegistryObject<SoundEvent> BREEZE_INHALE = SOUND_EVENTS.register("breeze_inhale",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_inhale")));
    public static final RegistryObject<SoundEvent> BREEZE_SHOOT_SOUND = SOUND_EVENTS.register("breeze_shoot",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_shoot")));
    public static final RegistryObject<SoundEvent> BREEZE_CHARGE = SOUND_EVENTS.register("breeze_charge",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_charge")));
    public static final RegistryObject<SoundEvent> BREEZE_JUMP_SOUND = SOUND_EVENTS.register("breeze_jump",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_jump")));
    public static final RegistryObject<SoundEvent> BREEZE_LAND = SOUND_EVENTS.register("breeze_land",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("breeze_land")));
}
