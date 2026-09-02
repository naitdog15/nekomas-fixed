package net.greenjab.nekomasfixed.registry.entity.WildFire;

import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireBomb extends Projectile {

    /** Deliberately far below mob gravity - the bomb is lobbed on a long, lazy arc. */
    static final double GRAVITY = 0.03;

    private static final ExplosionDamageCalculator EXPLOSION_BEHAVIOR = new ExplosionDamageCalculator()  {
        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
            return state.is(Blocks.AIR);
        }
    };

    public FireBomb(Level level, LivingEntity owner) {
        this(EntityTypeRegistry.FIRE_BOMB.get(), level);
        this.setOwner(owner);
        this.moveTo(owner.getX(), owner.getY(), owner.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();
    }

    public FireBomb(EntityType<FireBomb> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void handleEntityEvent(byte status) {
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, GRAVITY, 0.0));
        }
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        Vec3 vec3d;
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getLocation();
        } else {
            vec3d = this.position().add(this.getDeltaMovement());
        }

        this.setPos(vec3d);
        this.updateRotation();
        this.checkInsideBlocks();
        super.tick();
        // The impact goes straight through, gated by the projectile impact event the way every
        // other projectile in the game gates it.
        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()
                && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof WildfireEntity)) {
            super.onHitEntity(entityHitResult);
            if (!this.level().isClientSide()) {
                // A null DamageSource makes Explosion build the standard entity-attributed one itself.
                this.level().explode(this, null, EXPLOSION_BEHAVIOR, entity.getX(), entity.getY() + 1, entity.getZ(), 1, true, Level.ExplosionInteraction.MOB);
                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().explode(this, null, EXPLOSION_BEHAVIOR, this.getX(), this.getY(), this.getZ(), 1, true, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
}
