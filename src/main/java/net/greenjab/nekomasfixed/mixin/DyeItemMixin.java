package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.ModDyeItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.greenjab.nekomasfixed.util.ModColors.*;

// 1.20.1 delta: DyeItem#tryApplyToSign(Level, SignBlockEntity, boolean, Player) has no ItemStack
// parameter (VERIFIED forge-1.20.1-mapped-src DyeItem.java:49) — the dye identity comes from `this`
// (the mixin's own target instance) instead, since it is an instance method on the dye item itself.
@Mixin(DyeItem.class)
public class DyeItemMixin {

    // The four extra dyes have to borrow a vanilla DyeColor, and every DyeItem files itself under
    // its colour on the way through the constructor. Letting them do that would leave
    // DyeItem.byColor(YELLOW) answering "amber" - which is what a bred sheep's colour lookup and the
    // shepherd's random dye trade both read - so the mod's dyes skip the map and vanilla keeps it.
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private Object keepVanillaDyeForBorrowedColor(Map<DyeColor, DyeItem> itemByColor, Object color, Object dye) {
        if (dye instanceof ModDyeItems) return null;
        return itemByColor.put((DyeColor) color, (DyeItem) dye);
    }

    @Inject(method = "tryApplyToSign", at = @At("RETURN"), cancellable = true)
    private void changeDye(Level level, SignBlockEntity sign, boolean isFrontText, Player player, CallbackInfoReturnable<Boolean> cir) {
        DyeItem self = (DyeItem)(Object)this;
        if (self == ItemRegistry.AMBER_DYE.get()) {
            applyDye(sign, isFrontText, AMBER.getColor());
            cir.setReturnValue(true);
        }
        if (self == ItemRegistry.AQUA_DYE.get()) {
            applyDye(sign, isFrontText, AQUA.getColor());
            cir.setReturnValue(true);
        }
        if (self == ItemRegistry.INDIGO_DYE.get()) {
            applyDye(sign, isFrontText, INDIGO.getColor());
            cir.setReturnValue(true);
        }

        if (self == ItemRegistry.MAROON_DYE.get()) {
            applyDye(sign, isFrontText, MAROON.getColor());
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void applyDye(SignBlockEntity sign, boolean front, int color) {
        SignText signText = sign.getText(front);
        for (int i = 0; i < 4; i++) {
            Component line = signText.getMessage(i, false);
            MutableComponent newLine = line.plainCopy();
            newLine.setStyle(line.getStyle().withColor(color));
            signText = signText.setMessage(i, newLine, newLine);
        }
        sign.setText(signText, front);
        sign.setChanged();
        assert sign.getLevel() != null;
        sign.getLevel().sendBlockUpdated(
                sign.getBlockPos(),
                sign.getBlockState(),
                sign.getBlockState(),
                Block.UPDATE_ALL
        );
    }

}
