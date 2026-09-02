package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.block.MelonBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 1.20.1's {@code Blocks.MELON} is one direct field initializer, {@code register("melon", new
 * MelonBlock(BlockBehaviour.Properties.of()...))} — there is no {@code references.BlockItemIds} bootstrap indirection to slice against (that pattern
 * is 26.2-only, see ItemsMixin's header), and only one {@code new MelonBlock(...)} call exists in
 * the whole class, so no {@code @Slice}/ordinal is needed to disambiguate it either.
 * <p>
 * Wraps the constructor call itself (not the outer {@code register(...)}, unlike the pristine 26.2
 * mixin) because that is the only point with direct access to the {@code Properties} this mod's own
 * {@code MelonBlock(boolean, Properties)} also needs — {@code register(String, Block)} only ever
 * sees the already-built instance. The wrapped {@code Operation}/handler is typed as vanilla's
 * {@code MelonBlock} specifically rather than the common {@code Block}, matching the constructor
 * being wrapped exactly. This mod's own {@code MelonBlock} extends vanilla's class of the same name
 * for that reason, so the instance handed back here really is one.
 */
@Mixin(Blocks.class)
public class BlocksMixin {
    // NEW targets take the constructor-descriptor form "(args)LConstructedType;" — the
    // invoke-style "<init>(args)V" form parses the constructed type as void and never matches.
    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/MelonBlock;"))
    private static net.minecraft.world.level.block.MelonBlock newMelon(BlockBehaviour.Properties properties, Operation<net.minecraft.world.level.block.MelonBlock> original) {
        return new MelonBlock(false, properties);
    }
}
