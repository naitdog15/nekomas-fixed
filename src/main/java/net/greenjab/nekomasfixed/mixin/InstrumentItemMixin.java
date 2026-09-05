package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.block.enums.GoatHornType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// DataComponents.INSTRUMENT/InstrumentComponent don't exist on 1.20.1 (see BlockBehaviourMixin).
// InstrumentItem#use still returns InteractionResultHolder<ItemStack> here, not bare InteractionResult.
@Mixin(InstrumentItem.class)
public class InstrumentItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (level.isClientSide()) return;
        ItemStack itemStack = player.getItemInHand(hand);
        ResourceKey<Instrument> instrumentKey = nekomasfixed$readInstrument(itemStack);
        if (instrumentKey != null) {
            ServerLevel serverWorld = (ServerLevel) level;
            MobEffectInstance glow = new MobEffectInstance(MobEffects.GLOWING, 30 * 20);
            for(Entity entity : serverWorld.getAllEntities()) {
                if (entity instanceof IronGolem ironGolem) {
                    if (player.hasEffect(MobEffects.BAD_OMEN))
                        ironGolem.addEffect(GoatHornType.fromInstrument(instrumentKey).getStatusEffect());
                }
                if (entity instanceof TamableAnimal tameable && tameable.isTame()) tameable.addEffect(glow);
                if (entity instanceof AbstractHorse horse && horse.isTamed()) horse.addEffect(glow);
            }
        }
    }

    @Unique
    private static ResourceKey<Instrument> nekomasfixed$readInstrument(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("instrument", 8)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("instrument"));
            if (id != null) {
                return ResourceKey.create(Registries.INSTRUMENT, id);
            }
        }
        return null;
    }
}
