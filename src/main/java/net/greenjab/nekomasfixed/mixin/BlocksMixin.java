package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.block.MelonBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 1.20.1's {@code Blocks.MELON} is one direct field initializer, {@code register("melon", new
 * MelonBlock(BlockBehaviour.Properties.of()...))} (VERIFIED forge-1.20.1-mapped-src Blocks.java:385)
 * — there is no {@code references.BlockItemIds} bootstrap indirection to slice against (that pattern
 * is 26.2-only, see ItemsMixin's header), and only one {@code new MelonBlock(...)} call exists in
 * the whole class, so no {@code @Slice}/ordinal is needed to disambiguate it either.
 * <p>
 * Wraps the constructor call itself (not the outer {@code register(...)}, unlike the pristine 26.2
 * mixin) because that is the only point with direct access to the {@code Properties} this mod's own
 * {@code MelonBlock(boolean, Properties)} also needs — {@code register(String, Block)} only ever
 * sees the already-built instance. The wrapped {@code Operation}/handler is typed as the common
 * {@code Block} rather than vanilla's {@code MelonBlock} specifically, since this mod's own
 * {@code MelonBlock} extends {@code Block} directly, not vanilla's class of the same name (VERIFIED:
 * no relationship between the two beyond the coincidental name) — {@code register}'s own parameter
 * is {@code Block}, so nothing downstream needs the narrower type.
 */
@Mixin(Blocks.class)
public class BlocksMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "Lnet/minecraft/world/level/block/MelonBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static Block newMelon(BlockBehaviour.Properties properties, Operation<Block> original) {
        return new MelonBlock(false, properties);
    }
}
