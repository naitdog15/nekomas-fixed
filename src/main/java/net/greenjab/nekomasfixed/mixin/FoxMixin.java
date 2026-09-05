package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

@Mixin(Fox.class)
public class FoxMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void customTick(CallbackInfo ci){
        if (!NekomasFixedConfig.FOXES_USE_POTIONS.get()) return;
        Fox foxEntity = (Fox)(Object)this;
        Level world = foxEntity.level();
        // Everything below writes to the world, so it belongs to the server alone; the client
        // used to run it too and place its own soul fire off its own roll.
        if (world.isClientSide()) return;
        int chance = 10;
        boolean randInt = foxEntity.getRandom().nextInt(chance) == 5;
        ItemStack stack = foxEntity.getMainHandItem();
        if(stack.is(ItemTags.HOES) && randInt){
            if(world.getBlockState(foxEntity.blockPosition().below()).is(BlockTags.DIRT)){
                world.setBlockAndUpdate(foxEntity.blockPosition().below(), Blocks.FARMLAND.defaultBlockState());
                stack.hurtAndBreak(1, foxEntity, holder -> holder.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }else if(stack.is(Items.SHEARS) && randInt){
            List<Sheep> nearbySheeps = world.getEntitiesOfClass(Sheep.class, foxEntity.getBoundingBox().inflate(1), ignored -> true);
            Sheep sheep;
            if(!nearbySheeps.isEmpty()){
                sheep = nearbySheeps.get(0);
                if(sheep.readyForShearing()){
                    if (world.isClientSide()) {return;}
                    sheep.shear(SoundSource.PLAYERS);
                    sheep.gameEvent(GameEvent.SHEAR, foxEntity);
                    stack.hurtAndBreak(1, foxEntity, holder -> holder.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
            }
        }
        else if(stack.is(Items.FLINT_AND_STEEL) && randInt){
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(foxEntity.getRandom());
            if(world.getBlockState(foxEntity.blockPosition().below()).is(Blocks.SOUL_SOIL)){
                world.setBlockAndUpdate(foxEntity.blockPosition().relative(dir), Blocks.SOUL_FIRE.defaultBlockState());
                stack.hurtAndBreak(1, foxEntity, holder -> holder.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }else{
                world.setBlockAndUpdate(foxEntity.blockPosition().relative(dir), Blocks.FIRE.defaultBlockState());
                stack.hurtAndBreak(1, foxEntity, holder -> holder.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }else if(stack.is(Items.POTION) && randInt){
            Potion potionContent = PotionUtils.getPotion(stack);
            if (potionContent != null && !potionContent.getEffects().isEmpty()) {
                for(MobEffectInstance effect : potionContent.getEffects()){
                    MobEffectInstance copy = new MobEffectInstance(effect);
                    foxEntity.addEffect(copy);
                }
                foxEntity.setItemInHand(InteractionHand.MAIN_HAND, Items.GLASS_BOTTLE.getDefaultInstance());
            }
        }else if(stack.is(Items.BUCKET) && randInt){
            // no Entity#getNearestViewDirection on this version - snap the look vector to an axis by hand
            Vec3 look = foxEntity.getViewVector(1.0F);
            Direction viewDir = Direction.getNearest(look.x, look.y, look.z);
            if(world.getFluidState(foxEntity.blockPosition().below().relative(viewDir)).is(Fluids.WATER)){
                world.setBlockAndUpdate(foxEntity.blockPosition().below().relative(viewDir), Blocks.AIR.defaultBlockState());
                foxEntity.setItemInHand(InteractionHand.MAIN_HAND, Items.WATER_BUCKET.getDefaultInstance());
            }else if(world.getFluidState(foxEntity.blockPosition().below().relative(viewDir)).is(Fluids.LAVA)){
                world.setBlockAndUpdate(foxEntity.blockPosition().below().relative(viewDir), Blocks.AIR.defaultBlockState());
                foxEntity.setItemInHand(InteractionHand.MAIN_HAND, Items.LAVA_BUCKET.getDefaultInstance());
            }
        }
    }
}
