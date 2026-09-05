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

// blocking is plain ShieldItem; only the burn-back is custom and lives in the damage pipeline.
// that hook only fires for melee - a blocked projectile still costs durability but never burns its shooter
public class WildfireShieldItem extends ShieldItem {

    public WildfireShieldItem(Item.Properties settings) {
        super(settings);
    }

    /** Netherite mends it. An ordinary shield takes planks; this one does not. */
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.NETHERITE_INGOT);
    }

    // checked live at use-time, so toggling this config takes effect immediately
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
