package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// vanilla hard-codes this carve-out one item at a time (see the nether star in ItemEntity#hurt), so
// this widens it to a tag. 1.20.1's SmithingTemplateItem builds its own Item.Properties and takes
// none from the caller, so fireproofing the templates the ordinary way isn't on offer here.
@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void surviveBlastsAndFire(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!NekomasFixedConfig.EXPLOSION_RESISTANT_TEMPLATES.get()) return;
        if (!source.is(DamageTypeTags.IS_EXPLOSION) && !source.is(DamageTypeTags.IS_FIRE)) return;
        if (((ItemEntity) (Object) this).getItem().is(ModTags.BLAST_AND_FIRE_PROOF)) cir.setReturnValue(false);
    }
}
