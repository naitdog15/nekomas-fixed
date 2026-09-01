package net.greenjab.nekomasfixed.registry.worldgen.tree;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.block.RopeBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.util.ModTreeDecorators;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

import static net.greenjab.nekomasfixed.registry.block.BaobabFruitBlock.AGE;

public class BaobabTreeDecorator extends TreeDecorator {

    private final float probability;
    public static final Codec<BaobabTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BaobabTreeDecorator::new, (decorator) -> decorator.probability).codec();

    public BaobabTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.BAOBAB_TREE_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context generator) {
        RandomSource random = generator.random();

        List<BlockPos> list = generator.leaves();
        if (!list.isEmpty()) {
            for(BlockPos pos : list){
                if (random.nextFloat()<0.1f) {
                    BlockPos fruitPos = pos.below();
                    if (generator.level().isStateAtPosition(fruitPos, state -> state.is(BlockTags.REPLACEABLE)) && !generator.logs().contains(fruitPos)) {
                        boolean playerGrown = !(generator.level() instanceof WorldGenRegion);
                        if (!playerGrown) {
                            for (int rope = 3 + random.nextInt(5); rope >= 0; rope--) {
                                BlockPos finalFruitPos = fruitPos;
                                if (generator.level().isStateAtPosition(fruitPos.below(), state -> state.is(BlockTags.REPLACEABLE) && !generator.logs().contains(finalFruitPos))) {
                                    generator.setBlock(fruitPos, BlockRegistry.ROPE.get().defaultBlockState().setValue(RopeBlock.ATTACHED, true));
                                    fruitPos = fruitPos.below();
                                }
                            }
                            generator.setBlock(fruitPos, BlockRegistry.BAOBAB_FRUIT.get().defaultBlockState().setValue(AGE, 1));
                        } else generator.setBlock(fruitPos, BlockRegistry.BAOBAB_FRUIT.get().defaultBlockState().setValue(AGE, 0));
                    }
                }
            }
        }
    }
}
