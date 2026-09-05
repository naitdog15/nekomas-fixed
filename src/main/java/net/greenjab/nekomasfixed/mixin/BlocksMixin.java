package net.greenjab.nekomasfixed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.nekomasfixed.registry.block.MelonBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// 1.20.1's Blocks.MELON is one direct field initializer with only one `new MelonBlock(...)` call in
// the class, so no @Slice/ordinal is needed. wraps the constructor itself, not register(...), since
// that's the only point with access to the Properties this mod's own MelonBlock(boolean, Properties) needs.
@Mixin(Blocks.class)
public class BlocksMixin {
    // NEW targets take the constructor-descriptor form "(args)LConstructedType;" — the
    // invoke-style "<init>(args)V" form parses the constructed type as void and never matches.
    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/MelonBlock;"))
    private static net.minecraft.world.level.block.MelonBlock newMelon(BlockBehaviour.Properties properties, Operation<net.minecraft.world.level.block.MelonBlock> original) {
        return new MelonBlock(false, properties);
    }
}
