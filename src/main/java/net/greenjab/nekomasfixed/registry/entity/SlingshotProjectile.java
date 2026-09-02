package net.greenjab.nekomasfixed.registry.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.compat.vanillabackport.BackportedContent;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class SlingshotProjectile extends ThrowableItemProjectile {

    private boolean shatter = false;
    private int ticksStuck = 0;
    private ItemStack weapon = ItemStack.EMPTY;

    public SlingshotProjectile(Level level, LivingEntity owner, ItemStack stack, ItemStack weapon, boolean shatter) {
        super(EntityTypeRegistry.SLINGSHOT_PROJECTILE.get(), owner, level);
        this.setItem(stack);
        this.weapon = weapon.copy();
        this.shatter = shatter;
    }

    public SlingshotProjectile(EntityType<SlingshotProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.DEATH) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(this.getParticleParameters(), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.level().isClientSide()) {
            if (!this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7))) {
                ticksStuck++;
            } else ticksStuck =0;
        }
    }


    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        DamageSource damageSource = this.damageSources().thrown(this, this.getOwner());
        if (entity.hurt(damageSource, getDamage(this.getItem().getItem()))) {
            if (entity instanceof LivingEntity livingEntity2) {
                this.knockback(livingEntity2);
            }
        }
    }

    /**
     * Punch is read off the slingshot that fired the shot and applied the way an arrow applies it:
     * the horizontal travel direction scaled by level, 0.6 and the target's knockback resistance,
     * plus the same small upward nudge.
     */
    protected void knockback(LivingEntity target) {
        double d = this.weapon.isEmpty() ? 0.0 : NekomasFixed.enchantLevel(this.weapon, "punch");
        if (d > 0.0) {
            double e = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3d = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d * 0.6 * e);
            if (vec3d.lengthSqr() > 0.0) target.push(vec3d.x, 0.1, vec3d.z);
        }
    }

    private float getDamage(Item item) {
        int damage;
        if (isBackportedRound(item, BackportedContent.COPPER_NUGGET)) damage = 2;
        else if (item==Items.GOLD_NUGGET) damage = 3;
        else if (item==Items.IRON_NUGGET) damage = 4;
        else if (item==Items.AMETHYST_SHARD) damage = 2;
        else if (isBackportedRound(item, BackportedContent.RESIN_CLUMP)) damage = 1;
        else damage = 2;
        damage +=NekomasFixed.enchantLevel(weapon, "power");
        return damage;
    }

    /**
     * Copper nuggets and resin clumps are not part of this version on their own; they only exist
     * while another mod supplies them. Asking by name keeps the rounds working when they are around
     * and costs nothing when they are not.
     */
    private static boolean isBackportedRound(Item item, RegistryObject<Item> round) {
        return NekomasFixedConfig.BACKPORTED_SLINGSHOT_AMMO.get() && round.isPresent() && item == round.get();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (shatter && getOwner() instanceof LivingEntity entity) {
            shatter = false;
            for (int i = 0;i<5;i++) {
                SlingshotProjectile newSlingshotProjectile = new SlingshotProjectile(this.level(), entity, getItem(), weapon, false);
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    newSlingshotProjectile.setPos(this.getX(), this.getY(), this.getZ());
                    Direction.Axis axis = blockHitResult.getDirection().getAxis();
                    Vec3 vec = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
                    if (Math.signum(this.getDeltaMovement().get(axis))!=Math.signum(vec.get(axis))){
                        Vec3 vec2 = new Vec3(vec.x==0?1:-0.9,vec.y==0?1:-0.9,vec.z==0?1:-0.9);
                        Vec3 vec3d = this.getDeltaMovement().multiply(vec2).scale(0.8).add(new Vec3(this.random.triangle(0, 1), 0.25, this.random.triangle(0, 1)));
                        newSlingshotProjectile.setDeltaMovement(vec3d);
                        newSlingshotProjectile.hasImpulse = true;
                    }
                } else if (hitResult instanceof EntityHitResult entityHitResult) {
                    Entity e = entityHitResult.getEntity();
                    newSlingshotProjectile.setPos(e.getX(), this.getY(), e.getZ());
                    Vec3 vec3d = this.getDeltaMovement().scale(0.8).add(new Vec3(this.random.triangle(0, 1), 0.25, this.random.triangle(0, 1)));
                    newSlingshotProjectile.setDeltaMovement(vec3d);
                    newSlingshotProjectile.hasImpulse = true;
                }
                this.level().addFreshEntity(newSlingshotProjectile);
            }
        }
        if (hitResult instanceof BlockHitResult blockHitResult && this.getItem().is(Items.AMETHYST_SHARD) && (blockHitResult.getDirection() != Direction.UP || this.getDeltaMovement().y < -0.035) && ticksStuck<5) {
            Direction.Axis axis = blockHitResult.getDirection().getAxis();
            Vec3 vec = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
            if (Math.signum(this.getDeltaMovement().get(axis))!=Math.signum(vec.get(axis))){
                Vec3 vec2 = new Vec3(vec.x==0?1:-1,vec.y==0?1:-1,vec.z==0?1:-1).scale(0.9);
                this.setDeltaMovement(this.getDeltaMovement().multiply(vec2));
                this.hasImpulse = true;
                this.playSound(SoundEvents.AMETHYST_BLOCK_FALL, 1, 1);
            } else {
                super.onHit(hitResult);
                if (!this.level().isClientSide()) {
                    this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
                    this.discard();
                }
            }
        } else {
            super.onHit(hitResult);
            if (isBackportedRound(this.getItem().getItem(), BackportedContent.RESIN_CLUMP)) {
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                cloud.setRadius(3.0F);
                cloud.setRadiusOnUse(-0.5F);
                cloud.setDuration(60);
                cloud.setWaitTime(0);
                cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
                cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4));
                this.level().addFreshEntity(cloud);
            }
            if (!this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
                this.discard();
            }
        }
    }
}
