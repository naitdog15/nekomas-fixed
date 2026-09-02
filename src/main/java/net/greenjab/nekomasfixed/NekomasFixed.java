package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.config.NekomasFixedClientConfig;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.network.SyncHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The @Mod entry point. The constructor does exactly five things — get the mod
 * bus, run the {@link Registries} aggregator, register the two common event-bus holders, declare
 * the two config specs, and initialise the network channel before {@code NetworkRegistry.lock()}. Every other package's
 * own registration/event logic lives in its own holder classes; this class fans out to them and
 * nothing else.
 */
@Mod(NekomasFixed.NAMESPACE)
public class NekomasFixed {
    public static final String MOD_NAME = "Nekoma' Fixed Minecraft";
    public static final String NAMESPACE = "nekomasfixed";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    // Exactly five statements. ModBusEvents / ForgeBusEvents / ModBusClientEvents
    // are @Mod.EventBusSubscriber classes (see those files) and self-register via FML's annotation
    // scan when the mod is constructed, which is why that alone satisfies "register the
    // common Forge-bus holder" without a fourth, redundant manual .register() call here (this mod's
    // own convention bans mixing the manual-register idiom with the annotation idiom on one class).
    public NekomasFixed() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registries.registerAll(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NekomasFixedConfig.SPEC, "nekomasfixed-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, NekomasFixedClientConfig.SPEC, "nekomasfixed-client.toml");
        SyncHandler.init();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    /**
     * 1.20.1 has no data-component ItemEnchantments; enchantments live in the vanilla
     * Map&lt;Enchantment, Integer&gt; returned by EnchantmentHelper.
     */
    public static int enchantLevel(ItemStack stack, String name) {
        int level = 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            ResourceLocation key = ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey());
            if (key != null && key.toString().toLowerCase().contains(name.toLowerCase())) {
                level += entry.getValue();
            }
        }
        return level;
    }
}
