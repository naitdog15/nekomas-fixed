package net.greenjab.nekomasfixed.render.other;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// this version's ClientTooltipComponent is getHeight()/getWidth(Font)/renderImage(Font,x,y,GuiGraphics),
// not 26.2's extractImage(...); renderEntityInInventoryFollowsMouse here still takes an origin+scale,
// not 26.2's renamed extractEntityInInventoryFollowsMouse rectangle - only reason the call below differs
// image only - the "Holding: ..." text is AnimalComponent#tooltipLine() on appendHoverText
public class AnimalTooltipComponent implements ClientTooltipComponent {
    private static LivingEntity cachedEntity;
    private static Level cachedLevel;
    private static CompoundTag cachedTag;
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
        CompoundTag tag = this.animalComponent.animal().get(0).entityData();
        if (cachedLevel != level || !tag.equals(cachedTag)) {
            Entity entity = this.animalComponent.animal().get(0).loadEntity(level);
            cachedEntity = entity instanceof LivingEntity livingEntity ? livingEntity : null;
            cachedLevel = level;
            cachedTag = tag.copy();
        }
        LivingEntity livingEntity = cachedEntity;
        if (livingEntity == null) return;

        livingEntity.tickCount = Math.toIntExact(level.getGameTime());
        float time = System.currentTimeMillis() % (20 * 1000);
        time *= (float) (2 * Math.PI) / (20 * 1000.0f);
        float dx = 10 * (float) (Math.cos(7 * time) + Math.sin(3 * time));
        float dy = 10 * (float) (Math.cos(5 * time) + Math.sin(2 * time));
        int width = this.getWidth(font);
        int height = this.getHeight();
        int centerX = x + width / 2;
        int bottomY = y + height;
        // 1.20.1's helper wants the offset from the render origin to the "mouse", not the mouse
        // position itself; feeding it the same wandering point keeps the slow turn the preview had.
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, centerX, bottomY, 40,
                centerX - (x - 15.0F + dx), (bottomY - 50.0F) - (y + 30.0F + dy), livingEntity);
    }

    @Mod.EventBusSubscriber(modid = NekomasFixed.NAMESPACE, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            cachedEntity = null;
            cachedLevel = null;
            cachedTag = null;
        }
    }
}
