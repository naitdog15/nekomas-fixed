package net.greenjab.nekomasfixed.registry.worldgen;

import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * The mod's own 5 {@code SpawnPlacements.register(...)} calls stay Java (run from
 * {@code FMLCommonSetupEvent#enqueueWork}) - this is a DIFFERENT mechanism from the 6
 * {@code BiomeModifications.addSpawn(...)} calls that used to live here, which become 6
 * {@code data/nekomasfixed/forge/biome_modifier/} JSON files ({@code data/**}, not this file's
 * scope). The 6 addSpawn specs (selector / MobCategory / entity / weight / min / max),
 * for whoever authors those JSON files:
 * <pre>
 * includeByKey(dripstone_caves, plains)         MONSTER   SUSPICIOUS_SPIDER  30 1 2
 * tag(nekomasfixed:spawns_rime)                 MONSTER   RIME              100 4 4
 * tag(minecraft:is_jungle)                      MONSTER   DERELICT          100 4 4
 * tag(minecraft:is_ocean)                       MONSTER   DRENCHED            5 1 2
 * tag(minecraft:more_frequent_drowned_spawns)   MONSTER   DRENCHED            5 1 2
 * includeByKey(flower_forest, sunflower_plains, meadow)  CREATURE  MOOBLOOM  30 1 2
 * </pre>
 */
public class BiomeAdditions {
    public static void addSpawns(){
        SpawnPlacements.register(EntityTypeRegistry.WILDFIRE.get(), SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WildfireEntity::canSpawn);
        SpawnPlacements.register(EntityTypeRegistry.SUSPICIOUS_SPIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SuspiciousSpider::canSpawn);
        SpawnPlacements.register(EntityTypeRegistry.RIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkSurfaceMonstersSpawnRules);
        SpawnPlacements.register(EntityTypeRegistry.DERELICT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkSurfaceMonstersSpawnRules);
        SpawnPlacements.register(EntityTypeRegistry.DRENCHED.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Drenched::canSpawn);
    }
}
