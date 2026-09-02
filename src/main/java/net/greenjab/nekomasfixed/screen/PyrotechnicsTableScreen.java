package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.render.other.LegacySpriteBlit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * The pyrotechnics table's screen: the pattern buttons down the left, the animated preview of the
 * shape they select, and the hint icons that sit in the empty ingredient slots.
 *
 * <p>Everything the table draws of its own is a loose PNG under {@code textures/gui/sprites/}, put
 * on screen through {@link LegacySpriteBlit}. The pattern previews are tall vertical strips of
 * square frames; the frame to show is picked by hand from the tick counter and the strip's own
 * length, recorded per pattern below. The empty-slot hints cycle through their alternatives on the
 * same timer, and the chest-slot outline behind the ingredient row is lifted out of
 * {@code textures/gui/container/horse.png}.
 */
@OnlyIn(Dist.CLIENT)
public class PyrotechnicsTableScreen extends AbstractContainerScreen<PyrotechnicsMenu> {

    private static final ResourceLocation TEXTURE = NekomasFixed.id("textures/gui/container/pyrotechnics.png");

    private static final ResourceLocation BUTTON_SELECTED_TEXTURE = NekomasFixed.id("container/pyrotechnics/button_selected");
    private static final ResourceLocation BUTTON_HIGHLIGHTED_TEXTURE = NekomasFixed.id("container/pyrotechnics/button_highlighted");
    private static final ResourceLocation BUTTON_TEXTURE = NekomasFixed.id("container/pyrotechnics/button");

    private static final ResourceLocation DYE_ICON = NekomasFixed.id("container/pyrotechnics/dye");
    private static final ResourceLocation PAPER_ICON = NekomasFixed.id("container/pyrotechnics/paper");
    private static final ResourceLocation GUNPOWDER_ICON = NekomasFixed.id("container/pyrotechnics/gunpowder");
    private static final ResourceLocation FIREWORK_STAR_ICON = NekomasFixed.id("container/pyrotechnics/firework_star");

    private static final ResourceLocation EMPTY_ICON = NekomasFixed.id("container/pyrotechnics/empty");
    private static final ResourceLocation FIRE_CHARGE_ICON = NekomasFixed.id("container/pyrotechnics/fire_charge");
    private static final ResourceLocation GOLD_NUGGET_ICON = NekomasFixed.id("container/pyrotechnics/gold_nugget");
    private static final ResourceLocation CREEPER_PATTERN_ICON = NekomasFixed.id("container/pyrotechnics/creeper_pattern");
    private static final ResourceLocation FEATHER_ICON = NekomasFixed.id("container/pyrotechnics/feather");
    private static final ResourceLocation GLOWSTONE_ICON = NekomasFixed.id("container/pyrotechnics/glowstone");
    private static final ResourceLocation DIAMOND = NekomasFixed.id("container/pyrotechnics/diamond");

    private static final List<ResourceLocation> DYE_OR_STAR_TEXTURES = List.of(
            DYE_ICON, FIREWORK_STAR_ICON);
    private static final List<ResourceLocation> SHAPE_TEXTURES = List.of(
            EMPTY_ICON, FIRE_CHARGE_ICON, GOLD_NUGGET_ICON, CREEPER_PATTERN_ICON, FEATHER_ICON);
    private static final List<ResourceLocation> TWINKLE_TEXTURES = List.of(
            EMPTY_ICON, GLOWSTONE_ICON);
    private static final List<ResourceLocation> TRAIL_TEXTURES = List.of(
            EMPTY_ICON, DIAMOND);

    /** Ticks a slot icon stays up before the next one in its list, same rate vanilla cycles at. */
    private static final int ICON_CHANGE_TICK_RATE = 30;

    private static final ResourceLocation HORSE_INVENTORY_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
    /** Where the 90x54 block of chest slots sits in horse.png (directly under the 176x166 window). */
    private static final int HORSE_CHEST_SLOTS_V = 166;

    /** Pattern preview strips: {@link #FRAME_SIZE}-square frames stacked vertically, one per tick. */
    private static final int FRAME_SIZE = 200;
    private static final int PREVIEW_SIZE = 71;

    /** A firework-shape button: its preview strip's name and length, and the item drawn on the button. */
    private record Pattern(String name, int frames, Item icon) {
        ResourceLocation preview() {
            return NekomasFixed.id("container/pyrotechnics/" + this.name);
        }
    }

    private static final List<Pattern> ANIMATIONS = List.of(
        new Pattern("none", 119, Items.AIR),
        new Pattern("large_ball", 119, Items.FIRE_CHARGE),
        new Pattern("star", 119, Items.GOLD_NUGGET),
        new Pattern("creeper", 119, Items.CREEPER_BANNER_PATTERN),
        new Pattern("burst", 118, Items.FEATHER),
        new Pattern("twinkle", 119, Items.GLOWSTONE_DUST),
        new Pattern("trail", 118, Items.DIAMOND)
    );

