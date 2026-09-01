package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Zombie has no .zombie subpackage on 1.20.1 (same dropped-segment pattern as Skeleton). EntityTypes
// (plural holder) is EntityType (see boat.PatrolSpawnerMixin's note). Mob#convertTo has one overload,
// convertTo(EntityType<T>, boolean): T (see SkeletonMixin's matching note) — no ConversionParams, no
// callback. The unnamed `_` lambda parameter (JEP 456, Java 21+) is not legal at this mod's Java 17
// compatibility level and is given a name instead.
@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

    @Shadow
    public abstract EntityType<? extends Zombie> getType();

    @Unique private int inPowderSnowTime = 0;

    protected ZombieMixin(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickDrenchedConversion(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel level && this.isAlive() && this.isInPowderSnow && this.getType() == EntityType.ZOMBIE) {
            this.inPowderSnowTime++;
            if (this.inPowderSnowTime >= 450) {
                Zombie ZE = (Zombie)(Object)this;
                ZE.convertTo(EntityTypeRegistry.RIME.get(), true);
                if (!this.isSilent()) level.levelEvent(null, LevelEvent.SOUND_SKELETON_TO_STRAY, this.blockPosition(), 0);
            }
        } else this.inPowderSnowTime = 0;
    }
}