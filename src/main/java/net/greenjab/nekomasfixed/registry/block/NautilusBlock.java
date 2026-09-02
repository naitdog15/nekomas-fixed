package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.registry.block.entity.NautilusBlockEntity;
import net.greenjab.nekomasfixed.registry.block.enums.NautilusBlockType;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NautilusBlock extends BaseEntityBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
	/** BlockItem reads placement overrides out of this tag, so the shell keeps its guest. */
	private static final String BLOCK_STATE_TAG = "BlockStateTag";
	private final NautilusBlockType nautilusBlockType;

	public NautilusBlock(NautilusBlockType nautilusBlockType, Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH));
		this.nautilusBlockType = nautilusBlockType;
	}

	/** The shell is a plain baked model; the block entity only holds the passenger. */
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(OCCUPIED)?15:0;
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		boolean occupied = hasAnimal(level, pos);
		if (level instanceof ServerLevel serverLevel) {
			if (occupied) {
				if (level.getBlockEntity(pos) instanceof NautilusBlockEntity nautilusBlockEntity) {
					List<Entity > list = nautilusBlockEntity.tryReleaseAnimal(state);
					if (!list.isEmpty()) {
						level.setBlockAndUpdate(pos, state.setValue(NautilusBlock.OCCUPIED, false));
						if (stack.is(Items.LEAD)) {
							if (list.get(0) instanceof Mob mob) {
								mob.setLeashedTo(player, true);
								stack.shrink(1);
							}
						}
					}
				}
			} else {
				List<Entity> list = serverLevel.getEntities(player, player.getBoundingBox().inflate(10));
				for (Entity entity : list) {
					if (!player.isSecondaryUseActive()
							&& entity instanceof Mob mob
							&& mob.canBeLeashed(player)
							&& entity.isAlive()
							&& entity.distanceToSqr(Vec3.atCenterOf(pos))<10) {
						AABB area = AABB.ofSize(entity.getBoundingBox().getCenter(), 32.0, 32.0, 32.0);
						List<Mob> list2 = serverLevel.getEntitiesOfClass(Mob.class, area, other -> other.getLeashHolder() == player);
						for (Mob entity2 : list2) {
							if (entity2 instanceof Animal animalEntity) {
								if (level.getBlockEntity(pos) instanceof NautilusBlockEntity nautilusBlockEntity) {
									if (animalEntity.getBoundingBox().getXsize()<1 &&
										animalEntity.getBoundingBox().getYsize()<1.5f) {
										nautilusBlockEntity.tryEnterNautilus(animalEntity);
										level.setBlockAndUpdate(pos, state.setValue(NautilusBlock.OCCUPIED, true));
										return InteractionResult.SUCCESS;
									}
								}
							}
						}
					}
				}
			}
		} else {
			return InteractionResult.SUCCESS;
		}

		return super.use(state, level, pos, player, hand, hit);
	}

	private boolean hasAnimal(Level level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof NautilusBlockEntity NautilusBlockEntity && NautilusBlockEntity.hasAnimal();
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(OCCUPIED, FACING);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NautilusBlockEntity(pos, state);
	}

	/**
	 * Puts a captured passenger back when the shell is placed again. The occupied property rides
	 * over in the placement tag, so without this the shell would show as occupied and be empty.
	 * The stored blob goes straight back into the block entity - same bytes it was taken as - and
	 * only on the server, since the client is told about the guest through the occupied property.
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(level, pos, state, placer, itemStack);
		if (level.isClientSide() || !StackData.contains(itemStack, StackData.KEY_ANIMAL)) return;
		if (level.getBlockEntity(pos) instanceof NautilusBlockEntity nautilusBlockEntity) {
			nautilusBlockEntity.restoreAnimal(StackData.readAnimal(itemStack));
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (level instanceof ServerLevel serverLevel
			&& player.getAbilities().instabuild
			&& serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
			&& level.getBlockEntity(pos) instanceof NautilusBlockEntity NautilusBlockEntity) {
			boolean occupied = state.getValue(OCCUPIED);
			boolean bl = NautilusBlockEntity.hasAnimal();
			if (bl || occupied) {
				ItemStack itemStack = getItemStack(this.getNautilusBlockType());
				StackData.writeAnimal(itemStack, NautilusBlockEntity.getAnimalComponent());
				setOccupiedTag(itemStack, occupied);
				ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity(itemEntity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	private static void setOccupiedTag(ItemStack stack, boolean occupied) {
		stack.getOrCreateTagElement(BLOCK_STATE_TAG).putString(OCCUPIED.getName(), Boolean.toString(occupied));
	}

	public static ItemStack getItemStack(@Nullable NautilusBlockType nautilusBlockType) {
		return new ItemStack(get(nautilusBlockType));
	}

	public static Block get(@Nullable NautilusBlockType nautilusBlockType) {
		if (nautilusBlockType == null) {
			return BlockRegistry.NAUTILUS_BLOCK.get();
		} else {
			return switch (nautilusBlockType) {
				case REGULAR -> BlockRegistry.NAUTILUS_BLOCK.get();
				case ZOMBIE -> BlockRegistry.ZOMBIE_NAUTILUS_BLOCK.get();
				case CORAL -> BlockRegistry.CORAL_NAUTILUS_BLOCK.get();
			};
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		ItemStack itemStack = super.getCloneItemStack(level, pos, state);
		setOccupiedTag(itemStack, state.getValue(OCCUPIED));
		return itemStack;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public NautilusBlockType getNautilusBlockType() {
		return this.nautilusBlockType;
	}
}
