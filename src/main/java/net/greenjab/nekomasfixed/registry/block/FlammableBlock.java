package net.greenjab.nekomasfixed.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code FireBlock.setFlammable(block, ignite, burn)} has no Forge
 * equivalent on 1.20.1 - on Forge, per-block fire spread/flammability is an {@code IForgeBlock}
 * override, which {@link Block} already implements by default (Forge patches it in). Mod-block-only,
 * so this stays entirely inside the mod's own block classes: no AT, no map mutation, no mixin, no
 * enqueueWork. Used for {@code baobab_planks} (a plain {@link Block}, the only baobab-set entry with
 * no other special behaviour).
 */
public class FlammableBlock extends Block {
    private final int igniteOdds;
    private final int burnOdds;

    public FlammableBlock(BlockBehaviour.Properties settings, int igniteOdds, int burnOdds) {
        super(settings);
        this.igniteOdds = igniteOdds;
        this.burnOdds = burnOdds;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return igniteOdds;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return burnOdds;
    }
}
