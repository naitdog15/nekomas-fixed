package net.greenjab.nekomasfixed.compat.vanillabackport;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.CompatMods;
import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Method;

/**
 * reached by name via reflection, never a hard reference to a class that may not be installed - if
 * the register call is missing these four simply render untextured, never a crash.
 * the map is read fresh every frame the ghast is drawn, so entries can arrive after the renderer
 * was built; they just need to land on the main thread, which is what enqueueWork is for.
 * client side only - a dedicated server never draws a ghast or loads this class.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GhastHarnessTextures {

    private static final String HANDLER = "com.blackgear.vanillabackport.client.level.entities.layer.GhastHarnessHandler";
    private static final String TEXTURE_PATH = "textures/entity/ghast/harness/";

    private GhastHarnessTextures() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!CompatMods.vanillaBackportHarnesses()
                || !NekomasFixedConfig.HARNESSES.get()
                || !NekomasFixedClientConfig.HARNESS_RENDERING.get()) {
            return;
        }
        event.enqueueWork(GhastHarnessTextures::register);
    }

    public static void register() {
        try {
            Method handoff = Class.forName(HANDLER).getMethod("register", ItemStack.class, ResourceLocation.class);
            put(handoff, ItemRegistry.AMBER_HARNESS, "amber_harness");
            put(handoff, ItemRegistry.AQUA_HARNESS, "aqua_harness");
            put(handoff, ItemRegistry.INDIGO_HARNESS, "indigo_harness");
            put(handoff, ItemRegistry.MAROON_HARNESS, "maroon_harness");
        } catch (Throwable failure) {
            // older builds of the supplying mod have no handler for this - harnesses stay wearable
            // but render bare, and this warning is the only hint why.
            NekomasFixed.LOGGER.warn("No {} in the installed Vanilla Backport, so the four harnesses "
                    + "will render untextured. A newer build of it supplies one.", HANDLER);
        }
    }

    private static void put(Method handoff, RegistryObject<Item> harness, String name)
            throws ReflectiveOperationException {
        if (!harness.isPresent()) {
            return;
        }
        handoff.invoke(null, new ItemStack(harness.get()), NekomasFixed.id(TEXTURE_PATH + name + ".png"));
    }
}
