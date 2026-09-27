package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.registry.block.ClamBlock;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.util.ModTags;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method="onDestroyed", at = @At( value = "HEAD"), cancellable = true)
    private void releaseAnimalOnNautilusDestroyed(ItemEntity entity, CallbackInfo ci) {
        AnimalComponent animalComponent = StackData.readAnimal(entity.getItem());
        if (!animalComponent.animal().isEmpty()) {
            AnimalComponent.StoredEntityData animal = animalComponent.animal().get(0);
            Level level = entity.level();
            BlockPos pos = entity.blockPosition();
            Entity releasedEntity = animal.loadEntity(level);
            if (releasedEntity != null) {
                double e = pos.getX() + 0.5;
                double g = pos.getY() + 0.5 - releasedEntity.getBbHeight() / 2.0F;
                double h = pos.getZ() + 0.5;
                releasedEntity.moveTo(e, g, h, releasedEntity.getYRot(), releasedEntity.getXRot());
                level.addFreshEntity(releasedEntity);
            }
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "updateBlockStateFromTag", at = @At("RETURN"))
    private BlockState placeOpenClam(BlockState original, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Level level, @Local(argsOnly = true) ItemStack itemStack) {
        if (itemStack.is(ModTags.CLAMTAG)) {
            int i = StackData.readClamState(itemStack);
            if (i > 0) {
                BlockState openState = original.setValue(ClamBlock.OPEN, true);
                level.setBlock(pos, openState, Block.UPDATE_CLIENTS);
                return openState;
            }
        }
        return original;
    }
}
