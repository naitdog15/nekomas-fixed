package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.SpottedSheepAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.greenjab.nekomasfixed.screen.config.ModConfigValues;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Swaps a spotted sheep's wool drop for the matching spotted wool, and gives copper armour a
 * lightning kick.
 *
 * <p>The drop hook sits on {@code spawnAtLocation(ItemStack, float)} - the overload every other one
 * funnels into - so it catches the drop however the sheep produced it.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"), argsOnly = true)
    private ItemStack replaceSpottedSheepDrops(ItemStack itemStack) {
        if ((Object) this instanceof Sheep sheep) {
            if (((SpottedSheepAccess) sheep).nekomasfixed$isSpotted()) {
                Item replacementItem = this.getSpottedWoolItem(itemStack.getItem());
                if (replacementItem != null) return new ItemStack(replacementItem, itemStack.getCount());
            }
        }
        return itemStack;
    }

    @Unique
    private Item getSpottedWoolItem(Item original) {
        if (original == Items.WHITE_WOOL) return BlockRegistry.WHITE_SPOTTED_WOOL.get().asItem();
        if (original == Items.ORANGE_WOOL) return BlockRegistry.ORANGE_SPOTTED_WOOL.get().asItem();
        if (original == Items.MAGENTA_WOOL) return BlockRegistry.MAGENTA_SPOTTED_WOOL.get().asItem();
        if (original == Items.LIGHT_BLUE_WOOL) return BlockRegistry.LIGHT_BLUE_SPOTTED_WOOL.get().asItem();
        if (original == Items.YELLOW_WOOL) return BlockRegistry.YELLOW_SPOTTED_WOOL.get().asItem();
        if (original == Items.LIME_WOOL) return BlockRegistry.LIME_SPOTTED_WOOL.get().asItem();
        if (original == Items.PINK_WOOL) return BlockRegistry.PINK_SPOTTED_WOOL.get().asItem();
        if (original == Items.GRAY_WOOL) return BlockRegistry.GRAY_SPOTTED_WOOL.get().asItem();
        if (original == Items.LIGHT_GRAY_WOOL) return BlockRegistry.LIGHT_GRAY_SPOTTED_WOOL.get().asItem();
        if (original == Items.CYAN_WOOL) return BlockRegistry.CYAN_SPOTTED_WOOL.get().asItem();
        if (original == Items.PURPLE_WOOL) return BlockRegistry.PURPLE_SPOTTED_WOOL.get().asItem();
        if (original == Items.BLUE_WOOL) return BlockRegistry.BLUE_SPOTTED_WOOL.get().asItem();
        if (original == Items.BROWN_WOOL) return BlockRegistry.BROWN_SPOTTED_WOOL.get().asItem();
        if (original == Items.GREEN_WOOL) return BlockRegistry.GREEN_SPOTTED_WOOL.get().asItem();
        if (original == Items.RED_WOOL) return BlockRegistry.RED_SPOTTED_WOOL.get().asItem();
        if (original == Items.BLACK_WOOL) return BlockRegistry.BLACK_SPOTTED_WOOL.get().asItem();

        if (original == ItemRegistry.AMBER_WOOL.get()) return BlockRegistry.AMBER_SPOTTED_WOOL.get().asItem();
        if (original == ItemRegistry.AQUA_WOOL.get()) return BlockRegistry.AQUA_SPOTTED_WOOL.get().asItem();
        if (original == ItemRegistry.INDIGO_WOOL.get()) return BlockRegistry.INDIGO_SPOTTED_WOOL.get().asItem();
        if (original == ItemRegistry.MAROON_WOOL.get()) return BlockRegistry.MAROON_SPOTTED_WOOL.get().asItem();

        return null;
    }

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void tickThunder(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        if (ModConfigValues.enableCopperBuff) {
            if ((Entity)(Object)this instanceof ServerPlayer player) {
                int armor = getCopperArmor(player);
                if (armor > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3*armor * 20, armor, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, armor, false, false, false));
                }
            }
        }
    }

    // Counts the copper armour pieces worn. There is no copper armour on this version - neither
    // vanilla's nor any this mod registers - so nothing can occupy those slots yet and the count is
    // always zero; the buff above wires straight back up once a copper set exists.
    @Unique
    private static int getCopperArmor(LivingEntity entity) {
        return 0;
    }
}
