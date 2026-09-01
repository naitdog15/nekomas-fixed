package net.greenjab.nekomasfixed.registry.block;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registry.block.entity.ClamBlockEntity;
import net.greenjab.nekomasfixed.registry.block.enums.ClamType;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.registry.registries.LootTableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ClamBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<ClamBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
				ClamType.CODEC.fieldOf("clam_type").forGetter(ClamBlock::getClamType),
				propertiesCodec()
			).apply(instance, ClamBlock::new)
	);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION;

	public static final ResourceLocation CONTENTS_DYNAMIC_DROP_ID = NekomasFixed.id("clam_contents");
	private final ClamType clamType;

	@Override
	public MapCodec<? extends ClamBlock> codec() {
		return CODEC;
	}

	public ClamBlock(ClamType clamType, Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(OPEN, false).setValue(POWERED, false));
		this.clamType = clamType;
	}

	@Override
	protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	private void tryLaunch(BlockState state, Level level, BlockPos pos) {
		boolean wasPowered = state.getValue(POWERED);
		boolean isPowered = level.hasNeighborSignal(pos);
		if (wasPowered != isPowered) {
			if (isPowered && !state.getValue(OPEN)) {
				List<Entity> entities = level.getEntities(null, new AABB(pos));
				for (Entity entity : entities) {
					if (entity instanceof LivingEntity || entity instanceof ItemEntity) {
						float power = level.getBestNeighborSignal(pos);
						power = (float) (Math.sqrt(power) / 4.0f);
						float dirx = -state.getValue(ClamBlock.FACING).getStepX();
						float dirz = -state.getValue(ClamBlock.FACING).getStepZ();
						if (entity instanceof ItemEntity) {
							dirx*=0.5f;
							dirz*=0.5f;
						}

						if (entity instanceof ServerPlayer serverPlayerEntity) {
							serverPlayerEntity.connection.send(new ClientboundSetEntityMotionPacket(serverPlayerEntity.getId(), new Vec3(power * dirx, power, power * dirz)));
						} else {
							entity.setDeltaMovement(power * dirx, power, power * dirz);
							entity.needsSync = true;
						}
					}
				}
			}
			level.setBlock(pos, state.setValue(POWERED, isPowered).setValue(OPEN, isPowered), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!level.isClientSide()) tryLaunch(state, level, pos);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
		if (!level.isClientSide()) tryLaunch(state, level, pos);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.is(state.getBlock())) {
			if (!level.isClientSide() && level.getBlockEntity(pos) == null) tryLaunch(state, level, pos);
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES_BY_DIRECTION.get((state.getValue(FACING)));
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction direction = ctx.getHorizontalDirection().getOpposite();
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(OPEN, false).setValue(POWERED, false);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
		Containers.updateNeighboursAfterDestroy(state, level, pos);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ClamBlockEntity clamBlockEntity && !hand.equals(InteractionHand.OFF_HAND)) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			} else {
				if (!(Boolean)state.getValue(OPEN) || player.isShiftKeyDown()) {
					BlockState blockState = state.cycle(OPEN);
					level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
					level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
					return InteractionResult.SUCCESS;
				}
				Inventory playerInventory = player.getInventory();
					boolean bl = swapSingleStack(stack, player, clamBlockEntity, playerInventory);
					if (bl) {
						this.playSound(level, pos, stack.isEmpty() ? SoundEvents.SHELF_TAKE_ITEM : SoundEvents.SHELF_SINGLE_SWAP);
					} else {
						if (stack.isEmpty()) {
							BlockState blockState = state.cycle(OPEN);
							level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
							level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
							return InteractionResult.SUCCESS;
						}

						this.playSound(level, pos, SoundEvents.SHELF_PLACE_ITEM);
					}
					return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
			}
		} else {
			return InteractionResult.PASS;
		}
	}
	private static boolean swapSingleStack(ItemStack stack, Player player, ClamBlockEntity clamBlockEntity, Inventory playerInventory) {
		if (stack.is(ItemTags.SHULKER_BOXES)) return false;
		ItemStack itemStack = clamBlockEntity.swapStack(0, stack);
		ItemStack itemStack2 = player.hasInfiniteMaterials() && itemStack.isEmpty() ? stack.copy() : itemStack;
		playerInventory.setItem(playerInventory.getSelectedSlot(), itemStack2);
		playerInventory.setChanged();
		clamBlockEntity.markDirty(GameEvent.ITEM_INTERACT_FINISH);
		return !itemStack.isEmpty();
	}
	private void playSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			BlockState blockState = state.cycle(OPEN);
			level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
		}
		return InteractionResult.SUCCESS;
	}

	public static PropertyRetriever< Float2FloatFunction> getAnimationProgressRetriever(LidBlockEntity progress) {
		return () -> progress::getOpenNess;
	}

	public interface PropertyRetriever<T> {
		T getFallback();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ClamBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? createTickerHelper(type, BlockEntityTypeRegistry.CLAM_BLOCK_ENTITY, ClamBlockEntity::clientTick) : null;
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ClamBlockEntity clamBlockEntity) {
			ItemStack item = clamBlockEntity.getItems().get(0);
			BlockState below = level.getBlockState(pos.below());
			if (below.is(Blocks.SAND) || below.is(Blocks.GRAVEL) || below.is(Blocks.DIRT)) {
				if (state.getValue(OPEN)) {
					if (item.isEmpty()) {
						clamBlockEntity.setHeldStack(below.getBlock().asItem().getDefaultInstance());
					} else {
						if (item.is(below.getBlock().asItem())) {
							clamBlockEntity.setHeldStack(item.copyWithCount(Math.min(item.getCount() + 1, item.getMaxStackSize())));
						}
					}
					if (!state.getValue(POWERED) && random.nextInt(Math.max(64 - item.getCount(),1)) < 4) {
						BlockState blockState = state.cycle(OPEN);
						level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
					}
				} else {
					if (item.is(Items.SAND) || item.is(Items.GRAVEL) || item.is(Items.DIRT)) {
						clamBlockEntity.setHeldStack(item.copyWithCount(item.getCount() - 1));
						if (random.nextInt(16) == 0) {
                            LootTable lootTable = level.getServer()
									.reloadableRegistries()
									.getLootTable(LootTableRegistry.CLAM_LOOT_TABLE);

							LootParams lootContextParameterSet = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, null).withParameter(LootContextParams.THIS_ENTITY, null).withLuck(getLuck(this.getClamType())).create(LootContextParamSets.FISHING);

							ObjectArrayList<ItemStack> loots = lootTable.getRandomItems(lootContextParameterSet);
							if (!loots.isEmpty()) {
								ItemStack itemStack = clamBlockEntity.getItems().get(0);
								ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
								itemEntity.setDefaultPickUpDelay();
								level.addFreshEntity(itemEntity);

								clamBlockEntity.setHeldStack(loots.get(0));
							}
						}
					}
					if (!state.getValue(POWERED) && random.nextInt(item.getCount() + 1) < 4) {
						BlockState blockState = state.cycle(OPEN);
						level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
					}
				}
			}
		}
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, OPEN, POWERED);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ClamBlockEntity clamBlockEntity) {
			int cstate = state.getValueOrElse(ClamBlock.OPEN, false)?1:0;
			if (cstate==1 && !clamBlockEntity.getItems().isEmpty() && !clamBlockEntity.getItems().get(0).isEmpty()) cstate++;
			clamBlockEntity.setState(cstate);
			if (!level.isClientSide() && player.preventsBlockDrops()) {
				ItemStack itemStack = getItemStack(this.getClamType());
				itemStack.applyComponents(blockEntity.collectComponents());
				ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity(itemEntity);
			} else {
				clamBlockEntity.unpackLootTable(player);
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof ClamBlockEntity clamBlockEntity) {
			builder = builder.withDynamicDrop(CONTENTS_DYNAMIC_DROP_ID, lootConsumer -> {
				for (int i = 0; i < clamBlockEntity.getContainerSize(); i++) {
					lootConsumer.accept(clamBlockEntity.getItem(i));
				}
			});
		}


		return super.getDrops(state, builder);
	}

	public static ItemStack getItemStack(@Nullable ClamType clamType) {
		return new ItemStack(get(clamType));
	}

	public static Block get(@Nullable ClamType clamType) {
		if (clamType == null) {
			return BlockRegistry.CLAM;
		} else {
			return switch (clamType) {
                case REGULAR -> BlockRegistry.CLAM;
                case BLUE -> BlockRegistry.CLAM_BLUE;
				case PINK -> BlockRegistry.CLAM_PINK;
				case PURPLE -> BlockRegistry.CLAM_PURPLE;
			};
		}
	}

	public static int getLuck(@Nullable ClamType clamType) {
		if (clamType == null) {
			return 0;
		} else {
			return switch (clamType) {
				case REGULAR -> 0;
				case BLUE -> 1;
				case PINK -> 2;
				case PURPLE -> 3;
			};
		}
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}

	public ClamType getClamType() {
		return this.clamType;
	}

	static {
		SHAPES_BY_DIRECTION = Shapes.rotateHorizontal(Block.box(1.0, 0, 0, 15.0, 4.0, 15.0));
	}
}
