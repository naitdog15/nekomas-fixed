package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.util.SpottedSheepAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Gives a sheep a "spotted" flag of its own, tracked and saved alongside vanilla's wool state. It is
 * synched rather than a plain field because the renderer needs it on the client.
 *
 * <p>Leave the {@code "Spotted"} NBT key exactly as it is - changing it, even by case, un-spots
 * every sheep in every existing world.
 */
@Mixin(Sheep.class)
public abstract class SheepMixin extends Animal implements SpottedSheepAccess {

    protected SheepMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private static final EntityDataAccessor<Boolean> SPOTTED = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void initSpottedTracker(CallbackInfo ci) {
        this.entityData.define(SPOTTED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeSpottedNbt(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("Spotted", this.entityData.get(SPOTTED));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readSpottedNbt(CompoundTag tag, CallbackInfo ci) {
        this.entityData.set(SPOTTED, tag.getBoolean("Spotted"));
    }

    @Override
    public boolean nekomasfixed$isSpotted() {
        return this.entityData.get(SPOTTED);
    }

    @Override
    public void nekomasfixed$setSpotted(boolean spotted) {
        this.entityData.set(SPOTTED, spotted);
    }
}
