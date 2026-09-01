package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.1 deltas (all VERIFIED against forge-1.20.1-mapped-src): {@code AbstractArrow}/{@code Arrow}
 * live directly under {@code net.minecraft.world.entity.projectile}, no {@code .arrow} subpackage.
 * {@code DataComponents.POTION_CONTENTS}/{@code PotionContents} do not exist (1.20.5+); the potion is
 * read via {@code PotionUtils.getPotion(ItemStack): Potion} and applied via
 * {@code AreaEffectCloud#setPotion(Potion)} (AreaEffectCloud.java:94), the same substitution
 * ItemStackMixin's {@code lightningGlint} already makes. {@code getPickupItemStackOrigin()} does not
 * exist; the one pickup-item accessor is {@code getPickupItem(): ItemStack}
 * (AbstractArrow.java:528), which is what {@code Arrow}'s own tipped-arrow pickup logic already
 * bakes potion data into. {@code setInGround(boolean)} does not exist — {@code inGround} is a plain
 * protected field written directly at several call sites, with no single method call standing in
 * for "just embedded in a block"; the closest true equivalent is {@code onHitBlock(BlockHitResult)}
 * (AbstractArrow.java:422), which this retargets onto instead.
 */
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "doPostHurtEffects", at = @At("HEAD"))
    private void onHit(LivingEntity mob, CallbackInfo ci) {
        if (((AbstractArrow)(Object) this) instanceof Arrow arrowEntity && !arrowEntity.getTags().contains("areaEffect")) {
            ItemStack arrow = arrowEntity.getPickupItem();
            Potion potion = PotionUtils.getPotion(arrow);
            if (potion != null && potion != net.minecraft.world.item.alchemy.Potions.EMPTY) {
                AreaEffectCloud areaEffectCloudEntity = makeAreaEffectCloudEntity(mob.level(), mob.getX(), mob.getY(), mob.getZ(), potion);
                mob.level().addFreshEntity(areaEffectCloudEntity);
                arrowEntity.addTag("areaEffect");
            }
        }
    }

    @Inject(method = "onHitBlock", at = @At("HEAD"))
    private void onHitBlock(BlockHitResult result, CallbackInfo ci) {
        if (((AbstractArrow)(Object) this) instanceof Arrow arrowEntity && !arrowEntity.getTags().contains("areaEffect")) {
            ItemStack arrow = arrowEntity.getPickupItem();
            Potion potion = PotionUtils.getPotion(arrow);
            if (potion != null && potion != net.minecraft.world.item.alchemy.Potions.EMPTY) {
                AreaEffectCloud areaEffectCloudEntity = makeAreaEffectCloudEntity(arrowEntity.level(), arrowEntity.getX(), arrowEntity.getY(), arrowEntity.getZ(), potion);
                arrowEntity.level().addFreshEntity(areaEffectCloudEntity);
                arrowEntity.addTag("areaEffect");
            }
        }
    }

    @ModifyExpressionValue(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPickupItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack removeEffectsIfPiecing(ItemStack original) {
        AbstractArrow PPE = (AbstractArrow)(Object) this;
        if (PPE instanceof Arrow) return Items.ARROW.getDefaultInstance();
        return original;
    }

    @Unique
    @NotNull
    private static AreaEffectCloud makeAreaEffectCloudEntity(Level target, double target1, double target2, double target3, Potion potion) {
        AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(target, target1, target2, target3);
        areaEffectCloudEntity.setRadius(2.0F);
        areaEffectCloudEntity.setRadiusOnUse(0F);
        areaEffectCloudEntity.setDuration(100);
        areaEffectCloudEntity.setWaitTime(5);
        areaEffectCloudEntity.setPotion(potion);
        areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
        return areaEffectCloudEntity;
    }

}