    private final int totalPatterns = ANIMATIONS.size();
    private int ticks;

    public PyrotechnicsTableScreen(PyrotechnicsMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.ticks++;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, partialTick);
        int hovered = patternAt(mouseX, mouseY);
        if (hovered >= 0 && hovered != this.menu.getSelectedPattern()) {
            context.renderTooltip(this.font, Component.translatable(
                    "container.nekomasfixed.pyrotechnics." + ANIMATIONS.get(hovered).name()), mouseX, mouseY);
        } else {
            this.renderTooltip(context, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float partialTick, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        Pattern selected = ANIMATIONS.get(Math.floorMod(this.menu.getSelectedPattern(), totalPatterns));
        LegacySpriteBlit.blitSpriteScaled(context, selected.preview(),
                this.leftPos + 98, this.topPos + 15, PREVIEW_SIZE, PREVIEW_SIZE,
                0, (this.ticks % selected.frames()) * FRAME_SIZE,
                FRAME_SIZE, FRAME_SIZE, FRAME_SIZE, selected.frames() * FRAME_SIZE);

        for (Slot slot : this.menu.slots) {
            if (slot.isActive() && slot.getContainerSlot() > 0 && slot.container instanceof SimpleContainer) {
                blitChestSlot(context, this.leftPos + slot.x - 1, this.topPos + slot.y - 1);
                if (!slot.hasItem()) {
                    if (slot.mayPlace(Items.WHITE_DYE.getDefaultInstance()))
                        LegacySpriteBlit.blitSprite(context, DYE_ICON, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.FIREWORK_STAR.getDefaultInstance()))
                        LegacySpriteBlit.blitSprite(context, FIREWORK_STAR_ICON, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.GUNPOWDER.getDefaultInstance()))
                        LegacySpriteBlit.blitSprite(context, GUNPOWDER_ICON, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.PAPER.getDefaultInstance()))
                        LegacySpriteBlit.blitSprite(context, PAPER_ICON, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
                }
            }
        }

        renderCyclingIcon(context, 0, DYE_OR_STAR_TEXTURES);
        Slot firstSlot = this.menu.slots.get(0);
        if (firstSlot.hasItem() && firstSlot.getItem().getItem() instanceof DyeItem) {
            renderCyclingIcon(context, 10, SHAPE_TEXTURES);
            renderCyclingIcon(context, 11, TWINKLE_TEXTURES);
            renderCyclingIcon(context, 12, TRAIL_TEXTURES);
        }
        if (this.menu.slots.get(14).isActive()) blitChestSlot(context, this.leftPos + 151, this.topPos + 72);

        for (int index = 0; index < totalPatterns; ++index) {
            int bx = buttonX(index);
            int by = buttonY(index);
            ResourceLocation button;
            if (index == this.menu.getSelectedPattern()) button = BUTTON_SELECTED_TEXTURE;
            else if (mouseX >= bx && mouseY >= by && mouseX < bx + 14 && mouseY < by + 18) button = BUTTON_HIGHLIGHTED_TEXTURE;
            else button = BUTTON_TEXTURE;

            LegacySpriteBlit.blitSprite(context, button, bx, by, 14, 18);
            context.renderItem(ANIMATIONS.get(index).icon().getDefaultInstance(), bx - 1, by + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = patternAt(mouseX, mouseY);
        if (index >= 0) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void blitChestSlot(GuiGraphics context, int x, int y) {
        context.blit(HORSE_INVENTORY_TEXTURE, x, y, 0, HORSE_CHEST_SLOTS_V, 18, 18);
    }

    private void renderCyclingIcon(GuiGraphics context, int slotIndex, List<ResourceLocation> icons) {
        Slot slot = this.menu.getSlot(slotIndex);
        if (slot.hasItem()) return;
        ResourceLocation icon = icons.get((this.ticks / ICON_CHANGE_TICK_RATE) % icons.size());
        LegacySpriteBlit.blitSprite(context, icon, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
    }

    /** The first button sits on its own above the row; the last two are pushed right by a divider. */
    private int buttonX(int index) {
        if (index == 0) return this.leftPos + 7;
        return this.leftPos + 7 - 14 + index * 14 + (index > 4 ? 6 : 0);
    }

    private int buttonY(int index) {
        return index == 0 ? this.topPos + 53 : this.topPos + 53 + 19;
    }

    /** Index of the pattern button under the given point, or -1. */
    private int patternAt(double mouseX, double mouseY) {
        for (int index = 0; index < totalPatterns; ++index) {
            double dx = mouseX - buttonX(index);
            double dy = mouseY - buttonY(index);
            if (dx >= 0.0 && dy >= 0.0 && dx < 14.0 && dy < 18.0) return index;
        }
        return -1;
    }
}
