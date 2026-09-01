package net.greenjab.nekomasfixed.registry.registries;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.screen.KilnMenu;
import net.greenjab.nekomasfixed.screen.PyrotechnicsMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ScreenHandlerRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NekomasFixed.NAMESPACE);

    public static final RegistryObject<MenuType<KilnMenu>> KILN =
            MENU_TYPES.register("kiln", () -> new MenuType<>(KilnMenu::new, FeatureFlags.VANILLA_SET));

    public static final RegistryObject<MenuType<PyrotechnicsMenu>> PYROTECHNICS =
            MENU_TYPES.register("pyrotechnics", () -> new MenuType<>(PyrotechnicsMenu::new, FeatureFlags.VANILLA_SET));
}
