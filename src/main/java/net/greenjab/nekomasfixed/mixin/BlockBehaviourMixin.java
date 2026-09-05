package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.block.GoatHornBlock;
import net.greenjab.nekomasfixed.registry.block.enums.GoatHornType;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 1.20.1: no split useItemOn(...) — retargeted onto combined use(state, level, pos, player, hand,
// hitResult); item comes from player.getItemInHand(hand).
// 1.20.1: no DataComponents.INSTRUMENT/InstrumentComponent (1.20.5+) — instrument id read off the
// stack's "instrument" NBT string tag instead, resolved to a ResourceKey<Instrument>.
// GoatHornType.fromInstrument needed a ResourceKey<Instrument> overload since it no longer gets an
// InstrumentComponent to unwrap.
@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void customOnUseWithItem(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.getBlockState(pos).is(BlockTags.REPLACEABLE) && state.isFaceSturdy(level, pos, hitResult.getDirection())) {
            if (itemStack.is(Items.GOAT_HORN)) {
                Direction direction = hitResult.getDirection();

                if (direction.getAxis().isVertical()) {
                    direction = player.getDirection();
                }

                Direction facing = direction.getOpposite();
                BlockPos placePos = pos.relative(direction);
                FluidState fluidState = level.getFluidState(placePos);

                ResourceKey<Instrument> instrumentKey = nekomasfixed$readInstrument(itemStack);
                if (instrumentKey == null) return;

                GoatHornType hornType = GoatHornType.fromInstrument(instrumentKey);

                BlockState newState = BlockRegistry.GOAT_HORN.get().defaultBlockState()
                        .setValue(GoatHornBlock.HORN, hornType)
                        .setValue(HorizontalDirectionalBlock.FACING, facing)
                        .setValue(GoatHornBlock.WATERLOGGED, fluidState.getType() == Fluids.WATER);

                if (level.getBlockState(placePos).is(BlockTags.REPLACEABLE) && state.isFaceSturdy(level, pos, hitResult.getDirection())) {
                    level.setBlockAndUpdate(placePos, newState);
                    level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    itemStack.shrink(1);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }

    // mirrors vanilla InstrumentItem's private getInstrument, minus its tag-fallback branch.
    @Unique
    private static ResourceKey<Instrument> nekomasfixed$readInstrument(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("instrument", 8)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("instrument"));
            if (id != null) {
                return ResourceKey.create(Registries.INSTRUMENT, id);
            }
        }
        return null;
    }
}
