package net.greenjab.nekomasfixed.registry.entity.Moobloom;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class Moobloom extends Cow {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    private static final EntityDimensions BABY_BASE_DIMENSIONS;
    private ItemStack LastFlowerEaten = ItemStack.EMPTY;
    private int flowerRegrowTimer = 20 * 60 * 5;
    public static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(Moobloom.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(Moobloom.class, EntityDataSerializers.BOOLEAN);

    public Moobloom(EntityType<? extends Cow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData);
        this.entityData.set(VARIANT, MoobloomVariants.getRandomVariant().path);

        return data;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0F));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25F, (stack) -> stack.is(ModTags.MOOBLOOM_FLOWERS), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Cow.createAttributes();
    }

    public void setLastFlowerEaten(ItemStack stack) {
        LastFlowerEaten = stack;
    }
    public ItemStack getLastFlowerEaten() {
        return LastFlowerEaten;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sheared", this.entityData.get(SHEARED));
        tag.putInt("FlowerRegrowTimer", this.flowerRegrowTimer);
        tag.putString("VariantPath", this.entityData.get(VARIANT));

        if (!LastFlowerEaten.isEmpty()) {
            tag.put("Item", LastFlowerEaten.save(new CompoundTag()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.flowerRegrowTimer = tag.contains("FlowerRegrowTimer") ? tag.getInt("FlowerRegrowTimer") : 20 * 60 * 5;
        this.entityData.set(VARIANT, tag.contains("VariantPath") ? tag.getString("VariantPath") : "ancient_cow_1");

        LastFlowerEaten = tag.contains("Item") ? ItemStack.of(tag.getCompound("Item")) : ItemStack.EMPTY;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS) && !this.isBaby()) {
            Level level = this.level();
            if (level instanceof ServerLevel serverLevel) {
                if (this.isShearable()) {
                    this.sheared(serverLevel, SoundSource.PLAYERS, itemStack);
                    this.gameEvent(GameEvent.SHEAR, player);
                    itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.SUCCESS;
        } else if (itemStack.is(Items.BOWL) && !this.isBaby()) {
            Level level = this.level();
            if (!level.isClientSide() && level instanceof ServerLevel) {
                // PORT: 1.20.1 has no DataComponents.SUSPICIOUS_STEW_EFFECTS component; the pre-component
                // API is SuspiciousStewItem.saveMobEffect(stack, MobEffect, durationTicks) (NBT-backed).
                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
                MoobloomVariants variant = MoobloomVariants.fromPath(this.entityData.get(VARIANT));
                SuspiciousStewItem.saveMobEffect(stew, variant.effectHolder.value(), variant.effectDuration);
                player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                if (!player.getInventory().add(stew)) {
                    player.drop(stew, false);
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public void sheared(ServerLevel level, SoundSource shearedSoundCategory, ItemStack shears) {
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);

            for(int i = 0; i < shears.getCount(); ++i) {
                ItemEntity itemEntity = this.spawnAtLocation(MoobloomVariants.fromPath(this.entityData.get(VARIANT)).flower, 1.0F);
                if (itemEntity != null) {
                    itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1F, this.random.nextFloat() * 0.05F, (this.random.nextFloat() - this.random.nextFloat()) * 0.1F));
                }
            }

        this.setSheared(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHEARED, false);
        this.entityData.define(VARIANT, "ancient_cow_1");
    }
    @Override
    public Moobloom getBreedOffspring(ServerLevel level, AgeableMob other) {
        Moobloom child = EntityTypeRegistry.MOOBLOOM.get().create(level, MobSpawnType.BREEDING);
        assert child != null;

        MoobloomVariants thisVariant = MoobloomVariants.fromPath(this.entityData.get(VARIANT));
        String result = thisVariant.path;
        if (other instanceof Moobloom mate) {
            MoobloomVariants secondVariant = MoobloomVariants.fromPath(mate.getEntityData().get(VARIANT));
            MoobloomVariants flowerVariant = MoobloomVariants.fromFlower(this.LastFlowerEaten.getItem());
            MoobloomVariants flowerVariant2 = MoobloomVariants.fromFlower(mate.getLastFlowerEaten().getItem());
            double random = level.getRandom().nextFloat();
            if (random <= 0.35) {
                result = thisVariant.path;
            } else if (random <= 0.7) {
                result = secondVariant.path;
            } else if (random <= 0.85) {
                result = flowerVariant.path;
            } else {
                result = flowerVariant2.path;
            }
        }
        child.getEntityData().set(VARIANT, result);
        child.getEntityData().set(SHEARED, true);
        return child;
    }

    public void setSheared(boolean val){
        this.entityData.set(SHEARED, val);
        this.flowerRegrowTimer = 20 * 60 * 5;}

    public boolean isShearable(){return !this.entityData.get(SHEARED);}

    public void regrowFlowers(){
        this.flowerRegrowTimer = 20 * 60 * 5;
        this.setSheared(false);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.MOOBLOOM_FLOWERS);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        if (this.entityData.get(SHEARED)) {
            if (this.flowerRegrowTimer > 0) this.flowerRegrowTimer--;
            else this.regrowFlowers();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.0001) {
                runAnimationState.startIfStopped(this.tickCount);
                idleAnimationState.stop();
            } else {
                idleAnimationState.startIfStopped(this.tickCount);
                runAnimationState.stop();
            }
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    static {
        BABY_BASE_DIMENSIONS = EntityTypeRegistry.MOOBLOOM.get().getDimensions().scale(0.5F).withEyeHeight(0.665F);
    }
}
