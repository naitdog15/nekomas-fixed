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
 * {@code @Mod.EventBusSubscriber} self-registers this class on the mod bus - no manual
 * {@code modBus.register(...)} call, ever, for the same class (this mod's convention).
 * cauldron and dyed-brush dispenser registration run inside {@code FMLCommonSetupEvent#enqueueWork}
 * because both mutate vanilla's non-thread-safe global maps after all {@code RegisterEvent}s have run.
 * no {@code EntityAttributeCreationEvent} handler here on purpose:
 * {@link net.greenjab.nekomasfixed.registry.entity.EntityAttributesAndSpawns} already handles all
 * 8 entity types, and a second handler would double-put the same suppliers.
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
     * on 26.2 this was a Fabric {@code DispenserBlock} registration made at item-registration time;
     * on Forge it must wait for common setup so {@link ItemDyeMap}'s static initializer (which calls
     * {@code RegistryObject#get()}) runs only after {@code RegisterEvent<Item>} has completed.
     */
    private static void registerDyedBrushDispenserBehaviour() {
        for (Map.Entry<AllDyes, Item> entry : ItemDyeMap.BRUSH.entrySet()) {
            DispenserBlock.registerBehavior(entry.getValue(), new DyedBrushBehaviour());
        }
    }
}
