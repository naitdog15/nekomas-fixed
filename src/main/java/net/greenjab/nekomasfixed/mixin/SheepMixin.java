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
 * A verbatim port, not a redesign. The server-synced accessor already existed; the three 1.20.1
 * deltas are all VERIFIED against forge-1.20.1-mapped-src Sheep.java: {@code defineSynchedData()}
 * takes NO {@code Builder} argument at all (line 139 — not even the 26.2 kind, so
 * {@link #initSpottedTracker} writes straight to {@code this.entityData});
 * {@code addAdditionalSaveData}/{@code readAdditionalSaveData} take a plain {@code CompoundTag}, not
 * {@code ValueInput}/{@code ValueOutput} (lines 270,276); and the class is
 * {@code net.minecraft.world.entity.animal.Sheep} — no {@code .sheep} subpackage.
 * <p>
 * Preserve the {@code "Spotted"} NBT key exactly — it un-spots every sheep in every existing world
 * if it changes even by case.
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
