package net.greenjab.nekomasfixed.registry.item;

import net.greenjab.nekomasfixed.registry.entity.CustomArrow;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomArrowItem extends ArrowItem {
    public CustomArrowItem(Properties properties) {
        super(properties);
    }

    // same shape as vanilla's arrow dispenser entry; must run after item registration
    public static void registerDispenserBehaviour() {
        DispenserBlock.registerBehavior(ItemRegistry.TIPPED_ARROW_CUSTOM.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                CustomArrow arrow = CustomArrow.createAt(level, position.x(), position.y(), position.z(), stack);
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        return CustomArrow.create(level, shooter, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        for (Potion potion : StackData.readTippedPotions(stack)) {
            tooltip.add(Component.translatable(potion.getName("item.minecraft.potion.effect."))
                    .withStyle(Style.EMPTY.withColor(PotionUtils.getColor(potion))));
        }
    }
}
