package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import com.mojang.authlib.GameProfile;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.registry.registries.ParticleRegistry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Profile ownership (for a player-head-styled dummy) is a plain nullable {@code GameProfile} synced
 * via {@code EntityDataSerializers.COMPOUND_TAG} plus {@code NbtUtils.writeGameProfile}/
 * {@code readGameProfile} - the same NBT shape {@code SkullBlockEntity} uses for player-head owners.
 * The click-position-based "empty hand removes whichever slot was clicked" behaviour falls through to
 * vanilla ArmorStand's own click-to-equip resolution via {@code Mob.getEquipmentSlotForItem}.
 */
public class TargetDummy extends ArmorStand implements Shearable {
	protected static final EntityDataAccessor<CompoundTag> PROFILE = SynchedEntityData.defineId(TargetDummy.class, EntityDataSerializers.COMPOUND_TAG);
	protected static final EntityDataAccessor<Boolean> ZOMBIE = SynchedEntityData.defineId(TargetDummy.class, EntityDataSerializers.BOOLEAN);
	private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE =  entity -> entity instanceof AbstractMinecart abstractMinecartEntity
			&& abstractMinecartEntity.getMinecartType() == AbstractMinecart.Type.RIDEABLE;
	private int lastHitValue;
	public long lastHitTime;
	/**
	 * 1.20.1's {@link Explosion} applies its knockback by calling {@link #setDeltaMovement(Vec3)}
	 * directly on the very next statement after {@code hurt}, and has no per-entity knockback
	 * multiplier to zero out. A dummy is a fixed prop, so arm this when a blast damages us and swallow
	 * exactly that one velocity write; every other movement still goes through untouched.
	 */
	private boolean ignoreNextExplosionPush;

	public TargetDummy(EntityType<? extends TargetDummy> entityType, Level level) {
		super(entityType, level);
		this.setMaxUpStep(0.0F);
	}

	public static AttributeSupplier.Builder createTargetDummyAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	private boolean canClip() {
		return !this.isNoGravity();
	}

	@Override
	public boolean isEffectiveAi() {
		return super.isEffectiveAi() && this.canClip();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(PROFILE, new CompoundTag());
		this.entityData.define(ZOMBIE, false);
	}

	/** {@code null} when no profile has been set. */
	@Nullable
	public GameProfile getTargetDummyProfile() {
		CompoundTag tag = this.entityData.get(PROFILE);
		return tag.isEmpty() ? null : NbtUtils.readGameProfile(tag);
	}

	private void setTargetDummyProfile(@Nullable GameProfile profile) {
		this.entityData.set(PROFILE, profile == null ? new CompoundTag() : NbtUtils.writeGameProfile(new CompoundTag(), profile));
	}

	public boolean isZombie() {
		return this.entityData.get(ZOMBIE);
	}

	private void setZombie(boolean zombie) {
		this.entityData.set(ZOMBIE, zombie);
	}

