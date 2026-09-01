package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.registry.other.ContainerTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * {@code ItemContainerContents} is replaced by a plain
 * {@code List<ItemStack>}-based {@link ContainerTooltipData}; {@code extractImage(...,
 * GuiGraphicsExtractor)} becomes 1.20.1's real {@code renderImage(Font, x, y, GuiGraphics)}. This DOES
 * restore the container-contents tooltip grid (slot backgrounds + item icons + count/durability
 * decorations); it does not depend on anything else from the deleted 1.21+ tooltip pipeline.
 */
public class ContainerTooltipComponent implements ClientTooltipComponent {
    /** 1.20.1 has no GUI sprite atlas: the bundle's slot background is a corner of this one sheet. */
    private static final ResourceLocation BUNDLE_TEXTURE = new ResourceLocation("textures/gui/container/bundle.png");
    private static final int BUNDLE_TEXTURE_SIZE = 128;
    private static final int SLOT_SPRITE_SIZE = 18;
    private final List<ItemStack> contents;
    private int numberOfSlots;

    public ContainerTooltipComponent(ContainerTooltipData data) {
        this.contents = data.contents();
        this.numberOfSlots = Math.min(27, this.contents.size());
    }

    @Override
    public int getHeight() {
        return this.getRows() * 24;
    }

    @Override
    public int getWidth(Font font) {
        return this.getColumns() * 24;
    }

    private int getRows() {
        if (numberOfSlots == 0) return 0;
        return Math.min((int) Math.ceil(numberOfSlots / (getColumns() + 0.0)), 3);
    }

    private int getColumns() {
        if (numberOfSlots == 0) return 0;
        return Mth.ceil(Math.max(Math.sqrt(numberOfSlots), numberOfSlots / 3.0));
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        List<ItemStack> list = firstStacksInContents();
        this.numberOfSlots = list.size();
        if (list.isEmpty()) return;
        int k = 0;
        for (int l = 0; l < this.getRows(); l++) {
            for (int m = 0; m < this.getColumns(); m++) {
                if (k >= numberOfSlots) break;
                int n = x + m * 24;
                int o = y + l * 24;
                drawItem(n, o, list, k, font, guiGraphics);
                k++;
            }
        }
    }

    private List<ItemStack> firstStacksInContents() {
        int i = Math.min(this.contents.size(), 27);
        return this.contents.subList(0, i);
    }

    private void drawItem(int x, int y, List<ItemStack> stacks, int index, Font font, GuiGraphics guiGraphics) {
        ItemStack itemStack = stacks.get(index);
        guiGraphics.blit(BUNDLE_TEXTURE, x, y, 24, 24, 0, 0, SLOT_SPRITE_SIZE, SLOT_SPRITE_SIZE,
                BUNDLE_TEXTURE_SIZE, BUNDLE_TEXTURE_SIZE);
        guiGraphics.renderItem(itemStack, x + 4, y + 4, index);
        guiGraphics.renderItemDecorations(font, itemStack, x + 4, y + 4);
    }
}
