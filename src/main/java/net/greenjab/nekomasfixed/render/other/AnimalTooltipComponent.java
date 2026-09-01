package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 1.20.1's real {@code ClientTooltipComponent} (VERIFIED shape, long-stable vanilla interface) is
 * {@code getHeight()} (no {@code Font} param), {@code getWidth(Font)}, and
 * {@code renderImage(Font, int x, int y, GuiGraphics)} — not 26.2's {@code
 * extractImage(Font, x, y, w, h, GuiGraphicsExtractor)}. {@code TypedEntityData}/manual
 * {@code EntityType.loadEntityRecursive} reconstruction is replaced by the ready-made
 * {@code AnimalComponent.StoredEntityData#loadEntity(Level)}. The mouse-follow mob preview itself is
 * 1.20.1 vanilla's own {@code InventoryScreen.renderEntityInInventoryFollowsAngle} (a real,
 * long-standing method — 26.2 appears to have simply renamed it
 * {@code extractEntityInInventoryFollowsMouse}).
 *
 * <p><b>This DOES restore</b> the animal-preview tooltip image (spinning live-entity render). It does
 * <b>not</b> restore anything about hover-text formatting — that is {@code AnimalComponent.tooltipLine()},
 * wired in by whichever {@code appendHoverText} override picks it up (not this class's concern).
 */
public class AnimalTooltipComponent implements ClientTooltipComponent {
    private final AnimalComponent animalComponent;

    public AnimalTooltipComponent(AnimalComponent animalComponent) {
        this.animalComponent = animalComponent;
    }

    @Override
    public int getHeight() {
        return this.animalComponent.animal().isEmpty() ? 0 : 3 * 24;
    }

    @Override
    public int getWidth(Font font) {
        return this.animalComponent.animal().isEmpty() ? 0 : 3 * 24;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        Level level = Minecraft.getInstance().level;
        if (level == null || this.animalComponent.animal().isEmpty()) return;
        Entity entity = this.animalComponent.animal().get(0).loadEntity(level);
        if (!(entity instanceof LivingEntity livingEntity)) return;

        entity.tickCount = Math.toIntExact(level.getGameTime());
        float time = System.currentTimeMillis() % (20 * 1000);
        time *= (float) (2 * Math.PI) / (20 * 1000.0f);
        float dx = 10 * (float) (Math.cos(7 * time) + Math.sin(3 * time));
        float dy = 10 * (float) (Math.cos(5 * time) + Math.sin(2 * time));
        int width = this.getWidth(font);
        int height = this.getHeight();
        InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, x, y - 20, x + width, y + height, 40,
                0.25F, x - 15 + dx, y + 30 + dy, livingEntity);
    }
}
