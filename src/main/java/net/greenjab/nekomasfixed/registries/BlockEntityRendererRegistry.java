package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.render.block.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockEntityRendererRegistry {

    @SubscribeEvent
    public static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.CLAM_BLOCK_ENTITY.get(), ClamBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.CLOCK_BLOCK_ENTITY.get(), ClockBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.ENDERMAN_HEAD_BLOCK_ENTITY.get(), EndermanHeadBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.HOLLOW_LOG_BLOCK_ENTITY.get(), HollowLogBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.SOUP_CAULDRON_BLOCK_ENTITY.get(), SoupCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.STACKED_CAKE_BLOCK_ENTITY.get(), StackedCakeBlockEntityRenderer::new);
    }
}
