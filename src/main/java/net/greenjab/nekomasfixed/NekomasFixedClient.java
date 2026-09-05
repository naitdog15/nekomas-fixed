package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.registry.block.cauldron.SoupCauldronBlock;
import net.greenjab.nekomasfixed.registry.block.entity.SoupCauldronBlockEntity;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/**
 * this file's only remaining job is block-color registration - everything else that used to live
 * here now self-registers elsewhere (renderer/model registries under {@code registries/**}, four
 * {@code Material} constants, {@code ClientSyncHandler.init()} folded into the SimpleChannel
 * construction, {@code MenuScreens.register(...)} in {@code ScreenRegistration}).
 * the null-level branch below is the old separate "color(state)" case; {@link BlockColor#getColor}
 * collapses both onto one method.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NekomasFixedClient {

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(NekomasFixedClient::soup, BlockRegistry.SOUP_CAULDRON.get());
    }

    public static int soup(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level == null || pos == null) return -1;
        if (level.getBlockEntity(pos) instanceof SoupCauldronBlockEntity soupCauldronBlockEntity) {
            float f = soupCauldronBlockEntity.getOpenNess(0);
            int s = SoupCauldronBlock.blendFoodColors(soupCauldronBlockEntity.getInputs());
            int w = BiomeColors.getAverageWaterColor(level, pos);
            return ((int) (f * (s >> 16 & 255) + (1 - f) * (w >> 16 & 255)) << 16)
                    | ((int) (f * (s >> 8 & 255) + (1 - f) * (w >> 8 & 255)) << 8)
                    | (int) (f * (s & 255) + (1 - f) * (w & 255)) - (int) Math.pow(2, 24);
        } else return BiomeColors.getAverageWaterColor(level, pos);
    }
}
