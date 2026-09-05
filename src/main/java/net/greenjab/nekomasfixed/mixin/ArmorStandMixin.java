package net.greenjab.nekomasfixed.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// hooks interactAt, not interact, since that's the one armour stand overrides. no per-slot armour
// item tags on this version, so each stack is asked which slot it belongs in.
@Mixin(ArmorStand.class)
public class ArmorStandMixin {

    @Unique
    private static boolean nekomasfixed$fitsSlot(ItemStack stack, EquipmentSlot slot) {
        return !stack.isEmpty() && Mob.getEquipmentSlotForItem(stack) == slot;
    }

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void interactAt(Player player, Vec3 location, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if(player!=null && player.isShiftKeyDown()){
            ArmorStand armorStandEntity = (ArmorStand) (Object) this;
            player.swing(hand, true);
            if(nekomasfixed$fitsSlot(armorStandEntity.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD) || nekomasfixed$fitsSlot(player.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD)){
                ItemStack tempItem = armorStandEntity.getItemBySlot(EquipmentSlot.HEAD);
                armorStandEntity.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
                player.setItemSlot(EquipmentSlot.HEAD, tempItem);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            if(nekomasfixed$fitsSlot(armorStandEntity.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST) || nekomasfixed$fitsSlot(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST)){
                ItemStack tempItem = armorStandEntity.getItemBySlot(EquipmentSlot.CHEST);
                armorStandEntity.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
                player.setItemSlot(EquipmentSlot.CHEST, tempItem);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            if(nekomasfixed$fitsSlot(armorStandEntity.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS) || nekomasfixed$fitsSlot(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS)){
                ItemStack tempItem = armorStandEntity.getItemBySlot(EquipmentSlot.LEGS);
                armorStandEntity.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
                player.setItemSlot(EquipmentSlot.LEGS, tempItem);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            if(nekomasfixed$fitsSlot(armorStandEntity.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET) || nekomasfixed$fitsSlot(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET)){
                ItemStack tempItem = armorStandEntity.getItemBySlot(EquipmentSlot.FEET);
                armorStandEntity.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));
                player.setItemSlot(EquipmentSlot.FEET, tempItem);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            //this doesnt work but at the moment i am too lazy to remove it - plz let it stay
            if(!armorStandEntity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && !player.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()){
                ItemStack tempItem = armorStandEntity.getItemBySlot(EquipmentSlot.MAINHAND);
                armorStandEntity.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND));
                player.setItemSlot(EquipmentSlot.MAINHAND, tempItem);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
