package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.ColorMerger;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

// carries several potions and releases one lingering cloud per potion on its first hit.
// it holds no vanilla potion, so AbstractArrowMixin's single-potion branch ignores it.
public class CustomArrow extends Arrow {
    private static final String TAG_POTIONS = "CustomPotions";

    private final List<Potion> potions = new ArrayList<>();
    private boolean spent;

    public CustomArrow(EntityType<? extends CustomArrow> type, Level level) {
        super(type, level);
    }

    public static CustomArrow create(Level level, LivingEntity shooter, ItemStack source) {
        CustomArrow arrow = new CustomArrow(EntityTypeRegistry.CUSTOM_ARROW.get(), level);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        arrow.setOwner(shooter);
        arrow.potions.addAll(StackData.readTippedPotions(source));
        arrow.applyTint();
        return arrow;
    }

    public static CustomArrow createAt(Level level, double x, double y, double z, ItemStack source) {
        CustomArrow arrow = new CustomArrow(EntityTypeRegistry.CUSTOM_ARROW.get(), level);
        arrow.setPos(x, y, z);
        arrow.potions.addAll(StackData.readTippedPotions(source));
        arrow.applyTint();
        return arrow;
    }

    private void applyTint() {
        int[] colors = new int[this.potions.size()];
        for (int i = 0; i < colors.length; i++) colors[i] = PotionUtils.getColor(this.potions.get(i));
        ItemStack tint = new ItemStack(Items.TIPPED_ARROW);
        tint.getOrCreateTag().putInt("CustomPotionColor", ColorMerger.mix(colors));
        this.setEffectsFromItem(tint);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (Potion potion : this.potions) list.add(StringTag.valueOf(BuiltInRegistries.POTION.getKey(potion).toString()));
        tag.put(TAG_POTIONS, list);
        tag.putBoolean("CustomSpent", this.spent);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.potions.clear();
        ListTag list = tag.getList(TAG_POTIONS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null && BuiltInRegistries.POTION.containsKey(id)) this.potions.add(BuiltInRegistries.POTION.get(id));
        }
        this.spent = tag.getBoolean("CustomSpent");
    }

    @Override
    protected ItemStack getPickupItem() {
        ItemStack stack = new ItemStack(ItemRegistry.TIPPED_ARROW_CUSTOM.get());
        StackData.writeTippedPotions(stack, this.potions);
        return stack;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity mob) {
        super.doPostHurtEffects(mob);
        spawnClouds(mob.getX(), mob.getY(), mob.getZ());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        spawnClouds(this.getX(), this.getY(), this.getZ());
    }

    private void spawnClouds(double x, double y, double z) {
        if (this.spent || this.level().isClientSide) return;
        this.spent = true;
        for (Potion potion : this.potions) {
            this.level().addFreshEntity(makeAreaEffectCloudEntity(this.level(), x, y, z, potion));
        }
    }

    private static AreaEffectCloud makeAreaEffectCloudEntity(Level level, double x, double y, double z, Potion potion) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, x, y, z);
        cloud.setRadius(2.0F);
        cloud.setRadiusOnUse(0F);
        cloud.setDuration(100);
        cloud.setWaitTime(5);
        cloud.setPotion(potion);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        return cloud;
    }
}