	public boolean canUseSlot(EquipmentSlot slot) {
		return true;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		GameProfile profile = this.getTargetDummyProfile();
		if (profile != null) {
			tag.put("profile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
		}
		tag.putInt("LastDamage", lastHitValue);
		tag.putBoolean("IsZombie", isZombie());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.noPhysics = !this.canClip();
		if (tag.contains("profile")) {
			this.entityData.set(PROFILE, tag.getCompound("profile"));
		}
		this.lastHitValue = tag.getInt("LastDamage");
		this.setZombie(tag.getBoolean("IsZombie"));
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity entity) {
	}

	@Override
	protected void pushEntities() {
		for (Entity entity : this.level().getEntities(this, this.getBoundingBox(), RIDEABLE_MINECART_PREDICATE)) {
			if (this.distanceToSqr(entity) <= 0.2) {
				entity.push(this);
			}
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.is(Items.SHEARS)) {
			if (!player.level().isClientSide()) {
				this.breakAndDropItem(this.damageSources().generic());
				this.spawnBreakParticles();
				this.kill();
				itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}
			return InteractionResult.SUCCESS;
		} else if (itemStack.is(Items.NAME_TAG)) {
			if (itemStack.hasCustomHoverName()) {
				Component name = itemStack.getHoverName();
				String s = name.getString();
				if (!s.isEmpty()) setTargetDummyProfile(new GameProfile(UUID.randomUUID(), s));
				setZombie(false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else if (itemStack.is(Items.PLAYER_HEAD)) {
			CompoundTag stackTag = itemStack.getTag();
			GameProfile headProfile = stackTag != null && stackTag.contains("SkullOwner", 10)
					? NbtUtils.readGameProfile(stackTag.getCompound("SkullOwner")) : null;
			if (headProfile != null) {
				setTargetDummyProfile(headProfile);
				setZombie(false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else if (itemStack.is(Items.ZOMBIE_HEAD)||itemStack.is(Items.SKELETON_SKULL)||itemStack.is(Items.ROTTEN_FLESH)) {
			setTargetDummyProfile(null);
			setZombie(true);
			return InteractionResult.SUCCESS;
		} else if (itemStack.is(Items.HAY_BLOCK)) {
			setTargetDummyProfile(null);
			setZombie(false);
			return InteractionResult.SUCCESS;
		} else if (player.isSpectator()) {
			return InteractionResult.SUCCESS;
		} else if (player.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		} else if (!itemStack.isEmpty()) {
			EquipmentSlot equipmentSlot = LivingEntity.getEquipmentSlotForItem(itemStack);
			if (this.equip(player, equipmentSlot, itemStack, hand)) {
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else {
			return InteractionResult.PASS;
		}
	}

	private boolean equip(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
		ItemStack itemStack = this.getItemBySlot(slot);
        if (player.getAbilities().instabuild && itemStack.isEmpty() && !stack.isEmpty()) {
            this.setItemSlot(slot, stack.copyWithCount(1));
            return true;
        } else if (stack.isEmpty() || stack.getCount() <= 1) {
            this.setItemSlot(slot, stack);
            player.setItemInHand(hand, itemStack);
            return true;
        } else if (!itemStack.isEmpty()) {
            return false;
        } else {
            this.setItemSlot(slot, stack.split(1));
            return true;
        }
	}

	public int getLastDamage(){
		return lastHitValue;
	}

	public void setLastDamage(int damage){
		lastHitValue = damage;
	}

	/**
	 * Runs its own hit pipeline instead of {@code LivingEntity#hurt}'s: no invulnerability window (every
	 * swing must register), no health bookkeeping, no knockback ({@code KNOCKBACK_RESISTANCE} is 1.0).
	 * <p>Client-guarded like vanilla's {@code ArmorStand#hurt}, since 1.20.1 has one two-sided hurt.
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!(this.level() instanceof ServerLevel level)) {
			return false;
		}
		// 1.20.1's Explosion pushes us on the statement right after this call.
		if (NekomasFixedConfig.TARGET_DUMMY_RESISTS_EXPLOSIONS.get() && source.is(DamageTypeTags.IS_EXPLOSION)) {
			this.ignoreNextExplosionPush = true;
		}
		if (this.isRemoved()) {
			return false;
		} else if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && source.getEntity() instanceof Mob) {
			return false;
		} else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			this.kill();
			return false;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
			this.onBreak(source);
			this.kill();
			return false;
		} else if (!(source.getEntity() instanceof Player attacker)) {
			return false;
		} else {
			amount = this.getDamageAfterArmorAbsorb(source, amount);
			amount = this.getDamageAfterMagicAbsorb(source, amount);
			// 1.20.1's DamageSource carries no weapon stack; the attacker's held item is what the
			// weapon-item accessor resolves to for a melee hit anyway.
			ItemStack weapon = attacker.getMainHandItem();
			if (weapon.is(Items.SHEARS)) {
				if (source.isCreativePlayer()) {
					this.playBreakSound();
				} else {
					this.breakAndDropItem(source);
					weapon.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
				}
				this.spawnBreakParticles();
				this.kill();
				return true;
			} else if (!attacker.getAbilities().mayBuild) {
				return false;
			} else if (source.isCreativePlayer()) {
				long l = level.getGameTime();
				if (l - this.lastHitTime > 5L) {
					this.registerHit(level, source, amount, l);
				} else {
					this.playBreakSound();
					this.spawnBreakParticles();
					this.kill();
				}
				return true;
			} else {
				this.registerHit(level, source, amount, level.getGameTime());
				return true;
			}
		}
	}

	private void registerHit(ServerLevel level, DamageSource source, float amount, long gameTime) {
		level.broadcastEntityEvent(this, EntityEvent.ARMORSTAND_WOBBLE);
		this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
		this.lastHitTime = gameTime;
		this.lastHitValue = (int) amount;
		level.sendParticles(ParticleRegistry.NUMBER.get(), this.getX(), this.getY() + 2, this.getZ(), 0, 1, 0, 0, amount);
	}

	@Override
	public void setDeltaMovement(Vec3 velocity) {
		if (this.ignoreNextExplosionPush) {
			this.ignoreNextExplosionPush = false;
			return;
		}
		super.setDeltaMovement(velocity);
	}

	@Override
	public void handleEntityEvent(byte status) {
		if (status == EntityEvent.ARMORSTAND_WOBBLE) {
			if (this.level().isClientSide()) {
				this.level()
						.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
				this.lastHitTime = this.level().getGameTime();
			}
		} else {
			super.handleEntityEvent(status);
		}
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d = this.getBoundingBox().getSize() * 4.0;
		if (Double.isNaN(d) || d == 0.0) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	private void spawnBreakParticles() {
		if (this.level() instanceof ServerLevel) {
			((ServerLevel)this.level())
					.sendParticles(
							new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
							this.getX(),
							this.getY(0.6666666666666666),
							this.getZ(),
							10,
							this.getBbWidth() / 4.0F,
							this.getBbHeight() / 4.0F,
							this.getBbWidth() / 4.0F,
							0.05
					);
		}
	}

	private void breakAndDropItem(DamageSource damageSource) {
		ItemStack itemStack = new ItemStack(ItemRegistry.TARGET_DUMMY.get());
		if (this.hasCustomName()) {
			itemStack.setHoverName(this.getCustomName());
		}
		Block.popResource(this.level(), this.blockPosition(), itemStack);
		this.onBreak(damageSource);
	}

	private void onBreak(DamageSource damageSource) {
		this.playBreakSound();
		this.dropAllDeathLoot(damageSource);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getItemBySlot(equipmentSlot);
			if (!itemStack.isEmpty()) {
				this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
				Block.popResource(this.level(), this.blockPosition().above(), itemStack);
			}
		}
	}

	private void playBreakSound() {
		this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
	}

	@Override
	public void travel(Vec3 movementInput) {
		if (this.canClip()) {
			super.travel(movementInput);
		}
	}

	@Override
	public void kill() {
		this.remove(Entity.RemovalReason.KILLED);
		this.gameEvent(GameEvent.ENTITY_DIE);
	}

	@Override
	public boolean ignoreExplosion() {
		return this.isInvisible();
	}

	@Override
	public boolean isPickable() {
		return super.isPickable();
	}

	@Override
	public boolean skipAttackInteraction(Entity attacker) {
		return attacker instanceof Player playerEntity && !this.level().mayInteract(playerEntity, this.blockPosition());
	}

	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}

	@Override
	public LivingEntity.Fallsounds getFallSounds() {
		return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ARMOR_STAND_HIT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ARMOR_STAND_BREAK;
	}

	@Override
	public void thunderHit(ServerLevel level, LightningBolt lightning) {
	}

	@Override
	public boolean isAffectedByPotions() {
		return false;
	}

	@Override
	public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
		return false;
	}

	@Override
	public boolean attackable() {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return this.getType().getDimensions();
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 1.7775F;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(ItemRegistry.TARGET_DUMMY.get());
	}

	@Override
	public boolean canBeSeenByAnyone() {
		return !this.isInvisible();
	}

	// Thin delegates onto ArmorStand's own built-in pose tracking (setHeadPose/getHeadPose/...) -
	// same public names the renderer would look for, backed by the base class instead of a
	// hand-rolled duplicate of a system 1.20.1's ArmorStand already has natively.
	public void setHeadRotation(Rotations angle) { this.setHeadPose(angle); }
	public void setBodyRotation(Rotations angle) { this.setBodyPose(angle); }
	public void setLeftArmRotation(Rotations angle) { this.setLeftArmPose(angle); }
	public void setRightArmRotation(Rotations angle) { this.setRightArmPose(angle); }
	public void setLeftLegRotation(Rotations angle) { this.setLeftLegPose(angle); }
	public void setRightLegRotation(Rotations angle) { this.setRightLegPose(angle); }
	public Rotations getHeadRotation() { return this.getHeadPose(); }
	public Rotations getBodyRotation() { return this.getBodyPose(); }
	public Rotations getLeftArmRotation() { return this.getLeftArmPose(); }
	public Rotations getRightArmRotation() { return this.getRightArmPose(); }
	public Rotations getLeftLegRotation() { return this.getLeftLegPose(); }
	public Rotations getRightLegRotation() { return this.getRightLegPose(); }

	@Override
	public void shear(SoundSource shearedSoundCategory) {
		this.breakAndDropItem(this.damageSources().generic());
		this.spawnBreakParticles();
		this.kill();
	}

	@Override
	public boolean readyForShearing() {
		return true;
	}
}
