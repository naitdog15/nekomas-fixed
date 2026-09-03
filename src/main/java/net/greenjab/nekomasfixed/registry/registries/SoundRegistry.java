package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SoundRegistry {
    private SoundRegistry() {
    }

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NekomasFixed.NAMESPACE);

    // The three sounds the termite makes while eating wood, composed in sounds.json out of vanilla events.
    public static final RegistryObject<SoundEvent> TERMITE_CHEW = SOUND_EVENTS.register("termite_chew",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("termite_chew")));
    public static final RegistryObject<SoundEvent> TERMITE_CHEW_FINISH = SOUND_EVENTS.register("termite_chew_finish",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("termite_chew_finish")));
    public static final RegistryObject<SoundEvent> TERMITE_DEPOSIT = SOUND_EVENTS.register("termite_deposit",
            () -> SoundEvent.createVariableRangeEvent(NekomasFixed.id("termite_deposit")));
}
