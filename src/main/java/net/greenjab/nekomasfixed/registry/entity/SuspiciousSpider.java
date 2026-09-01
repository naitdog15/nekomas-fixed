package net.greenjab.nekomasfixed.registry.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class SuspiciousSpider extends Spider {

    public SuspiciousSpider(EntityType<? extends Spider> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createSuspiciousSpiderAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0F).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData) {
        entityData = super.finalizeSpawn(level, difficulty, spawnReason, entityData);
        if (entityData instanceof Spider.SpiderEffectsGroupData spiderData) {
            spiderData.setRandomEffect(random);
            Holder<MobEffect> registryEntry = spiderData.effect;
            if (registryEntry != null) this.addEffect(new MobEffectInstance(registryEntry, -1));
        }
        return entityData;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean bl = super.doHurtTarget(target);
        if (bl && target instanceof LivingEntity)
            ((LivingEntity)target).addEffect(getRandomStatusEffectOnHit());
        return bl;
    }

    private MobEffectInstance getRandomStatusEffectOnHit(){
        int i = random.nextInt(4);
        return switch (i) {
            case 0 -> new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true);
            case 1 -> new MobEffectInstance(MobEffects.BLINDNESS, 200, 1, false, true);
            case 2 -> new MobEffectInstance(MobEffects.POISON, 200, 1, false, true);
            default -> new MobEffectInstance(MobEffects.WITHER, 200, 1, false, true);
        };
    }

    public static boolean canSpawn(
            EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random
    ) {
        return checkMonsterSpawnRules(type, level, spawnReason, pos, random) && (spawnReason == MobSpawnType.SPAWNER || !level.canSeeSky(pos));
    }
}
