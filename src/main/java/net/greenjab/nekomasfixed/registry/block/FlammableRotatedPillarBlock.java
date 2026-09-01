package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * See {@link FlammableBlock}. Used for the baobab log/wood set.
 * {@code strippedSupplier} is null for the already-stripped variants (no further axe-stripping
 * target); non-null for {@code baobab_log}/{@code baobab_wood}, which strip into
 * {@code stripped_baobab_log}/{@code stripped_baobab_wood} - the Forge replacement for Fabric's
 * {@code StrippableBlockRegistry.register(log, stripped)}, both sites mod->mod so no AT is needed.
 * The supplier is a lazy {@code RegistryObject::get} reference, never dereferenced before the
 * stripped block is actually registered.
 */
public class FlammableRotatedPillarBlock extends RotatedPillarBlock {
    private final int igniteOdds;
    private final int burnOdds;
    @Nullable
    private final Supplier<? extends Block> strippedSupplier;

    public FlammableRotatedPillarBlock(BlockBehaviour.Properties settings, int igniteOdds, int burnOdds,
                                        @Nullable Supplier<? extends Block> strippedSupplier) {
        super(settings);
        this.igniteOdds = igniteOdds;
        this.burnOdds = burnOdds;
        this.strippedSupplier = strippedSupplier;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return igniteOdds;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return burnOdds;
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (strippedSupplier != null && toolAction == ToolActions.AXE_STRIP) {
            return strippedSupplier.get().withPropertiesOf(state);
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
