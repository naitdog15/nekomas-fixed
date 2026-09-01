package net.greenjab.nekomasfixed.registry.block.entity;

import com.google.common.collect.Lists;
import net.greenjab.nekomasfixed.registry.block.NautilusBlock;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NautilusBlockEntity extends BlockEntity {
	private final List<AnimalComponent.StoredEntityData> animal = Lists.newArrayList();

	public NautilusBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.NAUTILUS_BLOCK_ENTITY, pos, state);
	}

	public boolean hasAnimal() {
		return !this.animal.isEmpty();
	}

	public void tryEnterNautilus(Animal animal) {
		if (this.animal.isEmpty()) {
			animal.stopRiding();
			animal.ejectPassengers();
			animal.dropLeash();
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
			entity.snapTo(e, g, h, direction.toYRot(), entity.getXRot());
			level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, level.getBlockState(pos)));
			return level.addFreshEntity(entity);
		} else return false;
	}

	// ValueInput/ValueOutput and the applyImplicitComponents/
	// collectImplicitComponents/removeComponentsFromTag trio are all 1.21+ (the latter three exist
	// specifically to sync a block entity's state into/out of a 1.21+ ItemStack data component when
	// the block is mined/placed). On 1.20.1 that same "animal survives being mined and re-placed"
	// behaviour is already covered by plain load/saveAdditional: BlockEntity#saveToItem(ItemStack)
	// (uninherited, unmodified here) calls saveWithoutMetadata() -> saveAdditional() and stores the
	// result under the dropped stack's own BlockEntityTag, and NautilusBlock#playerWillDestroy
	// already builds the drop through that path - so one CompoundTag-based pair covers both the
	// world-save case and the item-carry case, and no separate component sync method is needed.
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.animal.clear();
		if (tag.contains("animal")) {
			AnimalComponent.StoredEntityData.LIST_CODEC.parse(NbtOps.INSTANCE, tag.get("animal"))
					.result().ifPresent(this.animal::addAll);
		}
	}

	// This is the exact "unguarded even when the list is empty" site
	// (26.2 source: NautilusBlockEntity.java:124's collectImplicitComponents) - an empty
	// AnimalComponent elided for free under the old component system, but an NBT facade writes an
	// empty compound unless guarded explicitly, which is a real on-disk/on-wire diff for every
	// nautilus block in every world. Guarded by the `if` below.
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!this.animal.isEmpty()) {
			AnimalComponent.StoredEntityData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.animal)
					.result().ifPresent(encoded -> tag.put("animal", encoded));
		}
	}
}
