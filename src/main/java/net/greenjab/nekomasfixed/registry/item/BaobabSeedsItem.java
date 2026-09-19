package net.greenjab.nekomasfixed.registry.item;

import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BaobabSeedsItem extends Item {
    public BaobabSeedsItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        if(state.is(BlockTags.LEAVES)){
            BlockPos below = pos.below();
            if (world.getBlockState(below).isAir() || world.getBlockState(below).is(BlockTags.REPLACEABLE)) {
                world.setBlockAndUpdate(below, BlockRegistry.BAOBAB_FRUIT.defaultBlockState());
                context.getItemInHand().consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
