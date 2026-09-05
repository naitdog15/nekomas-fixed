package net.greenjab.nekomasfixed.registry.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.greenjab.nekomasfixed.registry.entity.WildfireTrident;
import net.greenjab.nekomasfixed.render.entity.NekomasFixedBEWLR;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class WildfireTridentItem extends Item {

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public WildfireTridentItem(Item.Properties settings) {
        super(settings);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            int i = this.getUseDuration(stack) - remainingUseTicks;
            if (i < 10) return;
            int j = EnchantmentHelper.getRiptide(stack);
            // riptide trigger treats being on fire as "wet" too - matches the item's theming
            if (j > 0 && !playerEntity.isInWaterOrRain() && !playerEntity.isOnFire()) return;
            if (stack.getDamageValue() >= stack.getMaxDamage() - 1) return;

            if (!level.isClientSide) {
                stack.hurtAndBreak(1, playerEntity, entity -> entity.broadcastBreakEvent(playerEntity.getUsedItemHand()));
                if (j == 0) {
                    WildfireTrident tridentEntity = new WildfireTrident(level, playerEntity, stack);
                    tridentEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, 2.5F + (float) j * 0.5F, 1.0F);
                    if (playerEntity.getAbilities().instabuild) tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    level.addFreshEntity(tridentEntity);
                    level.playSound(null, tridentEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!playerEntity.getAbilities().instabuild) playerEntity.getInventory().removeItem(stack);
                }
            }

            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (j > 0) {
                float g = playerEntity.getYRot();
                float h = playerEntity.getXRot();
                float jx = -Mth.sin(g * ((float) Math.PI / 180F)) * Mth.cos(h * ((float) Math.PI / 180F));
                float kx = -Mth.sin(h * ((float) Math.PI / 180F));
                float lx = Mth.cos(g * ((float) Math.PI / 180F)) * Mth.cos(h * ((float) Math.PI / 180F));
                float m = Mth.sqrt(jx * jx + kx * kx + lx * lx);
                float thrust = 3.0F * ((1.0F + (float) j) / 4.0F);
                float fx = jx * thrust / m;
                float fy = kx * thrust / m;
                float fz = lx * thrust / m;
                playerEntity.push(fx, fy, fz);
                playerEntity.startAutoSpinAttack(20);
                if (playerEntity.onGround()) playerEntity.move(MoverType.SELF, new Vec3(0.0D, 1.1999999D, 0.0D));

                SoundEvent soundEvent;
                if (j >= 3) soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
                else if (j == 2) soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
                else soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
                level.playSound(null, playerEntity, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isInWaterOrRain() && !user.isOnFire()) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    // custom renderer only kicks in for a model that asks for it; sprite models ignore this
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return NekomasFixedBEWLR.instance();
            }
        });
    }
}
