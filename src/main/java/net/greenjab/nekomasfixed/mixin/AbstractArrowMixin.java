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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A tipped arrow leaves a lingering cloud of its own potion wherever it lands - on a mob or in a
 * block - and loses that potion if it is picked back up.
 *
 * <p>The potion rides in the arrow's own pickup stack, which is where {@code Arrow} already keeps
 * it, and {@code onHitBlock} stands in for "the arrow has just embedded itself" since being stuck is
 * a plain field here rather than a setter call.
 */
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Shadow protected abstract ItemStack getPickupItem();

    @Inject(method = "doPostHurtEffects", at = @At("HEAD"))
    private void onHit(LivingEntity mob, CallbackInfo ci) {
        if (((AbstractArrow)(Object) this) instanceof Arrow arrowEntity && !arrowEntity.getTags().contains("areaEffect")) {
            ItemStack arrow = this.getPickupItem();
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
            ItemStack arrow = this.getPickupItem();
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
