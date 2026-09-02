package net.greenjab.nekomasfixed.screen;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.greenjab.nekomasfixed.util.ModColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

import java.util.List;
import java.util.Map;

public class PyrotechnicsMenu extends AbstractContainerMenu {

    private final Container input = new SimpleContainer(14){
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final Container output = new SimpleContainer(1);
    private final DataSlot selectedPattern = DataSlot.standalone();

    public PyrotechnicsMenu(int syncId, Inventory playerInventory) {
        super(ScreenHandlerRegistry.PYROTECHNICS.get(), syncId);
        this.addDataSlot(selectedPattern);

        // dyes / stars
        this.addSlot(new Slot(this.input, 0, 8, 16)
        { @Override public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.FIREWORK_STAR)||stack.getItem() instanceof DyeItem;}});
        this.addSlot(new Slot(this.input, 1, 8+18, 16)
        {   @Override public boolean isActive() {
                return slots.get(0).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                    : slots.get(0).getItem().is(Items.FIREWORK_STAR) &&stack.is(Items.FIREWORK_STAR);}});
        this.addSlot(new Slot(this.input, 2, 8+18*2, 16)
        {   @Override public boolean isActive() {
                return slots.get(1).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                        : slots.get(0).getItem().is(Items.FIREWORK_STAR) &&stack.is(Items.FIREWORK_STAR);}});
        this.addSlot(new Slot(this.input, 3, 8+18*3, 16)
        {   @Override public boolean isActive() {
                return slots.get(2).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                        : slots.get(0).getItem().is(Items.FIREWORK_STAR) &&stack.is(Items.FIREWORK_STAR);}});
        this.addSlot(new Slot(this.input, 4, 8+18*4, 16)
        {   @Override public boolean isActive() {
                return slots.get(3).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                        : slots.get(0).getItem().is(Items.FIREWORK_STAR) &&stack.is(Items.FIREWORK_STAR);}});
        // fade / gunpowder
        this.addSlot(new Slot(this.input, 5, 8, 35)
        {   @Override public boolean isActive() {
                return slots.get(0).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                        : slots.get(0).getItem().is(Items.FIREWORK_STAR) && stack.is(Items.GUNPOWDER);}});
        this.addSlot(new Slot(this.input, 6, 8+18, 35)
        {   @Override public boolean isActive() {
                return slots.get(5).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                        : slots.get(0).getItem().is(Items.FIREWORK_STAR) && stack.is(Items.GUNPOWDER);}});
        this.addSlot(new Slot(this.input, 7, 8+18*2, 35)
        {   @Override public boolean isActive() {
                return slots.get(6).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.getItem() instanceof DyeItem
                    : slots.get(0).getItem().is(Items.FIREWORK_STAR) && stack.is(Items.GUNPOWDER);}});
        this.addSlot(new Slot(this.input, 8, 8+18*3, 35)
        {   @Override public boolean isActive() {
                return (slots.get(7).hasItem() && slots.get(0).getItem().getItem() instanceof DyeItem) || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem && stack.getItem() instanceof DyeItem;}});
        this.addSlot(new Slot(this.input, 9, 8+18*4, 35)
        {   @Override public boolean isActive() {
                return slots.get(8).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem && stack.getItem() instanceof DyeItem;}});
        // shape
        this.addSlot(new Slot(this.input, 10, 33, 54)
        {   @Override public boolean isActive() {
                return slots.get(0).getItem().getItem() instanceof DyeItem || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return isActive() && (stack.is(Items.FIRE_CHARGE)|| stack.is(Items.GOLD_NUGGET)|| stack.is(Items.CREEPER_BANNER_PATTERN)|| stack.is(Items.FEATHER));}});
        // glowstone
        this.addSlot(new Slot(this.input, 11, 62, 54)
        {   @Override public boolean isActive() {
                return slots.get(0).getItem().getItem() instanceof DyeItem || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return isActive() && (stack.is(Items.GLOWSTONE_DUST));}});
        // diamond
        this.addSlot(new Slot(this.input, 12, 80, 54)
        {   @Override public boolean isActive() {
                return slots.get(0).getItem().getItem() instanceof DyeItem || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return isActive() && (stack.is(Items.DIAMOND));}});
        // gunpowder / paper
        this.addSlot(new Slot(this.input, 13, 99, 73)
        {    @Override public boolean isActive() {
                return slots.get(0).hasItem() || this.hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return slots.get(0).getItem().getItem() instanceof DyeItem ? stack.is(Items.GUNPOWDER)
                    : slots.get(0).getItem().is(Items.FIREWORK_STAR) && stack.is(Items.PAPER);}});

        this.addSlot(new Slot(this.output, 0, 152, 73) {
            @Override public boolean isActive() {
                return slots.get(0).hasItem();}
            @Override public boolean mayPlace(ItemStack stack) {
                return false;}
            @Override public void onTake(final Player player, final ItemStack carried) {
                slots.stream().filter(slot -> slot.container==input).forEach(slot -> slot.remove(slot.getItem().is(Items.CREEPER_BANNER_PATTERN)?0:1));}
        });

