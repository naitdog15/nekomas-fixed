package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.Registries;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.block.entity.TermitehiveBlockEntity;
import net.greenjab.nekomasfixed.registry.block.enums.HollowLogType;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.registry.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Termite extends Monster {
    public final AnimationState swipeAnimationState = new AnimationState();
    public final AnimationState chewAnimationState = new AnimationState();
    private static final EntityDataAccessor<Termite.State> STATE;
    private static final EntityDataAccessor<Boolean> LADEN;
    private static final EntityDataAccessor<Optional<BlockPos>> CHEW_TARGET;
    /** Length of the swipe animation, and how long the SWIPING state lasts. */
    private static final int SWIPE_TICKS = 20;
    private int swipeStartTick = Integer.MIN_VALUE;

    // Server-side bookkeeping. None of it is synched - the client has no goals to run and reads only
    // the synched state and target.
    private int hungerCooldown;
    private int ladenTicks;
    private int chewProgress;
    private boolean hurtSinceChewStart;
    private int nextSearchTick;
    /** Logs and hives nothing could path to, held with the game time they become worth trying again. */
    private final Map<BlockPos, Long> unreachableTargets = new LinkedHashMap<>();

    public Termite(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        // Step height and safe fall distance are set via setMaxUpStep(float) / an overridden
        // getMaxFallDistance() rather than attributes.
        this.setMaxUpStep(1.0F);
        // Stagger the first block search so a mound's worth of termites do not all scan on one tick.
        this.nextSearchTick = this.random.nextInt(40);
    }

    @Override
    public int getMaxFallDistance() {
        return 2;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EnterMoundGoal(this));
        this.goalSelector.addGoal(2, new ReturnLadenGoal(this));
        this.goalSelector.addGoal(2, new ChewLogGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.4d));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.6F, false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, State.IDLING);
        this.entityData.define(LADEN, false);
        this.entityData.define(CHEW_TARGET, Optional.empty());
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2d)
                .add(Attributes.ATTACK_SPEED, 1.6d)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2d)
                .add(Attributes.MOVEMENT_SPEED, 0.4d);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.setState(State.SWIPING);
        return super.doHurtTarget(target);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    private void setState(State state) {
        this.entityData.set(STATE, state);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (STATE.equals(data)) {
            this.swipeAnimationState.stop();
            this.chewAnimationState.stop();
            switch (this.entityData.get(STATE)) {
                case SWIPING -> {
                    this.swipeAnimationState.start(this.tickCount);
                    this.swipeStartTick = this.tickCount;
                }
                // chewAnimationState was just stopped above, so this restarts the loop from frame
                // zero every time CHEWING is (re)published - the client re-runs this handler on
                // every incoming value for STATE with no equality check. startIfStopped over start
                // just keeps the call idempotent; it doesn't preserve loop phase here.
                case CHEWING -> this.chewAnimationState.startIfStopped(this.tickCount);
                case IDLING -> {
                }
            }
        }
        super.onSyncedDataUpdated(data);
    }

    boolean canEnterMound() {
        return this.getTarget() == null && this.level().isNight();
    }

    public boolean isChewing() {
        return this.entityData.get(STATE) == State.CHEWING;
    }

    public boolean isLaden() {
        return this.entityData.get(LADEN);
    }

    public Optional<BlockPos> getChewTarget() {
        return this.entityData.get(CHEW_TARGET);
    }

    /** How far through the current chew the termite is. Server side only - the counter is not synched. */
    public float getChewProgress() {
        return (float) this.chewProgress / NekomasFixedConfig.TERMITE_CHEW_TICKS.get();
    }

    public void setLaden(boolean laden) {
        this.entityData.set(LADEN, laden);
    }

    public void resetHunger() {
        this.hungerCooldown = 1200 + this.random.nextInt(1200);
        this.ladenTicks = 0;
    }

    /** One shared timer for all three block searches - only one of them can be looking at a time. */
    boolean searchTimerElapsed() {
        return this.tickCount >= this.nextSearchTick;
    }

    void delaySearch() {
        this.nextSearchTick = this.tickCount + 40 + this.random.nextInt(41);
    }

    void markTargetUnreachable(BlockPos pos) {
        this.unreachableTargets.entrySet().removeIf(entry -> entry.getValue() <= this.level().getGameTime());
        // Bounded on purpose: this is a hint, not a record, and it must not grow with the world.
        if (this.unreachableTargets.size() >= 8) {
            Iterator<BlockPos> iterator = this.unreachableTargets.keySet().iterator();
            iterator.next();
            iterator.remove();
        }
        this.unreachableTargets.put(pos.immutable(), this.level().getGameTime() + 1200L);
    }

    boolean isTargetUnreachable(BlockPos pos) {
        Long expiry = this.unreachableTargets.get(pos);
        if (expiry == null) {
            return false;
        }
        if (expiry <= this.level().getGameTime()) {
            this.unreachableTargets.remove(pos);
            return false;
        }
        return true;
    }

    /** A hive worth walking to: loaded, still a hive with a live block entity, and with room inside. */
    boolean isUsableHive(BlockPos pos) {
        Level level = this.level();
        if (!level.isLoaded(pos) || !level.getBlockState(pos).is(BlockRegistry.TERMITE_HIVE.get())) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof TermitehiveBlockEntity hive && !hive.isFullOfTermites();
    }

    BlockPos findUsableHive() {
        return BlockPos.findClosestMatch(this.blockPosition(), 16, 8,
                        pos -> !this.isTargetUnreachable(pos) && this.isUsableHive(pos))
                .map(BlockPos::immutable)
                .orElse(null);
    }

    /**
     * Cells a termite can stand in to work on a block: the four sides at its own height, the four a
     * step below for a block whose neighbours are cut away, and for a block it can climb, the top.
     */
    static Set<BlockPos> approachSpots(Level level, BlockPos block, boolean includeTop) {
        Set<BlockPos> spots = new HashSet<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = block.relative(direction);
            if (isStandable(level, side)) {
                spots.add(side);
            }
            BlockPos lower = side.below();
            if (isStandable(level, lower)) {
                spots.add(lower);
            }
        }
        if (includeTop && isStandable(level, block.above())) {
            spots.add(block.above());
        }
        return spots;
    }

    /**
     * The termite pathfinds as a 1x1x1 mob, so one clear block with a sturdy face under it is a legal
     * standing cell. The sturdy floor also stops navigation quietly relocating the spot: it walks an
     * air destination down to whatever is below it.
     */
    static boolean isStandable(Level level, BlockPos spot) {
        BlockPos below = spot.below();
        if (!level.isLoaded(spot) || !level.isLoaded(below)) {
            return false;
        }
        if (!level.getBlockState(spot).getCollisionShape(level, spot).isEmpty()) {
            return false;
        }
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    /**
     * Paths to a cell beside the block, never at the block: navigation retargets a solid destination
     * to the first open space above it, which sends a termite up a whole trunk or mound dome.
     */
    boolean pathBeside(BlockPos block, boolean includeTop, double speed) {
        Set<BlockPos> spots = approachSpots(this.level(), block, includeTop);
        if (spots.isEmpty()) {
            return false;
        }
        Path path = this.getNavigation().createPath(spots, 0);
        return path != null && this.getNavigation().moveTo(path, speed);
    }

    /** Starts a chew: the target is synched before the state so the client never sees CHEWING without one. */
    public void beginChew(BlockPos pos) {
        this.hurtSinceChewStart = false;
        this.chewProgress = 0;
        this.entityData.set(CHEW_TARGET, Optional.of(pos));
        this.setState(State.CHEWING);
    }

    /** Idempotent - safe to call whether or not a chew is actually in progress. */
    public void clearChew() {
        this.getNavigation().stop();
        this.chewProgress = 0;
        this.entityData.set(CHEW_TARGET, Optional.empty());
        if (this.isChewing()) {
            this.setState(State.IDLING);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        boolean hurt = super.hurt(damageSource, amount);
        if (hurt && this.isChewing()) {
            this.hurtSinceChewStart = true;
            this.clearChew();
        }
        return hurt;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isChewing() || this.isLaden();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Laden", this.isLaden());
        tag.putInt("HungerCooldown", this.hungerCooldown);
        tag.putInt("LadenTicks", this.ladenTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setLaden(tag.getBoolean("Laden"));
        this.hungerCooldown = tag.getInt("HungerCooldown");
        this.ladenTicks = tag.getInt("LadenTicks");
    }

    // 1.20.1's AnimationState only accumulates time from the renderer, so the swipe is timed off the
    // entity's own tick counter instead - the server has to end the state too.
    @Override
    public void tick() {
        super.tick();
        if (this.entityData.get(STATE) == State.SWIPING) {
            if (this.tickCount - this.swipeStartTick > SWIPE_TICKS) {
                this.setState(State.IDLING);
            }
        }
        if (this.level().isClientSide()) {
            TermiteChewEffects.clientTick(this);
        } else {
            if (this.hungerCooldown > 0) {
                this.hungerCooldown--;
            }
            if (this.isLaden()) {
                this.ladenTicks++;
                // Two minutes carrying wood with no mound to unload at: the termite drops the load
                // quietly and waits out the usual cooldown rather than wandering laden forever.
                if (this.ladenTicks > 2400) {
                    this.setLaden(false);
                    this.resetHunger();
                }
            }
        }
    }

    static {
        STATE = SynchedEntityData.defineId(Termite.class, Registries.TERMITE_STATE.get());
        LADEN = SynchedEntityData.defineId(Termite.class, EntityDataSerializers.BOOLEAN);
        CHEW_TARGET = SynchedEntityData.defineId(Termite.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    }

    // One goal for going home: the pair this replaces held no flags between them, so a strolling
    // termite kept re-pathing on top of them, and both scanned the world before checking the time.
    private static class EnterMoundGoal extends Goal {
        private static final double SPEED = 0.4;
        private final Termite termite;
        private BlockPos hivePos;
        private int travelTicks;

        EnterMoundGoal(Termite termite) {
            this.termite = termite;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            // Cheap checks first: the hive search reads thousands of block states.
            if (!this.termite.canEnterMound() || !this.termite.searchTimerElapsed()) {
                return false;
            }
            this.termite.delaySearch();
            this.hivePos = this.termite.findUsableHive();
            return this.hivePos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.hivePos != null
                    && this.travelTicks < 600
                    && this.termite.canEnterMound()
                    && this.termite.isUsableHive(this.hivePos);
        }

        @Override
        public void start() {
            this.travelTicks = 0;
            if (approachSpots(this.termite.level(), this.hivePos, true).isEmpty()) {
                // Nowhere to stand beside this hive, so walking to it can never work - drop it now
                // instead of holding MOVE, LOOK and JUMP for the whole travel window over a mound
                // the termite cannot get to. A path that merely fails to build is left to the
                // once-a-second retry in tick(): navigation refuses to plan at all in mid-air.
                this.termite.markTargetUnreachable(this.hivePos);
                this.hivePos = null;
                return;
            }
            this.termite.pathBeside(this.hivePos, true, SPEED);
        }

        @Override
        public void stop() {
            this.hivePos = null;
            this.travelTicks = 0;
            this.termite.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.hivePos == null) {
                return;
            }
            this.travelTicks++;
            this.termite.getLookControl().setLookAt(Vec3.atCenterOf(this.hivePos));
            if (this.termite.distanceToSqr(Vec3.atCenterOf(this.hivePos)) <= 4.0) {
                if (this.termite.level().getBlockEntity(this.hivePos) instanceof TermitehiveBlockEntity hive) {
                    hive.tryEnterMound(this.termite);
                }
                return;
            }
            if (this.travelTicks % 20 == 0 && this.termite.getNavigation().isDone()) {
                this.termite.pathBeside(this.hivePos, true, SPEED);
            }
        }
    }

    // A termite that has hollowed a log carries the wood to the nearest mound with room in it. The
    // hive is checked again every tick because it can be broken or filled while the walk is going on.
    private static class ReturnLadenGoal extends Goal {
        private static final double SPEED = 0.4;
        private final Termite termite;
        private BlockPos hivePos;
        private Path plannedPath;
        private int repathAttempts;

        ReturnLadenGoal(Termite termite) {
            this.termite = termite;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (!this.termite.isLaden() || this.termite.getTarget() != null || this.termite.level().isNight()) {
                return false;
            }
            if (!this.termite.searchTimerElapsed()) {
                return false;
            }
            this.termite.delaySearch();
            return this.selectHive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.hivePos != null
                    && this.termite.isLaden()
                    && this.termite.getTarget() == null
                    && !this.termite.level().isNight()
                    && this.termite.isUsableHive(this.hivePos);
        }

        @Override
        public void start() {
            this.repathAttempts = 0;
            if (this.plannedPath != null) {
                this.termite.getNavigation().moveTo(this.plannedPath, SPEED);
                this.plannedPath = null;
            }
        }

        @Override
        public void stop() {
            this.hivePos = null;
            this.plannedPath = null;
            this.repathAttempts = 0;
            this.termite.getNavigation().stop();
        }

        @Override
        public void tick() {
            // The selector only re-polls canContinueToUse every other tick, so the full set of
            // conditions is re-tested here rather than trusted in between.
            if (this.hivePos == null || !this.termite.isLaden() || this.termite.getTarget() != null
                    || this.termite.level().isNight() || !this.termite.isUsableHive(this.hivePos)) {
                return;
            }
            this.termite.getLookControl().setLookAt(Vec3.atCenterOf(this.hivePos));
            double distanceSqr = this.termite.distanceToSqr(Vec3.atCenterOf(this.hivePos));
            boolean navigationDone = this.termite.getNavigation().isDone();
            // Hives sit anywhere on the mound's shell, several blocks up, so a walk that has run out
            // of path close to the dome counts as arrived.
            if (distanceSqr <= 6.25 || (navigationDone && distanceSqr <= 16.0)) {
                this.deposit();
                return;
            }
            if (navigationDone && (this.repathAttempts++ >= 3
                    || !this.termite.pathBeside(this.hivePos, true, SPEED))) {
                this.termite.markTargetUnreachable(this.hivePos);
                this.hivePos = null;
            }
        }

        private boolean selectHive() {
            BlockPos hive = this.termite.findUsableHive();
            if (hive == null) {
                return false;
            }
            Set<BlockPos> spots = Termite.approachSpots(this.termite.level(), hive, true);
            if (spots.isEmpty()) {
                this.termite.markTargetUnreachable(hive);
                return false;
            }
            // A best-effort partial path still comes back non-null, so ask whether it actually arrives.
            Path path = this.termite.getNavigation().createPath(spots, 0);
            if (path == null || !path.canReach()) {
                this.termite.markTargetUnreachable(hive);
                return false;
            }
            this.hivePos = hive;
            this.plannedPath = path;
            return true;
        }

        private void deposit() {
            this.termite.getNavigation().stop();
            if (this.termite.level().getBlockEntity(this.hivePos) instanceof TermitehiveBlockEntity hive) {
                hive.onTermiteDeposit(this.termite);
            }
            this.termite.setLaden(false);
            this.termite.resetHunger();
            // Cleared here so the tick that runs before the selector drops this goal does nothing.
            this.hivePos = null;
        }
    }

    // Walk to a spot beside a hollowable log, turn to face it, and gnaw for the configured time.
    // Everything that can go wrong mid-chew - the log changing, the termite being shoved, night
    // falling, the config or the griefing rule going off - is re-tested every tick, so a chew either
    // completes on a log the termite is still standing at or leaves no trace at all.
    private static class ChewLogGoal extends Goal {
        private static final double SPEED = 0.4;
        /** How long the walk to the log may take before the termite gives up on it. */
        private static final int APPROACH_LIMIT = 300;
        private final Termite termite;
        private BlockPos logPos;
        private BlockPos standPos;
        private BlockState expectedState;
        private Path plannedPath;
        private int approachTicks;
        private int repathAttempts;

        ChewLogGoal(Termite termite) {
            this.termite = termite;
            // JUMP as well as MOVE and LOOK: FloatGoal holds only JUMP, and without it a chewing
            // termite pushed into water would keep chewing while FloatGoal bobbed it.
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            Termite mob = this.termite;
            Level level = mob.level();
            if (!NekomasFixedConfig.TERMITES_EAT_LOGS.get()
                    || !ForgeEventFactory.getMobGriefingEvent(level, mob)) {
                return false;
            }
            // isNight rather than isDay: isDay is false in a thunderstorm and in every fixed-time
            // dimension, which would put the nether wood this mod maps out of reach for good.
            if (mob.isLaden() || mob.getTarget() != null || level.isNight() || mob.hungerCooldown > 0) {
                return false;
            }
            if (!mob.searchTimerElapsed()) {
                return false;
            }
            mob.delaySearch();
            return this.findLog();
        }

        @Override
        public boolean canContinueToUse() {
            Termite mob = this.termite;
            if (this.logPos == null || this.standPos == null) {
                return false;
            }
            if (mob.isLaden() || mob.getTarget() != null || mob.level().isNight() || mob.hurtSinceChewStart) {
                return false;
            }
            if (!NekomasFixedConfig.TERMITES_EAT_LOGS.get()
                    || !ForgeEventFactory.getMobGriefingEvent(mob.level(), mob)) {
                return false;
            }
            if (!this.logIntact()) {
                return false;
            }
            return mob.isChewing() || this.approachTicks < APPROACH_LIMIT;
        }

        @Override
        public void start() {
            // Being hurt only cancels the chew it happened during - it must not keep blocking every
            // attempt after that one, or a termite hurt once while chewing would never chew again.
            this.termite.hurtSinceChewStart = false;
            this.approachTicks = 0;
            this.repathAttempts = 0;
            if (this.plannedPath != null) {
                this.termite.getNavigation().moveTo(this.plannedPath, SPEED);
                this.plannedPath = null;
            }
        }

        @Override
        public void stop() {
            // The selector calls this without consulting the goal when Float or the mound goal takes
            // over, so it has to be the one place that always tidies a chew away.
            this.termite.clearChew();
            this.logPos = null;
            this.standPos = null;
            this.expectedState = null;
            this.plannedPath = null;
            this.approachTicks = 0;
            this.repathAttempts = 0;
        }

        @Override
        public void tick() {
            Termite mob = this.termite;
            if (this.logPos == null || this.standPos == null) {
                return;
            }
            // A finished chew leaves this goal running until the selector next polls it, so the
            // tick in between does nothing at all rather than re-running the walk to a log that
            // is already hollow.
            if (mob.isLaden()) {
                return;
            }
            mob.getLookControl().setLookAt(Vec3.atCenterOf(this.logPos));
            if (!mob.isChewing()) {
                this.approachTicks++;
                if (this.chewConditionsHold()) {
                    this.startChewing();
                } else if (mob.getNavigation().isDone()) {
                    Path path = mob.getNavigation().createPath(this.standPos, 0);
                    if (this.repathAttempts++ >= 3 || path == null || !path.canReach()
                            || !mob.getNavigation().moveTo(path, SPEED)) {
                        mob.markTargetUnreachable(this.logPos);
                        this.logPos = null;
                    }
                }
                return;
            }
            if (!this.chewConditionsHold()) {
                mob.clearChew();
                return;
            }
            mob.chewProgress++;
            if (mob.chewProgress >= NekomasFixedConfig.TERMITE_CHEW_TICKS.get()) {
                this.finish();
                return;
            }
            // After the finish check, so the last gnaw does not land on top of the log giving way.
            if (mob.chewProgress % 12 == 0) {
                this.playChewSound();
            }
        }

        private boolean findLog() {
            Termite mob = this.termite;
            Level level = mob.level();
            int probed = 0;
            for (BlockPos cursor : BlockPos.withinManhattan(mob.blockPosition(), 12, 4, 12)) {
                // isLoaded first: reading a block state at an unloaded position would load the chunk.
                if (!level.isLoaded(cursor)) {
                    continue;
                }
                BlockState state = level.getBlockState(cursor);
                if (!HollowLogType.hasHollowVariant(state)) {
                    continue;
                }
                BlockPos log = cursor.immutable();
                if (mob.isTargetUnreachable(log) || !state.canEntityDestroy(level, log, mob)) {
                    continue;
                }
                Set<BlockPos> spots = Termite.approachSpots(level, log, false);
                if (spots.isEmpty()) {
                    mob.markTargetUnreachable(log);
                    continue;
                }
                probed++;
                Path path = mob.getNavigation().createPath(spots, 0);
                if (path != null && path.canReach() && path.getTarget() != null) {
                    this.logPos = log;
                    this.standPos = path.getTarget().immutable();
                    this.expectedState = state;
                    this.plannedPath = path;
                    return true;
                }
                mob.markTargetUnreachable(log);
                // Pathfinding is the expensive part of this search - three logs is the budget.
                if (probed >= 3) {
                    return false;
                }
            }
            return false;
        }

        private void startChewing() {
            Termite mob = this.termite;
            mob.getNavigation().stop();
            Vec3 toLog = Vec3.atCenterOf(this.logPos).subtract(mob.position());
            float yaw = (float) (Mth.atan2(toLog.z, toLog.x) * (180.0 / Math.PI)) - 90.0F;
            mob.setYRot(yaw);
            mob.setYBodyRot(yaw);
            mob.setYHeadRot(yaw);
            // Another mod may be protecting this log - ask before committing to a chew on wood that
            // was never going to give way, and drop it as unreachable instead of parking here.
            if (!ForgeHooks.canEntityDestroy(mob.level(), this.logPos, mob)) {
                mob.markTargetUnreachable(this.logPos);
                this.logPos = null;
                return;
            }
            mob.beginChew(this.logPos);
            this.playChewSound();
        }

        private boolean chewConditionsHold() {
            Termite mob = this.termite;
            Level level = mob.level();
            return NekomasFixedConfig.TERMITES_EAT_LOGS.get()
                    && ForgeEventFactory.getMobGriefingEvent(level, mob)
                    && !level.isNight()
                    && mob.getTarget() == null
                    && !mob.isLaden()
                    && !mob.hurtSinceChewStart
                    && this.logIntact()
                    && Termite.isStandable(level, this.standPos)
                    && this.atStandingSpot()
                    && mob.onGround()
                    && !mob.isInFluidType();
        }

        private boolean logIntact() {
            Level level = this.termite.level();
            // Block states are shared instances, so identity is the exact-state check.
            return this.expectedState != null
                    && level.isLoaded(this.logPos)
                    && level.getBlockState(this.logPos) == this.expectedState;
        }

        /**
         * A 0.5-wide mob is steered to the middle of its node but counted as having reached it up to
         * 0.75 away, so anything tighter than that would never be satisfied.
         */
        private boolean atStandingSpot() {
            return this.termite.distanceToSqr(Vec3.atBottomCenterOf(this.standPos)) <= 0.5625;
        }

        private void playChewSound() {
            Termite mob = this.termite;
            mob.level().playSound(null, mob, SoundRegistry.TERMITE_CHEW.get(), SoundSource.HOSTILE,
                    0.6F, 0.9F + mob.getRandom().nextFloat() * 0.3F);
        }

        private void finish() {
            Termite mob = this.termite;
            Level level = mob.level();
            BlockState old = this.expectedState;
            BlockState hollow = HollowLogType.getHollowState(old);
            // Last look at everything immediately before the swap: a chew that cannot finish cleanly
            // leaves the log exactly as it was. Another mod may be protecting the block, and it gets
            // the final say right here, before the swap actually happens.
            if (!this.chewConditionsHold() || hollow.is(Blocks.AIR)
                    || !ForgeHooks.canEntityDestroy(level, this.logPos, mob)
                    || !level.setBlock(this.logPos, hollow, 3)) {
                mob.clearChew();
                return;
            }
            level.gameEvent(GameEvent.BLOCK_CHANGE, this.logPos, GameEvent.Context.of(mob, hollow));
            if (level instanceof ServerLevel serverLevel) {
                TermiteChewEffects.onChewFinished(serverLevel, this.logPos, old);
            }
            level.playSound(null, mob, SoundRegistry.TERMITE_CHEW_FINISH.get(), SoundSource.BLOCKS,
                    1.0F, 0.92F + mob.getRandom().nextFloat() * 0.16F);
            mob.setLaden(true);
            mob.clearChew();
        }
    }

    // EntityDataSerializer.simpleEnum(Termite.State.class) is byte-identical to the
    // old ByteBufCodecs.idMapper wire form for a bare 2-constant enum whose declared index equals its
    // ordinal - so the wire contract is simply "ordinal". PACKET_CODEC, INDEX_TO_VALUE and getIndex()
    // are dropped, not ported. New states only ever get appended at the end for the same reason.
    public enum State {
        IDLING,
        SWIPING,
        CHEWING
    }
}
