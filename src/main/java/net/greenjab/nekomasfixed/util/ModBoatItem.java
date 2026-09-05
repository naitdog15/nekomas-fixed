package net.greenjab.nekomasfixed.util;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * vanilla's BoatItem only places EntityType.BOAT/CHEST_BOAT and stamps a Boat.Type onto them, so it
 * can't place this mod's boat types. same placement logic driven by a supplied entity type instead;
 * the supplier is a RegistryObject, dereferenced only on right-click, never during registration.
 */
public class ModBoatItem extends Item {

    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    private final Supplier<? extends EntityType<? extends Boat>> boatType;

    public ModBoatItem(Supplier<? extends EntityType<? extends Boat>> boatType, Item.Properties settings) {
        super(settings);
        this.boatType = boatType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        // Don't place a boat inside whatever the player is already looking at within reach.
        Vec3 viewVector = player.getViewVector(1.0F);
        List<Entity> inTheWay = level.getEntities(player,
                player.getBoundingBox().expandTowards(viewVector.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
        if (!inTheWay.isEmpty()) {
            Vec3 eyePosition = player.getEyePosition();
            for (Entity entity : inTheWay) {
                if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eyePosition)) {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        Boat boat = this.boatType.get().create(level);
        if (boat == null) {
            return InteractionResultHolder.fail(stack);
        }
        Vec3 placement = hitResult.getLocation();
        boat.moveTo(placement.x, placement.y, placement.z, player.getYRot(), 0.0F);
        if (!level.noCollision(boat, boat.getBoundingBox())) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, placement);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
