package net.greenjab.nekomasfixed.registry.block;

import net.greenjab.nekomasfixed.registry.block.entity.TermitehiveBlockEntity;
import net.greenjab.nekomasfixed.registry.entity.Termite;
import net.greenjab.nekomasfixed.registry.other.TermitesComponent;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.util.StackData;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;

import java.util.List;

public class TermitehiveBlock extends BaseEntityBlock {
    public static IntegerProperty TERMITES = IntegerProperty.create("termites", 0, 2);
    /** BlockItem reads placement overrides out of this tag, so a picked hive keeps its count. */
    private static final String BLOCK_STATE_TAG = "BlockStateTag";

    public TermitehiveBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(TERMITES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TERMITES);
    }

    /** The hive is a plain baked model; the block entity only tracks the colony. */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;

        if (type == BlockEntityTypeRegistry.TERMITE_HIVE_BLOCK_ENTITY.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TermitehiveBlockEntity hive) {
                    TermitehiveBlockEntity.serverTick(level1, pos, state1, hive);
                    int current = state1.getValue(TERMITES);
                    int actual = hive.getTermiteCount();
                    if (current != actual) level1.setBlock(pos, state1.setValue(TERMITES, actual), 3);
                }
            };
        }
        return null;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level instanceof ServerLevel serverLevel
                && player.getAbilities().instabuild
                && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
                && level.getBlockEntity(pos) instanceof TermitehiveBlockEntity termitehiveBlockEntity) {
            boolean bl = !termitehiveBlockEntity.hasNoTermites();
            if (bl) {
                ItemStack itemStack = new ItemStack(this);
                StackData.writeTermites(itemStack, new TermitesComponent(termitehiveBlockEntity.createTermitesData()));
                ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getCloneItemStack(level, pos, state);
        itemStack.getOrCreateTagElement(BLOCK_STATE_TAG)
                .putString(TERMITES.getName(), Integer.toString(state.getValue(TERMITES)));
        return itemStack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof PrimedTnt
                || entity instanceof Creeper
                || entity instanceof WitherSkull
                || entity instanceof WitherBoss
                || entity instanceof MinecartTNT) {
            BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockEntity instanceof TermitehiveBlockEntity termitehiveBlockEntity) {
                termitehiveBlockEntity.angerTermites(TermitehiveBlockEntity.TermiteState.EMERGENCY);
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        if (level.getBlockState(neighborPos).getBlock() instanceof FireBlock && level.getBlockEntity(pos) instanceof TermitehiveBlockEntity termitehiveBlockEntity) {
            termitehiveBlockEntity.angerTermites(TermitehiveBlockEntity.TermiteState.EMERGENCY);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TermitehiveBlockEntity(pos, state);
    }

    /**
     * Puts a carried colony back when the hive is placed again. It has to go in one termite at a
     * time: marking the hive dirty is what makes it swarm, so loading a saved tag into it would
     * eject everything it had just been given. Only refills an empty hive, and only on the server -
     * the client learns the count from the termites property once the hive ticks.
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (level.isClientSide() || !StackData.contains(itemStack, StackData.KEY_TERMITES)) return;
        if (level.getBlockEntity(pos) instanceof TermitehiveBlockEntity termitehiveBlockEntity
                && termitehiveBlockEntity.hasNoTermites()) {
            StackData.readTermites(itemStack).termites().forEach(termitehiveBlockEntity::addTermite);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide() && blockEntity instanceof TermitehiveBlockEntity termitehiveBlockEntity) {
            // silk touch is what keeps the colony inside, same as a bee nest
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0) {
                termitehiveBlockEntity.angerTermites(TermitehiveBlockEntity.TermiteState.EMERGENCY);
                level.updateNeighbourForOutputSignal(pos, this);
                this.angerNearbyTermites(level, pos);
            }
            CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)player, state, tool, termitehiveBlockEntity.getTermiteCount());
        }
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        this.angerNearbyTermites(level, pos);
    }

    private void angerNearbyTermites(Level level, BlockPos pos) {
        AABB box = new AABB(pos).inflate(8.0, 6.0, 8.0);
        List<Termite> list = level.getEntitiesOfClass(Termite.class, box);
        if (!list.isEmpty()) {
            List<Player> list2 = level.getEntitiesOfClass(Player.class, box);
            if (list2.isEmpty())  return;
            for (Termite termite : list) {
                if (termite.getTarget() == null) {
                    Player playerEntity = Util.getRandom(list2, level.getRandom());
                    termite.setTarget(playerEntity);
                }
            }
        }
    }
}
