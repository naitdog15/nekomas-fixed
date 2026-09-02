package net.greenjab.nekomasfixed.compat.ntrials;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The blocks, items and sounds New Trials adds, looked up by id so this mod never has to compile
 * against it. Unlike Vanilla Backport, New Trials keeps its content in its own namespace, which is
 * the only real difference between this class and its counterpart.
 *
 * <p>Every handle here is empty when New Trials is absent, so always ask {@code isPresent()} before
 * using one. The handles refill themselves whenever the registries are rebuilt, so they stay right
 * across a reload and across joining a server with a different set of mods.
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
