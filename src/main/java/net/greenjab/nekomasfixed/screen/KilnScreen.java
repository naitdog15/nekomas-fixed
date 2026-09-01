package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * PORT: 1.20.1's {@code AbstractFurnaceScreen} constructor is {@code (menu, AbstractFurnaceRecipeBook
 * Component, Inventory, Component title, ResourceLocation texture)} - 5 args, not 26.2's 8 (toggle
 * text / lit-progress texture / burn-progress texture / tabs list are all gone from the screen layer;
 * they live inside {@code RecipeBookComponent} + the recipe-book-category system now - see {@code
 * KilnRecipeBookClient} for where the 3 tabs actually get wired). {@code @OnlyIn} added
 * explicitly - the Fabric client source set gave this structural dist-safety for free;
 * Forge's single source set needs the annotation instead.
 */
@OnlyIn(Dist.CLIENT)
public class KilnScreen extends AbstractFurnaceScreen<KilnMenu> {
    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/gui/container/kiln.png");

    public KilnScreen(KilnMenu menu, Inventory inventory, Component title) {
        super(menu, new KilnRecipeBookClient.KilnRecipeBookComponent(), inventory, title, TEXTURE);
    }
}
