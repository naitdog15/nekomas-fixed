package net.greenjab.nekomasfixed.registry.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.util.EntityNbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import javax.annotation.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TermitehiveBlockEntity extends BlockEntity {
    static final Logger LOGGER = LogUtils.getLogger();
    /**
     * Entity NBT that means nothing once the termite is inside the hive, and would only bloat the
     * stored tag. Mirrors
     * {@link net.greenjab.nekomasfixed.registry.other.AnimalComponent#IRRELEVANT_ANIMAL_NBT_KEYS}
     * plus the two pollination counters.
     */
    static final List<String> IRRELEVANT_TERMITE_NBT_KEYS = Arrays.asList(
            "Air",
            "ArmorDropChances",
            "HandDropChances",
            "ArmorItems",
            "HandItems",
            "Brain",
            "CanPickUpLoot",
            "DeathTime",
            "FallDistance",
            "FallFlying",
            "Fire",
            "HurtByTimestamp",
            "HurtTime",
            "LeftHanded",
            "Motion",
            "NoGravity",
            "OnGround",
            "PortalCooldown",
            "Pos",
            "Rotation",
            "SleepingX",
            "SleepingY",
            "SleepingZ",
            "CannotEnterHiveTicks",
            "TicksSincePollination",
            "CropsGrownSincePollination",
            "HivePos",
            "Passengers",
            "Leash",
            "UUID"
    );
    private final List<TermitehiveBlockEntity.Termite> termites = Lists.newArrayList();

    public TermitehiveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.TERMITE_HIVE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setChanged() {
        this.angerTermites(TermitehiveBlockEntity.TermiteState.EMERGENCY);
        super.setChanged();
    }

    public boolean hasNoTermites() {
        return this.termites.isEmpty();
    }

    public boolean isFullOfTermites() {
        return this.termites.size() == 2;
    }

    public void angerTermites(TermitehiveBlockEntity.TermiteState termiteState) {
        List<Entity> list = Lists.newArrayList();
        this.termites.removeIf( termite -> releaseTermite(this.level, this.worldPosition, termite.createData(), list, termiteState));
        if (!list.isEmpty()) {
            super.setChanged();
        }
    }

    @VisibleForDebug
    public int getTermiteCount() {
        return this.termites.size();
    }


    public void tryEnterMound(net.greenjab.nekomasfixed.registry.entity.Termite entity) {
        if (this.termites.size() < 2) {
            entity.stopRiding();
            entity.ejectPassengers();
            entity.dropLeash(true, true);
            this.addTermite(TermitehiveBlockEntity.TermiteData.of(entity));
            if (this.level != null) {

                BlockPos blockPos = this.getBlockPos();
                this.level
                        .playSound(
                                null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F
                        );
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, this.getBlockState()));
            }

            entity.discard();
            super.setChanged();
        }
    }

    public void addTermite(TermitehiveBlockEntity.TermiteData termite) {
        this.termites.add(new TermitehiveBlockEntity.Termite(termite));
    }

    private static boolean releaseTermite(
            Level level,
            BlockPos pos,
            TermitehiveBlockEntity.TermiteData termite,
            @Nullable List<Entity> entities,
            TermitehiveBlockEntity.TermiteState termiteState
    ) {

        Direction direction = Direction.fromYRot(level.getRandom().nextInt(360));
        BlockPos blockPos = pos.relative(direction);
        boolean bl = !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty();
        if (bl && termiteState != TermitehiveBlockEntity.TermiteState.EMERGENCY) {
            return false;
        } else {
            Entity entity = termite.loadEntity(level);
            if (entity != null) {
                if (entity instanceof net.greenjab.nekomasfixed.registry.entity.Termite termiteEntity) {

                    if (entities != null) {
                        entities.add(termiteEntity);
                    }

                    float f = entity.getBbWidth();
                    double d = bl ? 0.0 : 0.55 + f / 2.0F;
                    double e = pos.getX() + 0.5 + d * direction.getStepX();
                    double g = pos.getY() + 0.5 - entity.getBbHeight() / 2.0F;
                    double h = pos.getZ() + 0.5 + d * direction.getStepZ();
                    entity.moveTo(e, g, h, entity.getYRot(), entity.getXRot());
                }

                level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, level.getBlockState(pos)));
                return level.addFreshEntity(entity);
            } else {
                return false;
            }
        }
    }

    private static void tickTermites(Level level, BlockPos pos, BlockState state, List<TermitehiveBlockEntity.Termite> termites) {
        boolean bl = false;
        Iterator<TermitehiveBlockEntity.Termite> iterator = termites.iterator();

        while (iterator.hasNext()) {
            TermitehiveBlockEntity.Termite termite = iterator.next();
            if (termite.canExitHive()) {
                TermitehiveBlockEntity.TermiteState termiteState = TermitehiveBlockEntity.TermiteState.TERMITE_RELEASED;
                if (releaseTermite(level, pos, termite.createData(), null, termiteState)) {
                    bl = true;
                    iterator.remove();
                }
            }
        }

        if (bl) {
            setChanged(level, pos, state);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TermitehiveBlockEntity blockEntity) {
        tickTermites(level, pos, state, blockEntity.termites);
        if (!blockEntity.termites.isEmpty() && level.getRandom().nextDouble() < 0.005) {
            double d = pos.getX() + 0.5;
            double e = pos.getY();
            double f = pos.getZ() + 0.5;
            level.playSound(null, d, e, f, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.termites.clear();
        if (tag.contains("termites")) {
            TermitehiveBlockEntity.TermiteData.LIST_CODEC.parse(NbtOps.INSTANCE, tag.get("termites"))
                    .result().ifPresent(list -> list.forEach(this::addTermite));
        }
    }

    // No key at all when the hive is empty, so nothing copying this block entity onto a stack
    // stamps it with a leftover empty list.
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        List<TermitehiveBlockEntity.TermiteData> data = this.createTermitesData();
        if (!data.isEmpty()) {
            TermitehiveBlockEntity.TermiteData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, data)
                    .result().ifPresent(encoded -> tag.put("termites", encoded));
        }
    }

    /** What a dropped or picked hive carries its occupants in. */
    public List<TermitehiveBlockEntity.TermiteData> createTermitesData() {
        return this.termites.stream().map(TermitehiveBlockEntity.Termite::createData).toList();
    }

   /* @Override
    public void registerTracking(ServerWorld level, DebugTrackable.Tracker tracker) {
        tracker.track(DebugSubscriptionTypes.TERMITE_HIVES, () -> TermiteHiveDebugData.fromTermitehive(this));
    }*/

    static class Termite {
        private final TermitehiveBlockEntity.TermiteData data;
        private int ticksInHive;

        Termite(TermitehiveBlockEntity.TermiteData data) {
            this.data = data;
            this.ticksInHive = data.ticksInHive();
        }

        public boolean canExitHive() {
            return this.ticksInHive++ > this.data.minTicksInHive;
        }

        public TermitehiveBlockEntity.TermiteData createData() {
            return new TermitehiveBlockEntity.TermiteData(this.data.entityData, this.ticksInHive, this.data.minTicksInHive);
        }

    }

    /**
     * One termite parked in the hive. The entity is kept as a plain {@link CompoundTag} that
     * carries its own {@code "id"} key, written and read back through {@link EntityNbtHelper} -
     * the same shape {@code AnimalComponent.StoredEntityData} uses.
     */
    public record TermiteData(CompoundTag entityData, int ticksInHive, int minTicksInHive) {
        public static final Codec<TermitehiveBlockEntity.TermiteData> CODEC = RecordCodecBuilder.create(
                 instance -> instance.group(
                                CompoundTag.CODEC.fieldOf("entity_data").forGetter(TermitehiveBlockEntity.TermiteData::entityData),
                                Codec.INT.fieldOf("ticks_in_hive").forGetter(TermitehiveBlockEntity.TermiteData::ticksInHive),
                                Codec.INT.fieldOf("min_ticks_in_hive").forGetter(TermitehiveBlockEntity.TermiteData::minTicksInHive)
                        )
                        .apply(instance, TermitehiveBlockEntity.TermiteData::new)
        );
        public static final Codec<List<TermitehiveBlockEntity.TermiteData>> LIST_CODEC = CODEC.listOf();

        public static TermitehiveBlockEntity.TermiteData of(Entity entity) {
            CompoundTag tag = EntityNbtHelper.store(entity, Set.copyOf(TermitehiveBlockEntity.IRRELEVANT_TERMITE_NBT_KEYS));
            boolean hasNectar = tag.getBoolean("HasNectar");
            return new TermitehiveBlockEntity.TermiteData(tag, 0, hasNectar ? 2400 : 600);
        }

        public static TermitehiveBlockEntity.TermiteData create(int ticksInHive) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", EntityType.getKey(EntityTypeRegistry.TERMITE.get()).toString());
            return new TermitehiveBlockEntity.TermiteData(tag, ticksInHive, 600);
        }

        @Nullable
        public Entity loadEntity(Level level) {
            Entity entity = EntityNbtHelper.load(this.entityData.copy(), level);
            if (entity != null && entity.getType() == EntityTypeRegistry.TERMITE.get()) {
                return entity;
            } else {
                return null;
            }
        }
    }

    public enum TermiteState {
        TERMITE_RELEASED,
        EMERGENCY
    }
}