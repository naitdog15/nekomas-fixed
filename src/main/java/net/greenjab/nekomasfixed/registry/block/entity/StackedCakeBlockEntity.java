package net.greenjab.nekomasfixed.registry.block.entity;

import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StackedCakeBlockEntity extends BlockEntity {
    public BlockState LAYER_2_STATE = Blocks.AIR.defaultBlockState();
    public BlockState LAYER_3_STATE = Blocks.AIR.defaultBlockState();
    public BlockState CANDLE_STATE = Blocks.AIR.defaultBlockState();
    public StackedCakeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.STACKED_CAKE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        putState(tag, "layer_2", LAYER_2_STATE);
        putState(tag, "layer_3", LAYER_3_STATE);
        putState(tag, "candle", CANDLE_STATE);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        LAYER_2_STATE = readState(tag, "layer_2");
        LAYER_3_STATE = readState(tag, "layer_3");
        CANDLE_STATE = readState(tag, "candle");
    }

    private static void putState(CompoundTag tag, String key, BlockState state) {
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state).result().ifPresent(encoded -> tag.put(key, encoded));
    }

    private static BlockState readState(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) {
            return Blocks.AIR.defaultBlockState();
        }
        return BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get(key)).result().orElse(Blocks.AIR.defaultBlockState());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
