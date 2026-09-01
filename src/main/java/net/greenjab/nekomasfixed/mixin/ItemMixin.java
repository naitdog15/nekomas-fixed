package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.other.StoredTimeComponent;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

	// 1.20.1 delta: ComponentRegistry.STORED_TIME (a DataComponentType, 1.20.5+) is replaced by
	// StackData's NBT facade. "hasNonDefault" becomes "value != default", which
	// is exactly StackData's own never-write-defaults invariant read back.
	// Item#isFoil(ItemStack) itself is unchanged: VERIFIED forge-1.20.1-mapped-src Item.java:284
	// has the identical (ItemStack) -> boolean signature this injector already used.
	@Inject(method="isFoil", at = @At(value = "HEAD"), cancellable = true)
	private void clockHasStoredTime(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (!StackData.readStoredTime(itemStack).equals(new StoredTimeComponent(0))) cir.setReturnValue(true);
	}

	// Unchanged: Item#onUseTick(Level, LivingEntity, ItemStack, int) has the identical signature on
	// 1.20.1 (VERIFIED forge-1.20.1-mapped-src Item.java:112).
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

	/**
	 * The vanilla-clock placement hijack, re-homed here from the deleted-content
	 * {@code ItemsMixin} (see that file's own header comment). {@code Items.<clinit>} runs inside
	 * {@code Bootstrap.bootStrap()}, strictly before {@code RegisterEvent}, so intercepting the
	 * {@code Items.CLOCK} field assignment itself (what {@code ItemsMixin} did on 26.2) is
	 * architecturally impossible on Forge; this HEAD injection on {@code Item#useOn}, gated on
	 * identity, is the chosen replacement.
	 * <p>
	 * This used to call a mixin-package
	 * {@code ClockPlacement.INSTANCE} that constructed a never-registered
	 * {@code StandingAndWallBlockItem} — guaranteed {@code IllegalStateException} because every
	 * {@code Item} ctor calls {@code BuiltInRegistries.ITEM.createIntrusiveHolder(this)}
	 * (forge-1.20.1-mapped-src {@code Item.java:61}). That class is deleted; this now calls
	 * {@link net.greenjab.nekomasfixed.registry.block.ClockPlacement#place(UseOnContext)}, which
	 * reproduces {@code StandingAndWallBlockItem#getPlacementState} + {@code BlockItem#place}
	 * against the two Block instances directly and instantiates no {@code Item} at all.
	 */
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void nekomasfixed$clockPlacesBlock(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
		if ((Object) this != Items.CLOCK) return;
		cir.setReturnValue(net.greenjab.nekomasfixed.registry.block.ClockPlacement.place(ctx));
	}
}
