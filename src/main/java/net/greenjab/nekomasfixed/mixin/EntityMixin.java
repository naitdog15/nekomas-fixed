package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.screen.config.ModConfigValues;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void tickThunder(ServerLevel world, LightningBolt lightning, CallbackInfo ci) {
        if (ModConfigValues.enableCopperBuff) {
            if ((Entity)(Object)this instanceof ServerPlayer player) {
                int armor = getCopperArmor(player);
                if (armor > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED, 3 * armor * 20, armor, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, armor, false, false, false));
                }
            }
        }
    }

    @Unique
    private static int getCopperArmor(LivingEntity entity) {
        int i =0;
        if (entity.getItemBySlot(EquipmentSlot.FEET).is(Items.COPPER_BOOTS)) i++;
        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(Items.COPPER_LEGGINGS)) i++;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.COPPER_CHESTPLATE)) i++;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.COPPER_HELMET)) i++;
        return i;
    }
}
