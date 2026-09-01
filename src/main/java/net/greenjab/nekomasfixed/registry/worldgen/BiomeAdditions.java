package net.greenjab.nekomasfixed.registry.worldgen;

import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * The mod's own 5 {@code SpawnPlacements.register(...)} calls stay Java (run from
 * {@code FMLCommonSetupEvent#enqueueWork}) - this is a DIFFERENT mechanism from the 6
 * {@code BiomeModifications.addSpawn(...)} calls that used to live here, which become 6
 * {@code data/nekomasfixed/forge/biome_modifier/} JSON files instead. The 6 addSpawn specs those
 * files encode (selector / MobCategory / entity / weight / min / max):
 * <pre>
 * includeByKey(dripstone_caves, plains)         MONSTER   SUSPICIOUS_SPIDER  30 1 2
 * tag(nekomasfixed:spawns_rime)                 MONSTER   RIME              100 4 4
 * tag(minecraft:is_jungle)                      MONSTER   DERELICT          100 4 4
 * tag(minecraft:is_ocean)                       MONSTER   DRENCHED            5 1 2
 * tag(minecraft:more_frequent_drowned_spawns)   MONSTER   DRENCHED            5 1 2
 * includeByKey(flower_forest, sunflower_plains, meadow)  CREATURE  MOOBLOOM  30 1 2
 * </pre>
 * The 4 {@code addFeature(...)} calls became {@code forge:add_features} JSON in the same directory.
 * Their specs (biome selector / placed feature / decoration step):
 * <pre>
 * savanna, savanna_plateau, windswept_savanna           nekomasfixed:baobab          VEGETAL_DECORATION
 * savanna, savanna_plateau, windswept_savanna, desert   nekomasfixed:mound           LOCAL_MODIFICATIONS
 * crimson_forest, nether_wastes                         nekomasfixed:geyser_feature  LOCAL_MODIFICATIONS
 * warm_ocean                                            nekomasfixed:clam            VEGETAL_DECORATION
 * </pre>
 * Explicit biome lists rather than tags, because that is what the selectors named one by one - no
 * vanilla biome tag matches any of these four sets exactly.
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
