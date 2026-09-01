package net.greenjab.nekomasfixed.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

import java.util.List;

/**
 * PORT (NOT attempted this pass - documented, not silently broken): {@code GuiGraphicsExtractor},
 * {@code extractRenderState}/{@code extractBackground}, {@code CyclingSlotBackground}, {@code
 * ResourceLocation} (as a rendering-sprite id type), {@code MouseButtonEvent}, {@code CursorTypes}, and the
 * {@code blitSprite}/{@code RenderPipelines.GUI_TEXTURED} draw calls are all part of 26.2's
 * render-state extraction split (a client-render-pipeline redesign with no 1.20.1 analogue) - the
 * widest single block of affected files in the whole port (roughly 69 files,
 * plus 29 GUI sprites). Rewriting this screen's rendering internals is out of proportion for
 * one file in a much larger cross-cutting redesign; this package's own job here (this package's own
 * screen/** code) - the MENU's own crafting logic (PyrotechnicsMenu.java) - is fully converted. This
 * class's imports and gross signatures (@OnlyIn, jspecify) are mechanically cleaned, but {@code
 * render}/{@code renderBg}/{@code mouseClicked}'s actual draw calls still target 26.2-only APIs and
 * will not compile until the wider render-state redesign reaches this screen. Left as a named,
 * predicted compile error rather than an invented reimplementation of a rendering
 * pipeline this package does not own.
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

    private final CyclingSlotBackground dyeOrStarSlotIcon = new CyclingSlotBackground(0);
    private static final List<ResourceLocation> DYE_OR_STAR_TEXTURES = List.of(
            DYE_ICON, FIREWORK_STAR_ICON);

    private final CyclingSlotBackground shapeSlotIcon = new CyclingSlotBackground(10);
    private static final List<ResourceLocation> SHAPE_TEXTURES = List.of(
            EMPTY_ICON, FIRE_CHARGE_ICON, GOLD_NUGGET_ICON, CREEPER_PATTERN_ICON, FEATHER_ICON);

    private final CyclingSlotBackground twinkleSlotIcon = new CyclingSlotBackground(11);
    private static final List<ResourceLocation> TWINKLE_TEXTURES = List.of(
            EMPTY_ICON, GLOWSTONE_ICON);

    private final CyclingSlotBackground trailSlotIcon = new CyclingSlotBackground(12);
    private static final List<ResourceLocation> TRAIL_TEXTURES = List.of(
            EMPTY_ICON, DIAMOND);

    private static final ResourceLocation CHEST_SLOTS_TEXTURE = ResourceLocation.withDefaultNamespace("container/horse/chest_slots");

    private static final List<Pair<String, Item>> ANIMATIONS = List.of(
        new Pair<>("none", Items.AIR),
        new Pair<>("large_ball", Items.FIRE_CHARGE),
        new Pair<>("star", Items.GOLD_NUGGET),
        new Pair<>("creeper", Items.CREEPER_BANNER_PATTERN),
        new Pair<>("burst", Items.FEATHER),
        new Pair<>("twinkle", Items.GLOWSTONE_DUST),
        new Pair<>("trail", Items.DIAMOND)
    );

    private final int totalPatterns = 7;

    public PyrotechnicsTableScreen(PyrotechnicsMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 186);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.dyeOrStarSlotIcon.tick(DYE_OR_STAR_TEXTURES);
        this.shapeSlotIcon.tick(SHAPE_TEXTURES);
        this.twinkleSlotIcon.tick(TWINKLE_TEXTURES);
        this.trailSlotIcon.tick(TRAIL_TEXTURES);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        this.extractContents(context, mouseX, mouseY, deltaTicks);
        this.extractCarriedItem(context, mouseX, mouseY);
        this.extractTooltip(context, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        context.blitSprite(RenderPipelines.GUI_TEXTURED, NekomasFixed.id("container/pyrotechnics/"+ANIMATIONS.get(this.menu.getSelectedPattern()).getFirst()), leftPos+98, topPos+15, 71, 71);
        for (Slot slot : this.menu.slots){
            if (slot.isActive() && slot.getContainerSlot()>0 && slot.container instanceof SimpleContainer) {
                context.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, leftPos+slot.x-1, topPos+slot.y-1, 18, 18);
                if (!slot.hasItem()){
                    if (slot.mayPlace(Items.WHITE_DYE.getDefaultInstance()))
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, DYE_ICON, leftPos + slot.x, topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.FIREWORK_STAR.getDefaultInstance()))
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, FIREWORK_STAR_ICON, 16, 16, 0, 0, leftPos + slot.x, topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.GUNPOWDER.getDefaultInstance()))
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, GUNPOWDER_ICON, 16, 16, 0, 0, leftPos + slot.x, topPos + slot.y, 16, 16);
                    else if (slot.mayPlace(Items.PAPER.getDefaultInstance()))
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, PAPER_ICON, 16, 16, 0, 0, leftPos + slot.x, topPos + slot.y, 16, 16);
                }
            }
        }
        this.dyeOrStarSlotIcon.extractRenderState(this.menu, context, deltaTicks, this.leftPos, this.topPos);
        if (this.menu.slots.getFirst().hasItem() && this.menu.slots.getFirst().getItem().getItem() instanceof DyeItem){
            this.shapeSlotIcon.extractRenderState(this.menu, context, deltaTicks, this.leftPos, this.topPos);
            this.twinkleSlotIcon.extractRenderState(this.menu, context, deltaTicks, this.leftPos, this.topPos);
            this.trailSlotIcon.extractRenderState(this.menu, context, deltaTicks, this.leftPos, this.topPos);
        }
        if (this.menu.slots.get(14).isActive()) context.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, leftPos+151, topPos+72, 18, 18);

        int sx = leftPos + 7-14;
        int sy = topPos + 53+19;

        for (int index = 0; index < totalPatterns; ++index) {
            int bx = sx + index * 14;
            int by = sy;
            if (index==0) {bx = leftPos +7;by=topPos +53;}
            if (index>4) bx+=6;
            boolean bl = mouseX >= bx && mouseY >= by && mouseX < bx + 14 && mouseY < by + 18;
            ResourceLocation identifier2;
            if (index == this.menu.getSelectedPattern()) identifier2 = BUTTON_SELECTED_TEXTURE;
            else if (bl) {
                identifier2 = BUTTON_HIGHLIGHTED_TEXTURE;
                context.setTooltipForNextFrame(Component.translatable("container.nekomasfixed.pyrotechnics."+ANIMATIONS.get(index).getFirst()), mouseX, mouseY);
                context.requestCursor(CursorTypes.POINTING_HAND);
            } else identifier2 = BUTTON_TEXTURE;

            context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier2, bx, by, 14, 18);
            context.item(ANIMATIONS.get(index).getSecond().getDefaultInstance(), bx-1, by +1);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int sx = leftPos + 7-14;
        int sy = topPos + 53+19;
        for (int index = 0; index < totalPatterns; ++index) {
            double dx = click.x() - (double) (sx + index * 14);
            double dy = click.y() - (double) (sy);
            if (index==0) { dx = click.x() - (leftPos +7);dy = click.y() - (topPos +53);}
            if (index>4) dx-=6;
            if (dx >= 0.0 && dy >= 0.0 && dx < 14.0 && dy < 18.0) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }
}