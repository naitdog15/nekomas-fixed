package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.item.RedstoneStrikerItem;
import net.greenjab.nekomasfixed.screen.config.ModConfigValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * 1.20.1 deltas: {@code EntitySpawnReason}/{@code EntityTypes} (plural holder) do not exist — see
 * boat.MobMixin/boat.PatrolSpawnerMixin's matching notes. {@code ServerLevel} has no
 * {@code tickThunder(LevelChunk)} method at all on 1.20.1 (VERIFIED: zero matches) — its own
 * lightning-strike selection is inlined directly in {@code tick(BooleanSupplier)}
 * (ServerLevel.java:407-423), so this retargets onto the same {@code tick} HEAD the other injector
 * here already uses, rather than a per-chunk hook that does not exist. This is moot regardless: see
 * EntityMixin's copper-armor NAMED GAP note, which applies identically here.
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow
    public abstract @Nullable ServerPlayer getRandomPlayer();

    @Inject(method = "tick", at = @At("HEAD"))
    private void depowerRedstoneStruckBlocks(BooleanSupplier haveTime, CallbackInfo ci) {
        ServerLevel level = ((ServerLevel)(Object)this);
        HashMap<GlobalPos, Long> STRUCK_WIRES_COPY = new HashMap<>(RedstoneStrikerItem.STRUCK_WIRES);
        for (Map.Entry<GlobalPos, Long> entry : STRUCK_WIRES_COPY.entrySet()) {
            if (level.getGameTime() > entry.getValue()) {
                GlobalPos Gpos = entry.getKey();
                if (level.dimension() == Gpos.dimension()) {
                    BlockPos pos = Gpos.pos();
                    BlockState state = level.getBlockState(pos);
                    RedstoneStrikerItem.STRUCK_WIRES.remove(Gpos);
                    state.handleNeighborChanged(level, pos, Blocks.AIR, null, false);
                    level.updateNeighborsAt(pos, state.getBlock());
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickThunder(BooleanSupplier haveTime, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object)this;
        boolean bl = level.isRaining();
        ServerPlayer player = this.getRandomPlayer();
        if (level.getRandom().nextInt(100) == 0 && ModConfigValues.enableCopperBuff && bl && level.isThundering() && player != null) {
            int armor = getCopperArmor(player);
            if (armor > 0 && level.getRandom().nextInt(1400-200*armor) == 0) {
                BlockPos blockPos = player.blockPosition();
                if (level.isRainingAt(blockPos)) {
                    LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightningEntity != null) {
                        lightningEntity.snapTo(Vec3.atBottomCenterOf(blockPos));
                        level.addFreshEntity(lightningEntity);
                    }
                }
            }
        }
    }

    // See EntityMixin's matching note: copper armor does not exist on 1.20.1, so this is
    // permanently 0 (NAMED GAP, not a feature cut).
    @Unique
    private static int getCopperArmor(LivingEntity entity) {
        return 0;
    }
}
