package net.greenjab.nekomasfixed.registry.entity.WildFire;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.ntrials.TrialsContent;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * The brain memories and voice the Wildfire runs on - a breeze-style AI vocabulary (charge, leap,
 * land, inhale, shoot, slide) that doesn't exist in the base game, registered here under this mod's
 * namespace.
 *
 * <p>Sounds map in {@code sounds.json} onto blaze/fire stand-ins; New Trials swaps in real breeze
 * recordings at the point of play when present.
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

    // The cooldowns and flags the attack tasks pass between each other, plus the jump target.
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT = unitMemory("breeze_shoot");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_LEAVING_WATER = unitMemory("breeze_leaving_water");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_COOLDOWN = unitMemory("breeze_shoot_cooldown");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_CHARGING = unitMemory("breeze_shoot_charging");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_RECOVERING = unitMemory("breeze_shoot_recovering");
    public static final RegistryObject<MemoryModuleType<BlockPos>> BREEZE_JUMP_TARGET =
            MEMORY_MODULE_TYPES.register("breeze_jump_target", () -> new MemoryModuleType<>(Optional.<Codec<BlockPos>>empty()));
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_COOLDOWN = unitMemory("breeze_jump_cooldown");
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_INHALING = unitMemory("breeze_jump_inhaling");

    // The six sounds the Wildfire speaks with, mapped in sounds.json onto blaze and fire sounds.
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

    /** The wind-up before a bomb or a fireball volley. */
    public static SoundEvent inhale() {
        return breezeVoice(BREEZE_INHALE, TrialsContent.BREEZE_INHALE);
    }

    /** The launch itself. */
    public static SoundEvent shoot() {
        return breezeVoice(BREEZE_SHOOT_SOUND, TrialsContent.BREEZE_SHOOT);
    }

    /** Crouching down before a leap. */
    public static SoundEvent charge() {
        return breezeVoice(BREEZE_CHARGE, TrialsContent.BREEZE_INHALE);
    }

    /** Leaving the ground. */
    public static SoundEvent jump() {
        return breezeVoice(BREEZE_JUMP_SOUND, TrialsContent.BREEZE_JUMP);
    }

    /** Hitting the ground again. */
    public static SoundEvent land() {
        return breezeVoice(BREEZE_LAND, TrialsContent.BREEZE_DEFLECT);
    }

    /** Skating around between attacks. */
    public static SoundEvent slide() {
        return breezeVoice(BREEZE_SLIDE, TrialsContent.BREEZE_AMBIENT_CAVE);
    }

    /**
     * Picks the voice to play with. New Trials ships real breeze recordings, and they beat this
     * mod's blaze-flavoured stand-ins every time, so they are used when they are there and the
     * option is on. The handle can be empty even with New Trials installed - a pack is free to turn
     * its content off - so this always has the mod's own sound to fall back on.
     */
    private static SoundEvent breezeVoice(RegistryObject<SoundEvent> own, RegistryObject<SoundEvent> trials) {
        if (NekomasFixedConfig.BREEZE_SOUNDS.get() && trials.isPresent()) {
            return trials.get();
        }
        return own.get();
    }
}
