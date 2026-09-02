package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.entity.Moobloom.Moobloom;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.worldgen.BiomeAdditions;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * EntityAttributeCreationEvent and the 5 SpawnPlacements.register calls inside
 * FMLCommonSetupEvent#enqueueWork could sit on {@code ModBusEvents}, but keeping them next to the
 * entities they describe is easier to follow. This is a second {@code @Mod.EventBusSubscriber}
 * class self-registering via FML's annotation scan exactly like {@code ModBusEvents} does — same
 * mechanism, different file — so the handlers still land on the same events, just closer to home.
 * (Sticking to one idiom for handlers matters: mixing the annotation with a manual
 * {@code register(...)} is the usual source of "my event never fired".)
 * <p>
 * Registers attributes for the 8 living entity types in this package (projectiles and vehicles -
 * SlownessSnowball, SlingshotProjectile, WildfireTrident, SpearEntity, FireBomb, BigBoat, HugeBoat,
 * FakeBoat - are not LivingEntity and need none). Rime and Derelict have no custom attribute builder
 * of their own (both plain Zombie subclasses) and reuse {@code Zombie.createAttributes()} directly.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityAttributesAndSpawns {
    private EntityAttributesAndSpawns() {
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(EntityTypeRegistry.TERMITE.get(), Termite.createAttributes().build());
        event.put(EntityTypeRegistry.SUSPICIOUS_SPIDER.get(), SuspiciousSpider.createSuspiciousSpiderAttributes().build());
        event.put(EntityTypeRegistry.WILDFIRE.get(), WildfireEntity.createWildfireAttributes().build());
        event.put(EntityTypeRegistry.DRENCHED.get(), Drenched.createDrenchedAttributes().build());
        event.put(EntityTypeRegistry.MOOBLOOM.get(), Moobloom.createAttributes().build());
        event.put(EntityTypeRegistry.TARGET_DUMMY.get(), TargetDummy.createTargetDummyAttributes().build());
        event.put(EntityTypeRegistry.RIME.get(), Zombie.createAttributes().build());
        event.put(EntityTypeRegistry.DERELICT.get(), Zombie.createAttributes().build());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BiomeAdditions::addSpawns);
    }
}
