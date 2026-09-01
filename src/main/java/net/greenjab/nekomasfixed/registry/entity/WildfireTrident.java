package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

/**
 * PORT: 1.20.1's {@code AbstractArrow} has no {@code PACKET_CODEC}/{@code ValueInput}/{@code ValueOutput}
 * hooks and no {@code getDefaultPickupItem()} split - it has a single abstract
 * {@code protected ItemStack getPickupItem()}, exactly like vanilla's own {@code ThrownTrident}
 * (forge-1.20.1-mapped-src/net/minecraft/world/entity/projectile/ThrownTrident.java), which this class
 * now mirrors structurally: a stored {@code tridentItem} field (not a base-class pickup slot),
 * {@code EnchantmentHelper.doPostHurtEffects}/{@code doPostDamageEffects} as two separate calls
 * (1.20.1 has no unified {@code doPostAttackEffects}), and {@code entity.hurt(DamageSource, float)} as
 * the outgoing-damage call (not {@code hurtServer}, which is the *incoming*-damage override hook).
 * The 26.2 {@code findHitEntities} plural override and {@code hitBlockEnchantmentEffects} have no
 * 1.20.1 hook to attach to and are dropped, matching vanilla's own shape exactly.
 */
public class WildfireTrident extends AbstractArrow {
	private static final EntityDataAccessor<Byte> LOYALTY = SynchedEntityData.defineId(WildfireTrident.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData.defineId(WildfireTrident.class, EntityDataSerializers.BOOLEAN);
	private ItemStack tridentItem = new ItemStack(ItemRegistry.WILDFIRE_TRIDENT.get());
	private boolean dealtDamage = false;
	public int returnTimer;

	public WildfireTrident(EntityType<? extends WildfireTrident> entityType, Level level) {
		super(entityType, level);
	}

	public WildfireTrident(Level level, LivingEntity owner, ItemStack stack) {
		super(EntityTypeRegistry.WILDFIRE_TRIDENT.get(), owner, level);
		this.tridentItem = stack.copy();
		this.entityData.set(LOYALTY, this.getLoyalty(stack));
		this.entityData.set(ENCHANTED, stack.hasFoil());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(LOYALTY, (byte) 0);
		this.entityData.define(ENCHANTED, false);
	}

	@Override
	public void tick() {
		if (this.level() instanceof ServerLevel level) level.sendParticles(ParticleTypes.FLAME, this.getX(),
				this.getY()+0.2, this.getZ(), 0, 0, 0.0, 0.0, 0);

		if (this.inGroundTime > 4) this.dealtDamage = true;

		Entity entity = this.getOwner();
		int i = this.entityData.get(LOYALTY);
		if (i > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
			if (!this.isOwnerAlive()) {
				if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED)
					this.spawnAtLocation(this.getPickupItem(), 0.1F);
				this.discard();
			} else {
				if (!(entity instanceof Player) && this.position().distanceTo(entity.getEyePosition()) < entity.getBbWidth() + 1.0) {
					this.discard();
					return;
				}
				this.setNoPhysics(true);
				Vec3 vec3d = entity.getEyePosition().subtract(this.position());
				this.setPosRaw(this.getX(), this.getY() + vec3d.y * 0.015 * i, this.getZ());
				double d = 0.05 * i;
				this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3d.normalize().scale(d)));
				if (this.returnTimer == 0) this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
				this.returnTimer++;
			}
		}
		super.tick();
	}

	private boolean isOwnerAlive() {
		Entity entity = this.getOwner();
		return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer) || !entity.isSpectator());
	}

	public boolean isEnchanted() {
		return this.entityData.get(ENCHANTED);
	}

	@Nullable
	@Override
	protected EntityHitResult findHitEntity(Vec3 currentPosition, Vec3 nextPosition) {
		return this.dealtDamage ? null : super.findHitEntity(currentPosition, nextPosition);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float damage = 8.0F;
		if (entity instanceof LivingEntity livingEntity) {
			damage += EnchantmentHelper.getDamageBonus(this.tridentItem, livingEntity.getMobType());
		}

		Entity owner = this.getOwner();
		DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
		this.dealtDamage = true;
		SoundEvent hitSound = SoundEvents.TRIDENT_HIT;
		if (entity.hurt(damageSource, damage)) {
			if (entity.getType() == EntityType.ENDERMAN) return;

			if (entity instanceof LivingEntity livingEntity) {
				if (owner instanceof LivingEntity ownerLiving) {
					EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
					EnchantmentHelper.doPostDamageEffects(ownerLiving, livingEntity);
				}
				this.doPostHurtEffects(livingEntity);
				entity.igniteForTicks(20*3);
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().multiply(0.02, 0.2, 0.02));
		this.playSound(hitSound, 1.0F, 1.0F);
	}

	@Override
	public ItemStack getWeaponItem() {
		return this.tridentItem.copy();
	}

	@Override
	protected ItemStack getPickupItem() {
		return this.tridentItem.copy();
	}

	@Override
	protected boolean tryPickup(Player player) {
		return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.TRIDENT_HIT_GROUND;
	}

	@Override
	public void playerTouch(Player player) {
		if (this.ownedBy(player) || this.getOwner() == null) super.playerTouch(player);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.dealtDamage = tag.getBoolean("DealtDamage");
		if (tag.contains("Trident", 10)) {
			this.tridentItem = ItemStack.of(tag.getCompound("Trident"));
		}
		this.entityData.set(LOYALTY, this.getLoyalty(this.tridentItem));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("DealtDamage", this.dealtDamage);
		tag.put("Trident", this.tridentItem.save(new CompoundTag()));
	}

	private byte getLoyalty(ItemStack stack) {
		return (byte) Mth.clamp(EnchantmentHelper.getLoyalty(stack), 0, 127);
	}

	@Override
	public void tickDespawn() {
		int i = this.entityData.get(LOYALTY);
		if (this.pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
			super.tickDespawn();
		}
	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
