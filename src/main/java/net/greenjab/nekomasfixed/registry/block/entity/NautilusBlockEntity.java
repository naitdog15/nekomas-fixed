package net.greenjab.nekomasfixed.registry.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.greenjab.nekomasfixed.registry.block.NautilusBlock;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class NautilusBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final List<AnimalComponent.StoredEntityData> animal = Lists.newArrayList();

	public NautilusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.NAUTILUS_BLOCK_ENTITY.get(), pos, state);
	}

	public boolean hasAnimal() {
		return !this.animal.isEmpty();
	}

	public void tryEnterNautilus(Animal animal) {
		if (this.animal.isEmpty()) {
			animal.stopRiding();
			animal.ejectPassengers();
			animal.dropLeash(true, true);
			this.animal.add(AnimalComponent.StoredEntityData.of(animal));
			if (this.level != null) {

				BlockPos blockPos = this.getBlockPos();
				this.level
					.playSound(
						null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F
					);
				this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(animal, this.getBlockState()));
			}

			animal.discard();
			super.setChanged();
		}
	}

	public List<Entity> tryReleaseAnimal(BlockState state) {
		List<Entity> list = Lists.newArrayList();
		if (this.level!=null) {
			this.animal.removeIf(data -> releaseAnimal(this.level, this.worldPosition, state, data, list));
			if (!list.isEmpty()) {
				super.setChanged();
			}
		}
		return list;
	}

	public boolean releaseAnimal(
		Level level,
		BlockPos pos,
		BlockState state,
		AnimalComponent.StoredEntityData animal,
		@Nullable List<Entity> entities
	) {
		Direction direction = state.getValue(NautilusBlock.FACING);
		BlockPos blockPos = pos.relative(direction);
		boolean bl = !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty() ;
		if (bl) return false;
		if (animal.tickEnteredHive() == level.getGameTime()) return false;
		Entity entity = animal.loadEntity(level);
		if (entity != null) {
			if (entities != null) entities.add(entity);
			double d = 0.55 + entity.getBbWidth() / 2.0F;
			double e = pos.getX() + 0.5 + d * direction.getStepX();
			double g = pos.getY();
			double h = pos.getZ() + 0.5 + d * direction.getStepZ();
			entity.setYRot(direction.toYRot());
			entity.setYBodyRot(direction.toYRot());
			entity.setYHeadRot(direction.toYRot());
			entity.yRotO =direction.toYRot();
			entity.moveTo(e, g, h, direction.toYRot(), entity.getXRot());
			level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, level.getBlockState(pos)));
			return level.addFreshEntity(entity);
		} else return false;
	}

	public AnimalComponent getAnimalComponent() {
		return new AnimalComponent(List.copyOf(this.animal));
	}

	// shell holds exactly one animal, so an already-occupied shell keeps what it has rather than doubling up.
	// the animal comes back as a new entity/UUID - capture drops the old one to avoid two entities claiming it.
	public void restoreAnimal(AnimalComponent component) {
		if (!this.animal.isEmpty() || component.animal().isEmpty()) {
			return;
		}
		this.animal.addAll(component.animal());
		super.setChanged();
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.animal.clear();
		if (tag.contains("animal", Tag.TAG_LIST)) {
			AnimalComponent.StoredEntityData.LIST_CODEC.parse(NbtOps.INSTANCE, tag.get("animal"))
					.resultOrPartial(error -> LOGGER.error("nekomasfixed: unreadable nautilus occupant at {}: {}", this.worldPosition, error))
					.ifPresent(this.animal::addAll);
		}
	}

	// no key at all when empty, so copying this block entity onto a stack never stamps a leftover empty
	// list; a failed encode also writes nothing rather than half an animal, loading back empty instead.
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!this.animal.isEmpty()) {
			DataResult<Tag> encoded = AnimalComponent.StoredEntityData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.animal);
			encoded.error().ifPresent(error -> LOGGER.error("nekomasfixed: could not save the nautilus occupant at {}: {}", this.worldPosition, error.message()));
			encoded.result().ifPresent(value -> tag.put("animal", value));
		}
	}
}
