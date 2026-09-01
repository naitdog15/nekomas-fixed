package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Skeleton has no .skeleton subpackage on 1.20.1 (VERIFIED: no such directory in
// forge-1.20.1-mapped-src's entity tree — matches AT L27's own "drop the .skeleton segment" note).
// Mob#convertTo has one overload here, convertTo(EntityType<T>, boolean): T (VERIFIED Mob.java:1205)
// — no ConversionParams, no post-conversion Consumer callback; the returned entity is customised
// directly afterward instead.
@Mixin(Skeleton.class)
public abstract class SkeletonMixin extends Monster {

    @Unique private int inWaterTime = 0;

    protected SkeletonMixin(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickDrenchedConversion(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel level && this.isAlive() && this.isEyeInFluid(FluidTags.WATER)) {
            this.inWaterTime++;
            if (this.inWaterTime >= 900) {
                var drenched = this.convertTo(EntityTypeRegistry.DRENCHED.get(), true);
                if (drenched != null) drenched.setVariant(this.random.nextInt(3));
                if (!this.isSilent()) level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_TO_DROWNED, this.blockPosition(), 0);
            }
        } else this.inWaterTime = 0;
    }
}