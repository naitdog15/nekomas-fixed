package net.greenjab.nekomasfixed.render.block.entity.state;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class HollowLogBlockEntityRenderState extends BlockEntityRenderState {
    public BlockState blockState = Blocks.AIR.defaultBlockState();
}