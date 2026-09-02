package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.entity.SpearEntity;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// A dispenser loaded with a spear plants it as a standing trap rather than tossing it as an item.
// Membership is the mod's own nekomasfixed:spears tag, which pulls in whatever spears the game has
// to offer and stays empty - and harmless - when it has none.
@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {

    @Inject(at = @At("HEAD"), method = "execute", cancellable = true)
    public void SpearAttack(BlockSource source, ItemStack dispensed, CallbackInfoReturnable<ItemStack> cir) {
        if (!NekomasFixedConfig.SPEAR_INTERACTIONS.get()) return;

        Level level = source.getLevel();
        if (level.isClientSide())  return;
        if (!source.getBlockState().is(Blocks.DISPENSER))  return;

        BlockPos pos = BlockPos.containing(DispenserBlock.getDispensePosition(source));
        BlockState blockState = level.getBlockState(pos);

        if (!blockState.getCollisionShape(level, pos).isEmpty()) return;
        if (!level.getEntitiesOfClass(SpearEntity.class, new AABB(pos).inflate(-0.2, -0.2, -0.2)).isEmpty()){
            cir.setReturnValue(dispensed);
            return;
        }

        if (dispensed.is(ModTags.SPEARS)) {
            SpearEntity entity = EntityTypeRegistry.SPEAR.get().create(level);
            if (entity != null) {
                entity.absMoveTo(pos.getX()+0.5, pos.getY()+0.2, pos.getZ()+0.5, 0, 0);
                entity.setStack(dispensed);
                entity.setDirection( source.getBlockState().getValue(DispenserBlock.FACING));
                level.addFreshEntity(entity);
                cir.setReturnValue(dispensed);
            }
        }
    }
}
