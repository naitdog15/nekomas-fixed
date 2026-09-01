package net.greenjab.nekomasfixed.registry.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class SpearEntity extends Entity {
	private int warmup = 0;
	private boolean startedAttack;
	private int ticksLeft = 20;

	protected static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.DIRECTION);
	protected static final EntityDataAccessor<ItemStack> SPEAR = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.ITEM_STACK);

	public SpearEntity(EntityType<? extends SpearEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DIRECTION, Direction.UP);
		// No spear item exists on 1.20.1, so there is nothing to default the carried stack to; the
		// dispenser wiring always calls setStack before the entity is added to the level.
		this.entityData.define(SPEAR, ItemStack.EMPTY);
	}

	public void setDirection(Direction dir) {
		entityData.set(DIRECTION, dir);
	}
	public Direction getDirection() {
		return entityData.get(DIRECTION);
	}
	public void setStack(ItemStack item) {
		entityData.set(SPEAR, item);
	}
	public ItemStack getStack() {
		return entityData.get(SPEAR);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.warmup = tag.getInt("Warmup");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("Warmup", this.warmup);
	}

	/** How far the stab reaches past the hitbox, along the facing axis only. */
	private Vec3 reachVector() {
		Vec3 unit = Vec3.atLowerCornerOf(getDirection().getNormal());
		return unit.multiply(unit).scale(0.4);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			this.ticksLeft--;
			if (this.ticksLeft == 20-5) {
				Vec3 b = reachVector();
				AABB box = this.getBoundingBox().inflate(b.x, b.y, b.z);
				List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, box);
				if (!list.isEmpty()) {
					// 1.20.1 has no spear sound event; the trident stab is the nearest match.
					this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.TRIDENT_HIT,
							this.getSoundSource(), 1.0F, 1f, false);
					for (int i = 0; i < 12; i++) {
						double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
						double e = this.getY() + 0.05 + this.random.nextDouble();
						double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
						double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						double h = 0.3 + this.random.nextDouble() * 0.3;
						double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						this.level().addParticle(ParticleTypes.CRIT, d, e, f, g, h, j);
					}
				}
			}
		} else if (--this.warmup < 0) {
			if (this.warmup == -5) {
				Vec3 b = reachVector();
				AABB box = this.getBoundingBox().inflate(b.x, b.y, b.z);
				for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
					this.damage(livingEntity);
				}
			}

			if (!this.startedAttack) {
				this.level().broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
				this.startedAttack = true;
			}

			if (--this.ticksLeft <= 0) {
				this.discard();
			}
		}
	}

	// PORT: 26.2's PiercingWeapon/Weapon DataComponents,
	// DataComponents.ATTRIBUTE_MODIFIERS-derived damage, and Player#stabAttack/onAttack/postPiercingAttack
	// are all part of the 1.21.2+ combat rework and have no 1.20.1 counterpart (1.20.1's Item.Properties
	// carries no components at all - components landed in 1.20.5). The fake-"Dispenser"-player positioning
	// technique is preserved (still valid 1.20.1 API), but the piercing-weapon-component attack dispatch is
	// replaced with a direct target.hurt(...) call using a fixed base damage plus the wielded stack's own
	// Sharpness-family bonus (EnchantmentHelper.getDamageBonus, the 1.20.1 form). Per-spear-material damage
	// scaling previously read from the stack's attribute-modifier component cannot be reproduced without the
	// spear Item class exposing a damage value some other way.
	private static final float BASE_SPEAR_DAMAGE = 8.0F;

	private void damage(LivingEntity target) {
		if (target.isAlive() && !target.isInvulnerable()) {
			if (this.level() instanceof ServerLevel level) {
				Direction direction = entityData.get(DIRECTION);
				float yRot = direction.getAxis().isHorizontal() ? direction.toYRot() : 0.0F;
				Player player = new Player(level, this.blockPosition(), yRot, new GameProfile(UUID.randomUUID(), "Dispenser")) {
					@Override
					public boolean isSpectator() {
						return false;
					}

					@Override
					public boolean isCreative() {
						return false;
					}
				};
				ItemStack stack = entityData.get(SPEAR);
				if (direction.getAxis().isHorizontal()) {
					player.absMoveTo(this.getX(), this.getY(), this.getZ(), direction.toYRot(), 0);
				} else {
					player.absMoveTo(this.getX(), this.getY(), this.getZ(), 0, direction==Direction.UP?-90:90);
				}
				player.attackStrengthTicker = 1000;
				player.getInventory().setItem(0, stack);
				DamageSource damageSource = this.damageSources().playerAttack(player);
				float damage = BASE_SPEAR_DAMAGE + EnchantmentHelper.getDamageBonus(stack, target.getMobType());
				if (target.hurt(damageSource, damage)) {
					// 1.20.1 has no unified EnchantmentHelper.doPostAttackEffects(level, target, source);
					// the two-call vanilla form is doPostHurtEffects(victim, attacker) + doPostDamageEffects(attacker, victim)
					// (see ThrownTrident.onHitEntity for the real vanilla usage this mirrors).
					EnchantmentHelper.doPostHurtEffects(target, player);
					EnchantmentHelper.doPostDamageEffects(player, target);
				}
			}
		}
	}

	@Override
	public void handleEntityEvent(byte status) {
		super.handleEntityEvent(status);
		if (status == EntityEvent.START_ATTACKING) {
			if (!this.isSilent()) this.level().playLocalSound(this.getX(),this.getY(),this.getZ(),SoundEvents.PISTON_EXTEND,
					this.getSoundSource(),0.7F,0.7f,false);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}
}