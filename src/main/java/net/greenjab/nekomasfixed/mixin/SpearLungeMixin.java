package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// a spear carrier hitting glass or ice at speed goes through it. the speed is read at the head of the
// tick because the collision has already zeroed the delta movement by the tail.
@Mixin(Player.class)
public abstract class SpearLungeMixin {

    @Unique
    private double preCollisionSpeed = 0.0;

    @Unique
    private Vec3 preCollisionVelocity = Vec3.ZERO;

    @Unique
    private Vec3 lastTickVelocity = Vec3.ZERO;

    // no known-speed accessor on this version. a player's position can't stand in for it (the move packet resets
    // the old position), but the server's own delta movement is what elytra flight and its wall damage run on.
    @Inject(method = "tick", at = @At("HEAD"))
    private void captureMomentum(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        // the move packet can already have zeroed the delta by the time the tick starts, so last tick's is kept and the faster of the two used
        Vec3 now = player.getDeltaMovement();
        this.preCollisionVelocity = now.horizontalDistanceSqr() >= this.lastTickVelocity.horizontalDistanceSqr() ? now : this.lastTickVelocity;
        this.preCollisionSpeed = this.preCollisionVelocity.horizontalDistance();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPlayerTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        this.lastTickVelocity = player.getDeltaMovement();

        if (player.level().isClientSide() || !player.horizontalCollision) return;

        if (!player.getMainHandItem().is(ModTags.SPEARS)) return;

        // fast enough to mean an elytra dive or a lunge rather than a walk into a pane
        if (this.preCollisionSpeed < 1.2) return;

        Vec3 impactDirection = this.preCollisionVelocity.normalize().scale(0.5);
        AABB crashBox = player.getBoundingBox().expandTowards(impactDirection);

        ServerLevel serverLevel = (ServerLevel) player.level();
        boolean shatteredGlass = false;
        TagKey<Block> shatterTag = ModTags.SPEAR_SHATTER;

        Iterable<BlockPos> intersectingBlocks = BlockPos.betweenClosed(
                Mth.floor(crashBox.minX), Mth.floor(crashBox.minY), Mth.floor(crashBox.minZ),
                Mth.floor(crashBox.maxX), Mth.floor(crashBox.maxY), Mth.floor(crashBox.maxZ)
        );

        for (BlockPos pos : intersectingBlocks) {
            if (serverLevel.getBlockState(pos).is(shatterTag)) {
                serverLevel.destroyBlock(pos, false, player, 512);
                shatteredGlass = true;
            }
        }

        if (shatteredGlass) {
            float recoilDamage = (float) Mth.clamp(this.preCollisionSpeed * 3.0, 1.0, 4.0);
            player.hurt(player.damageSources().flyIntoWall(), recoilDamage);
            player.getMainHandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            player.setDeltaMovement(player.getDeltaMovement().scale(0.5));
        }
    }
}
