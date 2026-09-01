package net.greenjab.nekomasfixed.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.render.block.entity.model.ClamBlockModel;
import net.greenjab.nekomasfixed.render.block.entity.model.ClockBlockModel;
import net.greenjab.nekomasfixed.render.block.entity.model.EndermanEyesBlockModel;
import net.greenjab.nekomasfixed.render.block.entity.model.EndermanHeadBlockModel;
import net.greenjab.nekomasfixed.render.entity.model.*;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * {@code ModModelLayerRegistry…()} maps to {@code RegisterLayerDefinitions} on Forge. This
 * class keeps its role as the {@link ModelLayerLocation} constant holder (~40 consumer sites across
 * {@code render/**}) and additionally self-subscribes to register them, instead of being called from
 * a shared {@code NekomasFixedClient.onInitializeClient()} that no longer exists on Forge.
 *
 * <p><b>{@code ArmorModelSet} does not exist on 1.20.1</b> (VERIFIED against
 * {@code forge-1.20.1-mapped-src} — no such class in that tree). 1.20.1's
 * {@code HumanoidArmorLayer} takes an "inner" (0.5 dilation) and "outer" (1.0 dilation) model shared
 * across every armor slot, plus a {@code ModelManager} (for the armor-trim atlas) — not four
 * separately-baked per-slot models. Vanilla already bakes and shares one such pair per mob family
 * ({@code ModelLayers.ZOMBIE_INNER_ARMOR}/{@code OUTER_ARMOR}, {@code SKELETON_…}, {@code PLAYER_…}),
 * so Derelict/Rime/Drenched/TargetDummy's armor layers reuse those directly instead of baking
 * mod-owned duplicates — simpler and correct, since armor shape is generic per mob family regardless
 * of the wearer's own body model.
 *
 * <p><b>No dedicated baby models</b>: 1.20.1 has no {@code BabyZombieModel}/{@code createBabyArmorMeshSet}
 * (both 26.2-only). Vanilla itself gives baby zombies the same body/armor model as adults and relies
 * on {@code HumanoidModel}'s own built-in young-scale-down — so Rime/Derelict do too (only their
 * *texture* switches for the baby variant). See {@code render/entity/model/DerelictModel}'s javadoc.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModModelLayerRegistry {

    public static final ModelLayerLocation CLAM = register("clam");
    public static final ModelLayerLocation CLOCK = register("clock");
    public static final ModelLayerLocation ENDERMAN_HEAD = register("enderman_head");
    public static final ModelLayerLocation ENDERMAN_EYES = register("enderman_head", "eyes");

    public static final ModelLayerLocation BIG_ACACIA_BOAT = register("big_boat/acacia");
    public static final ModelLayerLocation BIG_BAMBOO_BOAT = register("big_boat/bamboo");
    public static final ModelLayerLocation BIG_BIRCH_BOAT = register("big_boat/birch");
    public static final ModelLayerLocation BIG_CHERRY_BOAT = register("big_boat/cherry");
    public static final ModelLayerLocation BIG_DARK_OAK_BOAT = register("big_boat/dark_oak");
    public static final ModelLayerLocation BIG_JUNGLE_BOAT = register("big_boat/jungle");
    public static final ModelLayerLocation BIG_MANGROVE_BOAT = register("big_boat/mangrove");
    public static final ModelLayerLocation BIG_OAK_BOAT = register("big_boat/oak");
    public static final ModelLayerLocation BIG_PALE_OAK_BOAT = register("big_boat/pale_oak");
    public static final ModelLayerLocation BIG_SPRUCE_BOAT = register("big_boat/spruce");
    public static final ModelLayerLocation BIG_BAOBAB_BOAT = register("big_boat/baobab");

    public static final ModelLayerLocation HUGE_ACACIA_BOAT = register("huge_boat/acacia");
    public static final ModelLayerLocation HUGE_BAMBOO_BOAT = register("huge_boat/bamboo");
    public static final ModelLayerLocation HUGE_BIRCH_BOAT = register("huge_boat/birch");
    public static final ModelLayerLocation HUGE_CHERRY_BOAT = register("huge_boat/cherry");
    public static final ModelLayerLocation HUGE_DARK_OAK_BOAT = register("huge_boat/dark_oak");
    public static final ModelLayerLocation HUGE_JUNGLE_BOAT = register("huge_boat/jungle");
    public static final ModelLayerLocation HUGE_MANGROVE_BOAT = register("huge_boat/mangrove");
    public static final ModelLayerLocation HUGE_OAK_BOAT = register("huge_boat/oak");
    public static final ModelLayerLocation HUGE_PALE_OAK_BOAT = register("huge_boat/pale_oak");
    public static final ModelLayerLocation HUGE_SPRUCE_BOAT = register("huge_boat/spruce");
    public static final ModelLayerLocation HUGE_BAOBAB_BOAT = register("huge_boat/baobab");

    public static final ModelLayerLocation TARGET_DUMMY = register("target_dummy");
    public static final ModelLayerLocation TARGET_DUMMY_BASE = register("target_dummy_base");

    public static final ModelLayerLocation WILD_FIRE = register("wild_fire");
    public static final ModelLayerLocation TERMITE = register("termite");
    public static final ModelLayerLocation MOOBLOOM = register("moobloom");
    public static final ModelLayerLocation MOOBLOOM_BABY = register("moobloom", "baby");
    public static final ModelLayerLocation WILDFIRE_TRIDENT = register("wildfirefire_trident");

    public static final ModelLayerLocation DRENCHED = register("drenched");
    public static final ModelLayerLocation SUSPICIOUS_SPIDER = register("suspicious_spider");

    public static final ModelLayerLocation RIME = register("rime");
    public static final ModelLayerLocation RIME_OUTER_LAYER = register("rime", "outer");

    public static final ModelLayerLocation DERELICT = register("derelict");
    public static final ModelLayerLocation DERELICT_OUTER_LAYER = register("derelict", "outer");

    public static final ModelLayerLocation BAOBAB_BOAT = register("boat/baobab");
    public static final ModelLayerLocation BAOBAB_CHEST_BOAT = register("chest_boat/baobab");

    private static ModelLayerLocation register(final String model) {
        return register(model, "main");
    }
    private static ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(NekomasFixed.id(path), layer);
    }

    @SubscribeEvent
    public static void registerEntityModelLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BAOBAB_BOAT, BoatModel::createBodyModel);
        event.registerLayerDefinition(BAOBAB_CHEST_BOAT, ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(TERMITE, TermiteModel::getTexturedModelData);
        event.registerLayerDefinition(MOOBLOOM, MoobloomModel::getTexturedModelData);
        event.registerLayerDefinition(MOOBLOOM_BABY, BabyMoobloomModel::getTexturedModelData);
        event.registerLayerDefinition(SUSPICIOUS_SPIDER, SuspiciousSpiderModel::getTexturedModelData);
        event.registerLayerDefinition(DRENCHED, DrenchedModel::getTexturedModelData);

        event.registerLayerDefinition(RIME, RimeModel::createBodyLayer);
        event.registerLayerDefinition(RIME_OUTER_LAYER, RimeModel::createOuterLayer);

        event.registerLayerDefinition(DERELICT, DerelictModel::createBodyLayer);
        event.registerLayerDefinition(DERELICT_OUTER_LAYER, DerelictModel::createOuterLayer);

        event.registerLayerDefinition(CLAM, ClamBlockModel::getTexturedModelData);
        event.registerLayerDefinition(CLOCK, ClockBlockModel::getTexturedModelData);
        event.registerLayerDefinition(ENDERMAN_HEAD, EndermanHeadBlockModel::getTexturedModelData);
        event.registerLayerDefinition(ENDERMAN_EYES, EndermanEyesBlockModel::getTexturedModelData);

        event.registerLayerDefinition(BIG_ACACIA_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_BAMBOO_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_BIRCH_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_CHERRY_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_DARK_OAK_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_JUNGLE_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_MANGROVE_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_OAK_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_PALE_OAK_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_SPRUCE_BOAT, BigBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(BIG_BAOBAB_BOAT, BigBoatModel::getChestTexturedModelData);

        event.registerLayerDefinition(HUGE_ACACIA_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_BAMBOO_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_BIRCH_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_CHERRY_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_DARK_OAK_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_JUNGLE_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_MANGROVE_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_OAK_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_PALE_OAK_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_SPRUCE_BOAT, HugeBoatModel::getChestTexturedModelData);
        event.registerLayerDefinition(HUGE_BAOBAB_BOAT, HugeBoatModel::getChestTexturedModelData);

        event.registerLayerDefinition(TARGET_DUMMY, TargetDummyModel::getTexturedModelData);
        event.registerLayerDefinition(TARGET_DUMMY_BASE, BasePlateModel::getTexturedModelData);

        event.registerLayerDefinition(WILD_FIRE, WildfireModel::getTexturedModelData);
        event.registerLayerDefinition(WILDFIRE_TRIDENT, TridentModel::createLayer);
    }
}
