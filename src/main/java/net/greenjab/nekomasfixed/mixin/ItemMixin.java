package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

	// presence of the recorded-time key is the question, not its value against a default - a clock
	// stopped at midnight records 0, and comparing against a zero default would read that as unset.
	@Inject(method="isFoil", at = @At(value = "HEAD"), cancellable = true)
	private void clockHasStoredTime(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (StackData.contains(itemStack, StackData.KEY_STORED_TIME)) cir.setReturnValue(true);
	}

	// hook is Item#appendHoverText; a block item reaches this too since BlockItem's override calls super first.
	@Inject(method = "appendHoverText", at = @At("HEAD"))
	private void modTooltipText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
	                            TooltipFlag flag, CallbackInfo ci) {
		if (StackData.contains(stack, StackData.KEY_ANIMAL)) {
			StackData.readAnimal(stack).tooltipLine().ifPresent(tooltip::add);
		}
		if (stack.is(Items.CLOCK) && StackData.contains(stack, StackData.KEY_STORED_TIME)) {
			tooltip.add(StackData.readStoredTime(stack).tooltipLine());
		}
	}

	@Inject(method="onUseTick", at=@At("HEAD"))
	private void customUsageTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining, CallbackInfo ci) {
		if (!level.isClientSide() || !itemStack.is(Items.GOAT_HORN)) return;

		if (livingEntity.tickCount % 3 == 0 && !level.isClientSide() ) {
            ServerLevel serverLevel = (ServerLevel) level;
			if(serverLevel.structureManager().getStructureWithPieceAt(livingEntity.blockPosition(), StructureTags.VILLAGE).isValid()) {
				serverLevel.sendParticles(ParticleTypes.POOF, livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(),
						20, 0.4, 0.2, 0.4, 0.05);
			}
		}
	}

	// Items.<clinit> runs inside Bootstrap.bootStrap(), strictly before any registration event, so
	// Items.CLOCK's field assignment can't be intercepted there - hence this HEAD injection on useOn
	// instead. can't build a fresh StandingAndWallBlockItem to delegate to either: every Item
	// constructor registers an intrusive holder, which would throw at registry freeze. ClockPlacement.place
	// reproduces the vanilla placement logic directly against the Block instances, no Item involved.
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void nekomasfixed$clockPlacesBlock(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
		if ((Object) this != Items.CLOCK) return;
		cir.setReturnValue(net.greenjab.nekomasfixed.registry.block.ClockPlacement.place(ctx));
	}
}
