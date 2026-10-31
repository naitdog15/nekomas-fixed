package net.greenjab.nekomasfixed.registry.item.quiver;

import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// bundle-shaped: the contents tooltip comes from ItemStackMixin (it reuses the container grid).
public class QuiverItem extends Item {
    private static final int FULL_BAR_COLOR = 0xFF5454;
    private static final int BAR_COLOR = 0x7087FF;

    public QuiverItem(Properties properties) {
        super(properties);
    }

    private static QuiverContents contents(ItemStack stack) {
        return new QuiverContents(StackData.readQuiver(stack));
    }

    public static int getArrowCount(ItemStack stack) {
        return contents(stack).getTotalArrowCount();
    }

    public static float getFullness(ItemStack stack) {
        return (float) getArrowCount(stack) / QuiverContents.MAX_ARROWS;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack quiver, Slot slot, ClickAction action, Player player) {
        if (quiver.getCount() != 1 || action != ClickAction.SECONDARY) return false;
        ItemStack target = slot.getItem();
        QuiverContents contents = contents(quiver);
        if (target.isEmpty()) {
            ItemStack removed = contents.removeOne();
            if (!removed.isEmpty()) {
                playRemoveOneSound(player);
                ItemStack leftover = slot.safeInsert(removed);
                if (!leftover.isEmpty()) contents.tryInsert(leftover);
                StackData.writeQuiver(quiver, contents.items);
            }
        } else if (QuiverContents.canItemBeInQuiver(target)) {
            int room = QuiverContents.MAX_ARROWS - contents.getTotalArrowCount();
            ItemStack taken = slot.safeTake(target.getCount(), room, player);
            int inserted = contents.tryInsert(taken);
            if (inserted > 0) playInsertSound(player);
            if (!taken.isEmpty()) slot.safeInsert(taken);
            StackData.writeQuiver(quiver, contents.items);
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack quiver, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (quiver.getCount() != 1) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;
        QuiverContents contents = contents(quiver);
        if (other.isEmpty()) {
            ItemStack removed = contents.removeOne();
            if (!removed.isEmpty()) {
                playRemoveOneSound(player);
                access.set(removed);
                StackData.writeQuiver(quiver, contents.items);
            }
        } else {
            int inserted = contents.tryInsert(other);
            if (inserted > 0) {
                playInsertSound(player);
                StackData.writeQuiver(quiver, contents.items);
            }
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        QuiverContents contents = contents(stack);
        ItemStack removed = contents.removeOne();
        if (removed.isEmpty()) return InteractionResultHolder.fail(stack);
        if (player instanceof ServerPlayer) player.drop(removed, true);
        StackData.writeQuiver(stack, contents.items);
        playDropContentsSound(player);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getArrowCount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.min(1 + 12 * getArrowCount(stack) / QuiverContents.MAX_ARROWS, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return getArrowCount(stack) >= QuiverContents.MAX_ARROWS ? FULL_BAR_COLOR : BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(getArrowCount(stack) + "/" + QuiverContents.MAX_ARROWS).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        ItemUtils.onContainerDestroyed(entity, contents(entity.getItem()).items.stream());
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
