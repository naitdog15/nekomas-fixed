package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.item.RedstoneStrikerItem;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
 * Expires redstone-striker charges once their timer runs out, and rolls the copper-armour lightning
 * strike. Lightning selection is inlined in {@code tick(BooleanSupplier)} rather than sitting behind
 * a per-chunk hook, so both injectors share that method's head.
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
                    state.neighborChanged(level, pos, Blocks.AIR, pos, false);
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
        if (level.getRandom().nextInt(100) == 0 && NekomasFixedConfig.COPPER_BUFF.get() && bl && level.isThundering() && player != null) {
            int armor = getCopperArmor(player);
            if (armor > 0 && level.getRandom().nextInt(1400-200*armor) == 0) {
                BlockPos blockPos = player.blockPosition();
                if (level.isRainingAt(blockPos)) {
                    LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightningEntity != null) {
                        lightningEntity.moveTo(Vec3.atBottomCenterOf(blockPos));
                        level.addFreshEntity(lightningEntity);
                    }
                }
            }
        }
    }

    // Copper armour worn, counted a piece at a time. The mod's own copper crown always counts; the
    // rest of the tag is armour another mod supplies, so it is only counted while the option asking
    // for it is on.
    @Unique
    private static final EquipmentSlot[] COPPER_ARMOUR_SLOTS =
            { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD };

    @Unique
    private static int getCopperArmor(LivingEntity entity) {
        int worn = 0;
        for (EquipmentSlot slot : COPPER_ARMOUR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.is(ModTags.COPPER_ARMOUR)) continue;
            if (stack.is(ItemRegistry.COPPER_CROWN.get()) || NekomasFixedConfig.COPPER_ARMOUR_SET.get()) worn++;
        }
        return worn;
    }
}
