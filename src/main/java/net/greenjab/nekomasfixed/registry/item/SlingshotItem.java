package net.greenjab.nekomasfixed.registry.item;

import java.util.function.Predicate;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.vanillabackport.BackportedContent;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.SlingshotProjectile;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SlingshotItem extends ProjectileWeaponItem {

    public SlingshotItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player playerEntity)) return;
        ItemStack itemStack = playerEntity.getProjectile(stack);
        if (itemStack.isEmpty()) return;
        if (itemStack.is(Items.ARROW)) itemStack = new ItemStack(Items.IRON_NUGGET);
        float f = getPullProgress(this.getUseDuration(stack) - remainingUseTicks);
        if (f < 0.99F) return;

        if (!level.isClientSide) {
            boolean shatter = NekomasFixedConfig.SHATTER_ENCHANTMENT.get()
                    && NekomasFixed.enchantLevel(stack, "shatter") != 0;
            SlingshotProjectile projectile = new SlingshotProjectile(level, playerEntity, itemStack, stack, shatter);
            boolean heavyRound = itemStack.is(Items.AMETHYST_SHARD) || isResinClump(itemStack);
            float speed = f * 3.0F * (heavyRound ? (1 / 2F) : (2 / 3F));
            projectile.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, speed, 1.0F);
            level.addFreshEntity(projectile);

            stack.hurtAndBreak(1, playerEntity, entity -> entity.broadcastBreakEvent(playerEntity.getUsedItemHand()));
            if (!playerEntity.getAbilities().instabuild) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) playerEntity.getInventory().removeItem(itemStack);
            }
        }

        level.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        playerEntity.awardStat(Stats.ITEM_USED.get(this));
    }

    /**
     * A resin clump is the heaviest thing the slingshot will throw, so it leaves the sling at the
     * same reduced speed an amethyst shard does. The clump is not part of this version on its own -
     * it only exists while a mod is supplying it - so the round is asked for by name and quietly
     * ignored when nothing answers.
     */
    private static boolean isResinClump(ItemStack stack) {
        return NekomasFixedConfig.BACKPORTED_SLINGSHOT_AMMO.get()
                && BackportedContent.RESIN_CLUMP.isPresent()
                && stack.is(BackportedContent.RESIN_CLUMP.get());
    }

    public static float getPullProgress(int useTicks) {
        float f = useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 1.5F;
        if (f > 1.0F) f = 1.0F;
        return f;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        boolean bl = !user.getProjectile(itemStack).isEmpty();
        if (!user.getAbilities().instabuild && !bl) return InteractionResultHolder.fail(itemStack);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    public static final Predicate<ItemStack> SLINGSHOT_PROJECTILES = stack -> stack.is(ModTags.SLINGSHOT_PROJECTILES);

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return SLINGSHOT_PROJECTILES;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}
