package net.greenjab.nekomasfixed.registry.block.entity;

import net.greenjab.nekomasfixed.network.SyncHandler;
import net.greenjab.nekomasfixed.network.UpdateClockPayload;
import net.greenjab.nekomasfixed.registry.block.AbstractClockBlock;
import net.greenjab.nekomasfixed.registry.block.FloorClockBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.PacketDistributor;
import javax.annotation.Nullable;

public class ClockBlockEntity extends BlockEntity {
	private int storedTime =-1;
	public static int timerDuration = 60;
	private int timer = -timerDuration;
	private boolean bell = false;
	private boolean showsTime = false;

	protected ClockBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public ClockBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityTypeRegistry.CLOCK_BLOCK_ENTITY.get(), pos, state);
	}

	/**
	 * Every read is guarded on the key actually being there. The clock has no update tag of its own
	 * - the client is fed by {@link UpdateClockPayload} instead - so this also runs against the
	 * empty tag of a block-entity update packet, and an unguarded getInt would silently reset the
	 * alarm to midnight and restart the timer every time a neighbour changed.
	 */
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("storedTime", Tag.TAG_INT)) this.setStoredTime(tag.getInt("storedTime"));
		if (tag.contains("timer", Tag.TAG_INT)) this.setTimer(tag.getInt("timer"));
		if (tag.contains("bell", Tag.TAG_BYTE)) this.setBell(tag.getBoolean("bell"));
		if (tag.contains("showsTime", Tag.TAG_BYTE)) this.setShowsTime(tag.getBoolean("showsTime"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("storedTime", this.getStoredTime());
		tag.putInt("timer", this.getTimer());
		tag.putBoolean("bell", this.hasBell());
		tag.putBoolean("showsTime", this.getShowsTime());
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** The bell is an item the player put in, so it has to come back out when the clock is broken. */
	public void dropBell() {
		if (this.level != null && bell) {
			Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), Items.BELL.getDefaultInstance());
		}
	}

	@Override
	public boolean triggerEvent(int type, int data) {
		return super.triggerEvent(type, data);
	}

	public void setStoredTime(int time) {
		storedTime = time;
	}
	public int getStoredTime() {
		return storedTime;
	}
	public void setTimer(int time) {
		timer = time;
	}
	public int getTimer() {
		return timer;
	}
	public void setBell(boolean hasbell) {
		bell = hasbell;
	}
	public boolean hasBell() {
		return bell;
	}
	public void setShowsTime(boolean showTime) {
		showsTime = showTime;
	}
	public boolean getShowsTime() {
		return showsTime;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, ClockBlockEntity blockEntity) {
		boolean powered = state.getValue(AbstractClockBlock.POWERED);
		boolean shouldBePowered = false;
		if ((int) ((level.getDayTime() + 6000) % 24000)==blockEntity.storedTime) {
			blockEntity.timer=0;
		}
		if (blockEntity.timer>-timerDuration) {
			blockEntity.timer--;
			if (blockEntity.timer<1) {
				shouldBePowered = true;
			}
		}
		if (level.getGameTime() % 20L == 0L) {
			if (level instanceof ServerLevel serverLevel) {
				level.updateNeighbourForOutputSignal(pos, state.getBlock());
				UpdateClockPayload payload = new UpdateClockPayload(pos.getX(), pos.getY(), pos.getZ(), blockEntity.getTimer(), blockEntity.hasBell(), blockEntity.getShowsTime());
                sendToAround(serverLevel.getServer()
								.getPlayerList(),
						null,
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						100,
						level.dimension(),
						payload
				);
			}
		}
		if (powered!=shouldBePowered) {
			((AbstractClockBlock)state.getBlock()).setPower(level, pos, state, shouldBePowered);
		}
		if (blockEntity.hasBell() && state.getBlock() instanceof FloorClockBlock && shouldBePowered && level.getGameTime() % 5L == 0L){
			level.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, pos);
			level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 0.3F, 2f);
		}
	}

	public static void sendToAround(PlayerList playerManager, @Nullable Player player, double x, double y, double z, double distance, ResourceKey<Level> worldKey, UpdateClockPayload payload) {
		for (int i = 0; i < playerManager.getPlayers().size(); i++) {
			ServerPlayer serverPlayerEntity = playerManager.getPlayers().get(i);
			if (serverPlayerEntity != player && serverPlayerEntity.level().dimension() == worldKey) {
				double d = x - serverPlayerEntity.getX();
				double e = y - serverPlayerEntity.getY();
				double f = z - serverPlayerEntity.getZ();
				if (d * d + e * e + f * f < distance * distance) {
					SyncHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), payload);
				}
			}
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, ClockBlockEntity blockEntity) {
		if (blockEntity.timer>-timerDuration) {
			blockEntity.timer--;
		}
	}

}