        for (int m = 0; m < 3; ++m)
            for (int l = 0; l < 9; ++l) this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 104 + m * 18));
        for (int m = 0; m < 9; ++m) this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 162));
    }

    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (inventory == this.input) {
            this.updateResult();
            this.broadcastChanges();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < 15) {
                if (!this.moveItemStackTo(stack, 15, 51, true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 14, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            slot.onTake(player, stack);
        }
        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < 8) {
            this.selectedPattern.set(id);
            return true;
        }
        return false;
    }

    // Firework data is plain NBT, exactly as vanilla writes it: an "Explosion" compound holding byte
    // "Type"/"Trail"/"Flicker" and int-array "Colors"/"FadeColors", and a "Fireworks" compound
    // holding byte "Flight" plus an "Explosions" list of those same per-star compounds. The shape
    // bytes below are vanilla's own numbering (0 small ball, 1 large ball, 2 star, 3 creeper,
    // 4 burst).
    private static final Map<Item, Byte> TYPE_MODIFIER_MAP = Map.of(
            Items.FIRE_CHARGE, (byte) 1,   // large ball
            Items.FEATHER, (byte) 4,        // burst
            Items.GOLD_NUGGET, (byte) 2,    // star
            Items.CREEPER_BANNER_PATTERN, (byte) 3   // creeper
    );

    private static int dyeFireworkColor(Item item) {
        if (item.equals(ItemRegistry.AMBER_DYE.get())) return ModColors.AMBER.getColor();
        if (item.equals(ItemRegistry.AQUA_DYE.get())) return ModColors.AQUA.getColor();
        if (item.equals(ItemRegistry.MAROON_DYE.get())) return ModColors.MAROON.getColor();
        if (item.equals(ItemRegistry.INDIGO_DYE.get())) return ModColors.INDIGO.getColor();
        return item instanceof DyeItem dyeItem ? dyeItem.getDyeColor().getFireworkColor() : DyeColor.WHITE.getFireworkColor();
    }

    private void updateResult() {
        ItemStack newOutput = ItemStack.EMPTY;
        if (slots.get(0).hasItem()) {
            if (slots.get(0).getItem().getItem() instanceof DyeItem && slots.get(13).getItem().is(Items.GUNPOWDER)) {
                if (slots.stream().filter(slot -> slot.getContainerSlot()<10 && slot.hasItem() && slot.container==input).allMatch(slot -> slot.getItem().getItem() instanceof DyeItem)) {
                    byte type = TYPE_MODIFIER_MAP.getOrDefault(slots.get(10).getItem().getItem(), (byte) 0);
                    boolean twinkle = slots.get(11).getItem().is(Items.GLOWSTONE_DUST);
                    boolean trail = slots.get(12).getItem().is(Items.DIAMOND);
                    IntList colorList = new IntArrayList();
                    slots.stream().filter(slot -> slot.getContainerSlot()<5 && slot.hasItem() && slot.container==input)
                            .forEach(slot -> colorList.add(dyeFireworkColor(slot.getItem().getItem())));
                    IntList fadeList = new IntArrayList();
                    slots.stream().filter(slot -> slot.getContainerSlot()>=5 && slot.getContainerSlot()<10 && slot.hasItem() && slot.container==input)
                            .forEach(slot -> fadeList.add(dyeFireworkColor(slot.getItem().getItem())));

                    newOutput = new ItemStack(Items.FIREWORK_STAR);
                    CompoundTag explosion = new CompoundTag();
                    explosion.putByte("Type", type);
                    explosion.put("Colors", new IntArrayTag(colorList.toIntArray()));
                    explosion.put("FadeColors", new IntArrayTag(fadeList.toIntArray()));
                    explosion.putBoolean("Trail", trail);
                    explosion.putBoolean("Flicker", twinkle);
                    newOutput.getOrCreateTagElement("Explosion").merge(explosion);
                }
            } else if (slots.get(0).getItem().is(Items.FIREWORK_STAR) && slots.get(13).getItem().is(Items.PAPER)) {
                if (slots.stream().filter(slot -> slot.getContainerSlot()<5 && slot.hasItem() && slot.container==input).allMatch(slot -> slot.getItem().is(Items.FIREWORK_STAR))) {
                    if (slots.stream().filter(slot -> slot.getContainerSlot() >= 5 && slot.getContainerSlot() < 10 && slot.hasItem() && slot.container==input).allMatch(slot -> slot.getItem().is(Items.GUNPOWDER))) {
                        if (!slots.get(10).hasItem()&&!slots.get(11).hasItem()&&!slots.get(12).hasItem()){
                            int i = Math.toIntExact((slots.stream().filter(slot -> slot.getContainerSlot() >= 5 && slot.getContainerSlot() < 10 && slot.hasItem() && slot.container==input).count()));
                            if (i>0) {
                                ListTag explosions = new ListTag();
                                slots.stream().filter(slot -> slot.getContainerSlot() < 5 && slot.hasItem() && slot.container == input).forEach(slot -> {
                                    CompoundTag starTag = slot.getItem().getTag();
                                    if (starTag != null && starTag.contains("Explosion")) explosions.add(starTag.getCompound("Explosion"));
                                });

                                newOutput = new ItemStack(Items.FIREWORK_ROCKET, 5);
                                CompoundTag fireworks = new CompoundTag();
                                fireworks.putByte("Flight", (byte) i);
                                fireworks.put("Explosions", explosions);
                                newOutput.getOrCreateTagElement("Fireworks").merge(fireworks);
                            }
                        }
                    }
                }
            }
        }
        output.setItem(0, newOutput);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.input);
    }
}
