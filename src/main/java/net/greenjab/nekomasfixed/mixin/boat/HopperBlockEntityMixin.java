package net.greenjab.nekomasfixed.mixin.boat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.entity.BigBoat;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

/**
 * 1.20.1 has no {@code getEntityContainer} method — the entity-based container lookup
 * {@code EntitySelector.CONTAINER_ENTITY_SELECTOR} feeds is inside the private
 * {@code getContainerAt(Level, double, double, double)} overload (VERIFIED
 * forge-1.20.1-mapped-src HopperBlockEntity.java:343,361; the field itself,
 * {@code EntitySelector.CONTAINER_ENTITY_SELECTOR}, is unchanged). {@code getContainerAt} is
 * overloaded ({@code (Level,BlockPos)} is also present), so the descriptor is given explicitly
 * rather than relying on name-only resolution.
 */
@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @Unique
    private static final Predicate<Entity> NEW_VALID_INVENTORIES = entity -> entity instanceof Container && entity.isAlive() && (!(entity instanceof BigBoat bigBoat) || bigBoat.hasChest());

    @WrapOperation(method = "getContainerAt(Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/Container;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntitySelector;CONTAINER_ENTITY_SELECTOR:Ljava/util/function/Predicate;", opcode = Opcodes.GETSTATIC))
    private static Predicate<Entity> dontGetBigChestWhenNoChest(Operation<Predicate<Entity>> original){
        return NEW_VALID_INVENTORIES;
    }


}
