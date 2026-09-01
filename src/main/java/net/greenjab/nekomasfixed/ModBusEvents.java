package net.greenjab.nekomasfixed;

import net.greenjab.nekomasfixed.registry.block.cauldron.CauldronBehaviour;
import net.greenjab.nekomasfixed.registry.other.DyedBrushBehaviour;
import net.greenjab.nekomasfixed.util.AllDyes;
import net.greenjab.nekomasfixed.util.ItemDyeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;

/**
 * The common (both-dist) mod-bus event holder. {@code @Mod.EventBusSubscriber}
 * self-registers this class onto the mod bus when the mod is constructed — no manual
 * {@code modBus.register(...)} call is needed or wanted (this mod's own convention: pick the
 * annotation idiom for handlers and never mix it with an explicit register call for the same class;
 * no class in this mod does both).
 * <p>
 * This class carries two cross-package dependencies: the cauldron interaction-map population and
 * the dyed-brush dispenser behaviours, both inside {@code FMLCommonSetupEvent#enqueueWork} because
 * both mutate vanilla's non-thread-safe global maps ({@code CauldronInteraction}'s and
 * {@code DispenserBlock.DISPENSER_REGISTRY}) after all {@code RegisterEvent}s have run.
 * <p>
 * No {@code EntityAttributeCreationEvent} handler here, deliberately:
 * {@link net.greenjab.nekomasfixed.registry.entity.EntityAttributesAndSpawns} is the mod's single
 * handler for all 8 entity types, and a second one would double-{@code put} the same attribute
 * suppliers.
 */
@Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBusEvents {
    private ModBusEvents() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CauldronBehaviour.register();
            registerDyedBrushDispenserBehaviour();
        });
    }

    /**
     * On 26.2 this was a Fabric {@code DispenserBlock} registration made
     * at item-registration time; on Forge it must wait for common setup so
     * {@link ItemDyeMap}'s static initializer (which itself calls {@code RegistryObject#get()}) runs
     * only after {@code RegisterEvent<Item>} has completed.
     */
    private static void registerDyedBrushDispenserBehaviour() {
        for (Map.Entry<AllDyes, Item> entry : ItemDyeMap.BRUSH.entrySet()) {
            DispenserBlock.registerBehavior(entry.getValue(), new DyedBrushBehaviour());
        }
    }
}
