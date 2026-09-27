package net.greenjab.nekomasfixed.mixin.accessor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandAccessor {

    @Invoker("isDisabled")
    boolean invokeIsDisabled(EquipmentSlot slot);
}
