package net.greenjab.nekomasfixed.compat.ntrials;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * unlike Vanilla Backport, New Trials keeps its content in its own namespace - the only real
 * difference from that counterpart class.
 * always check {@code isPresent()} before using a handle; they refill themselves on registry
 * rebuild, so they stay right across a reload or joining a server with a different mod set.
 */
public final class TrialsContent {

    private static final String NAMESPACE = "ntrials";

    public static final RegistryObject<Item> MACE = item("mace");

    public static final RegistryObject<Block> TRIAL_SPAWNER = block("trial_spawner");

    public static final RegistryObject<SoundEvent> BREEZE_AMBIENT = sound("entity.breeze.ambient");
    public static final RegistryObject<SoundEvent> BREEZE_AMBIENT_CAVE = sound("entity.breeze.ambient_cave");
    public static final RegistryObject<SoundEvent> BREEZE_DEATH = sound("entity.breeze.death");
    public static final RegistryObject<SoundEvent> BREEZE_DEFLECT = sound("entity.breeze.deflect");
    public static final RegistryObject<SoundEvent> BREEZE_HURT = sound("entity.breeze.hurt");
    public static final RegistryObject<SoundEvent> BREEZE_INHALE = sound("entity.breeze.inhale");
    public static final RegistryObject<SoundEvent> BREEZE_JUMP = sound("entity.breeze.jump");
    public static final RegistryObject<SoundEvent> BREEZE_SHOOT = sound("entity.breeze.shoot");

    private TrialsContent() {
    }

    private static RegistryObject<Item> item(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.ITEMS, NekomasFixed.NAMESPACE);
    }

    private static RegistryObject<Block> block(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.BLOCKS, NekomasFixed.NAMESPACE);
    }

    private static RegistryObject<SoundEvent> sound(String path) {
        return RegistryObject.create(ResourceLocation.fromNamespaceAndPath(NAMESPACE, path),
                ForgeRegistries.Keys.SOUND_EVENTS, NekomasFixed.NAMESPACE);
    }
}
