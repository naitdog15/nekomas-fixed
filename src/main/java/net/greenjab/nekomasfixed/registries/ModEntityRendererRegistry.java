package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.render.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/**
 * {@code ModEntityRendererRegistry…()} maps to Forge's {@code RegisterRenderers#registerEntityRenderer}.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRendererRegistry {

    @SubscribeEvent
    public static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityTypeRegistry.FAKE_BOAT.get(), FakeBoatRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.BIG_ACACIA_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_ACACIA_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_BAMBOO_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_BAMBOO_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_BIRCH_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_BIRCH_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_CHERRY_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_CHERRY_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_DARK_OAK_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_DARK_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_JUNGLE_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_JUNGLE_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_MANGROVE_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_MANGROVE_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_OAK_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_PALE_OAK_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_PALE_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.BIG_SPRUCE_BOAT.get(), context -> new BigBoatRenderer<>(context, ModModelLayerRegistry.BIG_SPRUCE_BOAT));

        event.registerEntityRenderer(EntityTypeRegistry.HUGE_ACACIA_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_ACACIA_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_BAMBOO_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_BAMBOO_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_BIRCH_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_BIRCH_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_CHERRY_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_CHERRY_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_DARK_OAK_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_DARK_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_JUNGLE_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_JUNGLE_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_MANGROVE_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_MANGROVE_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_OAK_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_PALE_OAK_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_PALE_OAK_BOAT));
        event.registerEntityRenderer(EntityTypeRegistry.HUGE_SPRUCE_BOAT.get(), context -> new HugeBoatRenderer(context, ModModelLayerRegistry.HUGE_SPRUCE_BOAT));

        event.registerEntityRenderer(EntityTypeRegistry.TARGET_DUMMY.get(), TargetDummyEntityRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.SPEAR.get(), SpearRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.WILDFIRE_TRIDENT.get(), ThrownWildfireTridentRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.FIRE_BOMB.get(), FireBombRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.SLINGSHOT_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.SLOWNESS_SNOWBALL.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(EntityTypeRegistry.WILDFIRE.get(), WildfireRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.TERMITE.get(), TermiteRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.MOOBLOOM.get(), MoobloomRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.SUSPICIOUS_SPIDER.get(), SuspiciousSpiderEntityRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.DRENCHED.get(), DrenchedRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.DERELICT.get(), DerelictRenderer::new);
        event.registerEntityRenderer(EntityTypeRegistry.RIME.get(), RimeRenderer::new);
    }
}
