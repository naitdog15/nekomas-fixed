package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The kiln's screen. The furnace screen already draws everything a kiln needs, so all this supplies
 * is the background and the recipe book; the tabs inside that book are set up in
 * {@link KilnRecipeBookClient}.
 */
@OnlyIn(Dist.CLIENT)
public class KilnScreen extends AbstractFurnaceScreen<KilnMenu> {
    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/gui/container/kiln.png");

    public KilnScreen(KilnMenu menu, Inventory inventory, Component title) {
        super(menu, new KilnRecipeBookClient.KilnRecipeBookComponent(), inventory, title, TEXTURE);
    }
}
