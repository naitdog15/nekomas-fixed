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
 * Second {@code @Mod.EventBusSubscriber} class, same mechanism as {@code ModBusEvents} but kept near
 * the entities it describes. Don't mix this annotation scan with a manual {@code register(...)} call -
 * that's the usual source of "my event never fired".
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
