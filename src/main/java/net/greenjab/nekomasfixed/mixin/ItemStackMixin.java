package net.greenjab.nekomasfixed.mixin;

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

/**
 * The tooltip-image dispatch is rewritten rather than retargeted. The 26.2 version keyed off
 * {@code DataComponents.CONTAINER} and the mod's own {@code ANIMAL} component, consulting
 * {@code TooltipDisplay} for whether each was hidden; none of {@code TooltipDisplay},
 * {@code DataComponents.TOOLTIP_DISPLAY}, {@code Item.TooltipContext} or
 * {@code ItemStack#addToTooltip(DataComponentType, ...)} exists on 1.20.1. The equivalents used
 * below are {@code BlockItem.getBlockEntityData(stack)}'s {@code "Items"} list — the NBT vanilla
 * containers actually keep their contents in — and {@code StackData}'s {@code "animal"} key. There
 * is no per-component hide flag to honour either: tooltip hiding here is {@code HideFlags}, which
 * has no bit for either payload, so a pack that hid these two on 26.2 has no lever left.
 * <p>
 * The tooltip <i>text</i> half of the same 26.2 injector does not live here. The text hook on this
 * version is {@code Item#appendHoverText}: the animal summary and the clock's recorded time come
 * from the base-class injection in {@code ItemMixin} (a block item reaches it because
 * {@code BlockItem}'s override calls super first), and the sickle's combo lines from its own
 * override.
 * <p>
 * The remaining two injectors port cleanly:
 * <p>
 * {@code use}: {@code ItemStack#use(Level, Player, InteractionHand)} itself is unchanged, but on
 * 1.20.1 it still returns {@code InteractionResultHolder<ItemStack>}, not the bare
 * {@code InteractionResult} 26.2 uses, so the handler's {@code CallbackInfoReturnable} type
 * parameter and the {@code @At} INVOKE descriptor's return type both change accordingly.
 * {@code ComponentRegistry.CLAM_STATE}/{@code STORED_TIME} become {@code StackData} calls.
 * <p>
 * {@code hasFoil}: unchanged target. {@code DataComponents.POTION_CONTENTS}/{@code PotionContents}
 * (1.20.5+) become {@code PotionUtils.getPotion(ItemStack)}, which returns a plain {@code Potion}
 * rather than {@code Optional<Holder<Potion>>}. {@code ItemRegistry.LIGHTNING} is a
 * {@code RegistryObject<Potion>} on its new registry row (1.20.1 registers a plain {@code Potion}
 * instance, not {@code registerForHolder}), so the comparison gains {@code .get()}.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
	private void modTooltipImage(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
		ItemStack stack = (ItemStack)(Object)this;
		List<ItemStack> contents = containerContents(stack);
		if (!contents.isEmpty()) {
			cir.setReturnValue(Optional.of(new ContainerTooltipData(contents)));
		} else if (StackData.contains(stack, StackData.KEY_ANIMAL)) {
			cir.setReturnValue(Optional.of(new AnimalTooltipData(StackData.readAnimal(stack))));
		}
	}

	/**
	 * The stacks a container item is carrying, empty when it is not a container or is carrying
	 * nothing. An empty result deliberately falls through to vanilla: that leaves a bundle on its own
	 * {@code BundleTooltip} (a bundle keeps its contents in the stack's own {@code "Items"}, not under
	 * {@code BlockEntityTag}, so it never matches here) and leaves an empty shulker box with no image
	 * at all, both exactly as vanilla had them.
	 * <p>
	 * Empty slots are dropped rather than kept in place, so the grid packs from the top-left — the
	 * same shape the replaced component held, which stored only the non-empty stacks. The 27 is the
	 * grid's own limit of three rows of nine; a bigger container is truncated, not wrapped.
	 */
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
			// 1.20.1 has no DataComponents.CONTAINER/ItemContainerContents: a placed-and-picked-up
			// clam's salvaged contents live in the same NBT vanilla shulker boxes use for theirs —
			// BlockItem.getBlockEntityData(stack) -> the "BlockEntityTag" sub-compound's "Items" list.
			CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
			if (c > 0 && blockEntityTag != null && blockEntityTag.contains("Items")
					&& !blockEntityTag.getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
				c++;
			}
			StackData.writeClamState(stack, c);
		}
		if (stack.is(Items.CLOCK)) {
			// "Has a time recorded" is a presence question, not a value comparison. Midnight lands on
			// exactly 0 here, and the never-write-defaults rule would drop a 0 straight back off the
			// stack — so the value is written with the generic core rather than through
			// StackData.writeStoredTime, whose default-elision would make a clock stopped at midnight
			// silently refuse to record. Every reader of this key (the glint in ItemMixin, the
			// tooltip line) asks the same presence question.
			if (StackData.contains(stack, StackData.KEY_STORED_TIME)) {
				StackData.remove(stack, StackData.KEY_STORED_TIME);
			} else {
				// 1.20.1 has no Level#getOverworldClockTime() (26.2-only cross-dimension normalisation);
				// substituted with this dimension's own raw day-time mod 24000. Note: a clock
				// stopped outside the overworld may show a different face than 26.2's would have.
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
