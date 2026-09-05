package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.greenjab.nekomasfixed.registry.other.AnimalTooltipData;
import net.greenjab.nekomasfixed.registry.other.ContainerTooltipData;
import net.greenjab.nekomasfixed.registry.other.StoredTimeComponent;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// tooltip-image dispatch: none of TooltipDisplay/DataComponents.TOOLTIP_DISPLAY/addToTooltip exist
// on 1.20.1, so this reads BlockItem.getBlockEntityData(stack)'s "Items" list and StackData's
// "animal" key directly instead - and there's no per-component HideFlags bit for either, so a pack
// that hid these on other versions has no lever left here.
// the tooltip *text* half lives in ItemMixin's appendHoverText hook instead, not here.
// use(): still returns InteractionResultHolder<ItemStack>, not a bare InteractionResult.
// hasFoil: PotionUtils.getPotion(ItemStack) returns a plain Potion, not Optional<Holder<Potion>>.
@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
	private void modTooltipImage(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
		ItemStack stack = (ItemStack)(Object)this;
		List<ItemStack> contents = containerContents(stack);
		if (NekomasFixedClientConfig.enabled(NekomasFixedClientConfig.CONTAINER_GRID_TOOLTIPS) && !contents.isEmpty()) {
			cir.setReturnValue(Optional.of(new ContainerTooltipData(contents)));
		} else if (StackData.contains(stack, StackData.KEY_ANIMAL)) {
			cir.setReturnValue(Optional.of(new AnimalTooltipData(StackData.readAnimal(stack))));
		}
	}

	// empty result falls through to vanilla deliberately: a bundle keeps its contents outside
	// BlockEntityTag so it never matches here and keeps its own BundleTooltip, and an empty shulker
	// box keeps no image. empty slots are dropped so the grid packs from the top-left; 27 is three
	// rows of nine, a bigger container is truncated not wrapped.
	@Unique
	private static List<ItemStack> containerContents(ItemStack stack) {
		CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
		if (blockEntityTag == null || !blockEntityTag.contains("Items", Tag.TAG_LIST)) {
			return List.of();
		}
		ListTag items = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
		List<ItemStack> contents = new ArrayList<>();
		for (int i = 0; i < items.size() && contents.size() < 27; i++) {
			ItemStack content = ItemStack.of(items.getCompound(i));
			if (!content.isEmpty()) {
				contents.add(content);
			}
		}
		return contents;
	}

	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
	private void useBlockItem(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = (ItemStack)(Object)this;
		if (stack.is(ModTags.CLAMTAG)) {
			int c = StackData.readClamState(stack) > 0 ? 0 : 1;
			// 1.20.1 has no DataComponents.CONTAINER/ItemContainerContents: a clam's salvaged contents
			// live in the same "BlockEntityTag" -> "Items" NBT vanilla shulker boxes use for theirs.
			CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
			if (c > 0 && blockEntityTag != null && blockEntityTag.contains("Items")
					&& !blockEntityTag.getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
				c++;
			}
			StackData.writeClamState(stack, c);
		}
		if (stack.is(Items.CLOCK)) {
			// "has a time recorded" is a presence question, not a value comparison - midnight lands on
			// exactly 0, and StackData.writeStoredTime's default-elision would drop that write, so this
			// writes through the generic core instead.
			if (StackData.contains(stack, StackData.KEY_STORED_TIME)) {
				StackData.remove(stack, StackData.KEY_STORED_TIME);
			} else {
				// no Level#getOverworldClockTime() on 1.20.1; substituted with this dimension's own
				// raw day-time mod 24000 - a clock outside the overworld may show a different face.
				int recorded = (int) ((level.getDayTime() + 6000) % 24000);
				StackData.write(stack, StackData.KEY_STORED_TIME, StoredTimeComponent.CODEC,
						new StoredTimeComponent(recorded));
			}
			player.swing(hand);
		}
	}

	@Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
	private void lightningGlint(CallbackInfoReturnable<Boolean> cir){
		ItemStack stack = (ItemStack)(Object)this;
		Potion potion = PotionUtils.getPotion(stack);
		if (potion == ItemRegistry.LIGHTNING.get()) {
			cir.setReturnValue(true);
		}
	}
}
