package net.greenjab.nekomasfixed.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Breaking a nautilus item drops the animal stored in it back into the world, and placing a clam
// item that was picked up open places it open again. Both bits of stack data go through StackData.
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

    @Inject(method="updateBlockStateFromTag", at = @At( value = "HEAD"))
    private void placeOpenClam(BlockPos pos, Level level, ItemStack itemStack, BlockState placedState, CallbackInfoReturnable<BlockState> cir) {
        if (itemStack.is(ModTags.CLAMTAG)) {
            int i = StackData.readClamState(itemStack);
            if (i > 0) {
                placedState = placedState.setValue(ClamBlock.OPEN, true);
                level.setBlock(pos, placedState, Block.UPDATE_CLIENTS);
            }
        }
    }
}
