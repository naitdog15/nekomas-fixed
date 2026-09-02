package net.greenjab.nekomasfixed.registry.worldgen;

import net.greenjab.nekomasfixed.registry.entity.Drenched;
import net.greenjab.nekomasfixed.registry.entity.SuspiciousSpider;
import net.greenjab.nekomasfixed.registry.entity.WildFire.WildfireEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Where each of this mod's mobs is allowed to stand when the game tries to spawn it. These 5
 * {@code SpawnPlacements.register(...)} calls have to be Java and run once, from
 * {@code FMLCommonSetupEvent#enqueueWork}.
 * <p>
 * Which biomes those mobs actually appear in, and where the mod's features generate, is a separate
 * mechanism and lives entirely in {@code data/nekomasfixed/forge/biome_modifier/}. Recorded here
 * because the two halves only make sense together, and a spawn that never shows up is usually a
 * missing JSON rather than a missing placement rule.
 * <p>
 * The 6 {@code forge:add_spawns} files, as biome selector / category / entity / weight / min / max:
 * <pre>
 * dripstone_caves, plains                       monster   suspicious_spider  30 1 2
 * #nekomasfixed:spawns_rime                     monster   rime              100 4 4
 * #minecraft:is_jungle                          monster   derelict          100 4 4
 * #minecraft:is_ocean                           monster   drenched            5 1 2
 * #minecraft:more_frequent_drowned_spawns       monster   drenched            5 1 2
 * flower_forest, sunflower_plains, meadow       creature  moobloom           30 1 2
 * </pre>
 * The 4 {@code forge:add_features} files, as biome selector / placed feature / decoration step:
 * <pre>
 * savanna, savanna_plateau, windswept_savanna           nekomasfixed:baobab          vegetal_decoration
 * savanna, savanna_plateau, windswept_savanna, desert   nekomasfixed:mound           local_modifications
 * crimson_forest, nether_wastes                         nekomasfixed:geyser_feature  local_modifications
 * warm_ocean                                            nekomasfixed:clam            vegetal_decoration
 * </pre>
 * Those four name their biomes one by one rather than using a tag, because no vanilla biome tag
 * matches any of the four sets exactly.
 * <p>
 * The three features also answer to their own switches in the config, read inside
 * {@code Feature#place} rather than here - a biome modifier is plain data and cannot be conditioned
 * on a config value, and reading it at the feature keeps the switch honest even when a datapack
 * places the feature somewhere else. {@code naturalMobSpawns} works the same way, from a spawn
 * placement check on the event bus.
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
