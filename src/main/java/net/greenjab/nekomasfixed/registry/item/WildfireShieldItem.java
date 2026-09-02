package net.greenjab.nekomasfixed.registry.item;

import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.render.entity.NekomasFixedBEWLR;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * The shield the wildfire leaves behind. Its blocking curve is an ordinary shield's, deliberately:
 * a raised shield stops a hit that arrives from within ninety degrees of where the wearer is
 * looking, it costs one durability plus one per point of damage once a hit is worth three or more,
 * it does nothing against a source that goes through shields, and it needs a quarter second of
 * being held up before it counts. All of that is {@link ShieldItem}'s already, which is why this
 * class does not re-implement any of it.
 * <p>
 * What makes it the wildfire's shield is the burn-back it hands whoever it blocks, and that lives
 * with the rest of the damage pipeline rather than here. Worth knowing when reading that code: the
 * game only offers the block-back hook for melee. A blocked hit whose source counts as a projectile
 * still takes the reduction and still costs durability, but never reaches the hook, so an arrow
 * stopped on this shield leaves its archer unburnt.
 * <p>
 * Nothing is needed for the offhand slot either: {@code ShieldItem} is already {@code Equipable}
 * and reports {@code OFFHAND}, and using it raises the shield instead of swapping it into that
 * slot, so the use-it-to-wear-it rule has nothing to do here.
 */
public class WildfireShieldItem extends ShieldItem {

    public WildfireShieldItem(Item.Properties settings) {
        super(settings);
    }

    /** Netherite mends it. An ordinary shield takes planks; this one does not. */
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.NETHERITE_INGOT);
    }

    /**
     * Raising the shield is the whole of its blocking, so the switch sits here: with shield
     * blocking turned off the shield simply never goes up, and therefore soaks nothing, loses no
     * durability and burns nobody. Read at the moment it is used, so flipping the switch takes
     * effect straight away.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        if (!NekomasFixedConfig.WILDFIRE_SHIELD_BLOCKING.get()) {
            return InteractionResultHolder.pass(user.getItemInHand(hand));
        }
        return super.use(level, user, hand);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return NekomasFixedBEWLR.instance();
            }
        });
    }
}
