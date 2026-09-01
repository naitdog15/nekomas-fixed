package net.greenjab.nekomasfixed.registry.block.cauldron;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

import static net.minecraft.core.cauldron.CauldronInteraction.EMPTY;

/**
 * {@code EMPTY} here is vanilla's own shared {@code CauldronInteraction.EMPTY}
 * map (an empty cauldron becoming a honey/magma/slime cauldron when the right item is used on it) -
 * a genuinely shared mutable vanilla structure, so this whole method (plus the 4 cauldron classes'
 * own {@code registerInteractions()}, called here too) must run inside
 * {@code FMLCommonSetupEvent#enqueueWork} — which is where {@code ModBusEvents} calls it from.
 */
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

        // The 4 custom liquids' OWN dedicated interaction maps - also deferred to this same
        // enqueueWork call for consistency, even though (unlike EMPTY above) each is a private,
        // mod-owned map with no cross-mod sharing concern.
        HoneyCauldronBlock.registerInteractions();
        MagmaCauldronBlock.registerInteractions();
        SlimeCauldronBlock.registerInteractions();
        IceCauldronBlock.registerInteractions();

        // The soup cauldron's ingredient palette needs this mod's own items resolved, which is
        // only true once every registry has been filled.
        SoupCauldronBlock.registerFoodColors();
    }
}
