package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.other.StoredTimeComponent;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Two of the original four injectors are dropped here, not ported — {@code getTooltipImage}'s
 * container/animal tooltip-image dispatch and {@code addDetailsToTooltip}'s animal/stored-time/combo
 * tooltip-text dispatch. Both hang off a 1.21+ mechanism with no 1.20.1 counterpart at all: {@code
 * TooltipDisplay}, {@code DataComponents.TOOLTIP_DISPLAY}, {@code Item.TooltipContext} and {@code
 * ItemStack#addToTooltip(DataComponentType, ...)} do not exist here (VERIFIED: none of the four
 * appear anywhere under forge-1.20.1-mapped-src). The 1.20.1 route is
 * {@code Item#appendHoverText}/{@code Item#getTooltipImage}, which is a rewrite rather than a
 * retarget. <b>Tooltip behaviour this port does NOT yet restore:</b> the container-contents
 * tooltip image (shulker boxes / bundle-style items), the nautilus animal tooltip image, the animal
 * summary tooltip line ({@code AnimalComponent#tooltipLine()}, already written and waiting — see
 * {@code registry/other/AnimalComponent.java}), the stored-time HH:MM tooltip line ({@code
 * StoredTimeComponent#formatted()}, likewise already written), and the sickle combo-percentage
 * tooltip line (whose original wording could not be recovered).
 * <p>
 * The other two injectors port cleanly:
 * <p>
 * {@code use}: {@code ItemStack#use(Level, Player, InteractionHand)} itself is unchanged, but on
 * 1.20.1 it still returns {@code InteractionResultHolder<ItemStack>} — VERIFIED
 * forge-1.20.1-mapped-src ItemStack.java:274 — not the bare {@code InteractionResult} 26.2 uses, so
 * the handler's {@code CallbackInfoReturnable} type parameter and the {@code @At} INVOKE
 * descriptor's return type both change accordingly. {@code ComponentRegistry.CLAM_STATE}/{@code
 * STORED_TIME} become {@code StackData} calls.
 * <p>
 * {@code hasFoil}: unchanged target. {@code DataComponents.POTION_CONTENTS}/{@code PotionContents}
 * (1.20.5+) become {@code PotionUtils.getPotion(ItemStack)}, which returns a plain {@code Potion}
 * (not {@code Optional<Holder<Potion>>}) on 1.20.1 — VERIFIED forge-1.20.1-mapped-src
 * PotionUtils.java:119. {@code ItemRegistry.LIGHTNING.get()} becomes a {@code RegistryObject<Potion>}
 * on its new registry row (1.20.1 registers a plain {@code Potion} instance, not
 * {@code registerForHolder}), so the comparison gains {@code .get()}.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

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
			if (!StackData.readStoredTime(stack).equals(new StoredTimeComponent(0))) {
				StackData.remove(stack, StackData.KEY_STORED_TIME);
			} else {
				// 1.20.1 has no Level#getOverworldClockTime() (26.2-only cross-dimension normalisation);
				// substituted with this dimension's own raw day-time mod 24000. Note: a clock
				// stopped outside the overworld may show a different face than 26.2's would have.
				StackData.writeStoredTime(stack, new StoredTimeComponent((int) ((level.getDayTime() + 6000) % 24000)));
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
