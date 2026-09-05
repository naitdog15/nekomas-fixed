package net.greenjab.nekomasfixed.registry.block.cauldron;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

import static net.minecraft.core.cauldron.CauldronInteraction.EMPTY;

// EMPTY is vanilla's own shared mutable CauldronInteraction.EMPTY map, so this must run inside
// FMLCommonSetupEvent#enqueueWork - ModBusEvents calls it from there.
public class CauldronBehaviour {

    public static void register() {
        EMPTY.put(Items.HONEY_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                level.setBlockAndUpdate(pos, BlockRegistry.HONEY_CAULDRON.get().defaultBlockState()
                        .setValue(HoneyCauldronBlock.HONEY_LEVEL, 1));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });

        EMPTY.put(Items.MAGMA_CREAM, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.setBlockAndUpdate(pos, BlockRegistry.MAGMA_CAULDRON.get().defaultBlockState()
                        .setValue(MagmaCauldronBlock.MAGMA_LEVEL, 1));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });

        EMPTY.put(Items.SLIME_BALL, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.setBlockAndUpdate(pos, BlockRegistry.SLIME_CAULDRON.get().defaultBlockState()
                        .setValue(SlimeCauldronBlock.SLIME_LEVEL, 1));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        });

        // deferred to the same enqueueWork call for consistency, though these maps are private and not shared
        HoneyCauldronBlock.registerInteractions();
        MagmaCauldronBlock.registerInteractions();
        SlimeCauldronBlock.registerInteractions();
        IceCauldronBlock.registerInteractions();
    }
}
