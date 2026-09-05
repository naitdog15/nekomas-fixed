package net.greenjab.nekomasfixed.registry.worldgen;

import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * SpawnPlacements.register calls must run from FMLCommonSetupEvent#enqueueWork, in Java - which
 * biomes/features these mobs actually show up in is separate data, under
 * {@code data/nekomasfixed/forge/biome_modifier/}.
 * <p>
 * The feature/spawn config switches are read inside Feature#place and the spawn placement check
 * instead of here: a biome modifier is plain data and can't be conditioned on a config value.
 */
public class BiomeAdditions {
    public static void addSpawns(){
        SpawnPlacements.register(EntityTypeRegistry.WILDFIRE.get(), SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WildfireEntity::canSpawn);
        SpawnPlacements.register(EntityTypeRegistry.SUSPICIOUS_SPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SuspiciousSpider::canSpawn);
        SpawnPlacements.register(EntityTypeRegistry.RIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(EntityTypeRegistry.DERELICT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(EntityTypeRegistry.DRENCHED.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Drenched::canSpawn);
    }
}
