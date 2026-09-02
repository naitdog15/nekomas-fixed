package net.greenjab.nekomasfixed.registry.entity.Moobloom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.greenjab.nekomasfixed.compat.vanillabackport.BackportedContent;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * This version has no {@code SuspiciousStewEffects} data component, so each variant just carries a
 * plain effect and duration instead of one of those entries, applied through
 * {@code SuspiciousStewItem.saveMobEffect(ItemStack, MobEffect, int duration)}.
 *
 * <p>Not an enum, even though it looks like fifteen fixed constants: the fifteenth,
 * open-eyeblossom, only exists once Vanilla Backport is loaded, has that flower turned on in its own
 * config, and hasn't had the mod's own {@code eyeblossomMoobloom} toggle switched off — three things
 * an enum's constant set, fixed at compile time, cannot express. The fourteen ordinary variants are
 * declared as constants as before; the fifteenth is assembled once, lazily, the first time anything
 * asks this class for its variant list, which in practice is the first time a Moobloom actually spawns
 * or is interacted with — long after mod construction and config load, never at registration.
 */
public final class MoobloomVariants {
    public static final MoobloomVariants ANCIENT = new MoobloomVariants("ancient", 1, Items.TORCHFLOWER.getDefaultInstance(), MobEffects.NIGHT_VISION);
    public static final MoobloomVariants AQUA = new MoobloomVariants("aqua", 1, Items.BLUE_ORCHID.getDefaultInstance(), MobEffects.SATURATION);
    public static final MoobloomVariants BLACK = new MoobloomVariants("black", 1, Items.WITHER_ROSE.getDefaultInstance(), MobEffects.WITHER);
    public static final MoobloomVariants BLUE = new MoobloomVariants("blue", 1, Items.CORNFLOWER.getDefaultInstance(), MobEffects.JUMP);
    public static final MoobloomVariants GRAY = new MoobloomVariants("gray", 1, Items.LILY_OF_THE_VALLEY.getDefaultInstance(), MobEffects.POISON);
    public static final MoobloomVariants ORANGE = new MoobloomVariants("orange", 1, Items.ORANGE_TULIP.getDefaultInstance(), MobEffects.WEAKNESS);
    public static final MoobloomVariants PINK = new MoobloomVariants("pink", 1, Items.PINK_TULIP.getDefaultInstance(), MobEffects.WEAKNESS);
    public static final MoobloomVariants PURPLE = new MoobloomVariants("purple", 1, Items.ALLIUM.getDefaultInstance(), MobEffects.FIRE_RESISTANCE);
    public static final MoobloomVariants RED_1 = new MoobloomVariants("red", 1, Items.RED_TULIP.getDefaultInstance(), MobEffects.WEAKNESS);
    public static final MoobloomVariants RED_2 = new MoobloomVariants("red", 2, Items.POPPY.getDefaultInstance(), MobEffects.DAMAGE_BOOST);
    public static final MoobloomVariants WHITE_1 = new MoobloomVariants("white", 1, Items.AZURE_BLUET.getDefaultInstance(), MobEffects.BLINDNESS);
    public static final MoobloomVariants WHITE_2 = new MoobloomVariants("white", 2, Items.WHITE_TULIP.getDefaultInstance(), MobEffects.WEAKNESS);
    public static final MoobloomVariants WHITE_3 = new MoobloomVariants("white", 3, Items.OXEYE_DAISY.getDefaultInstance(), MobEffects.REGENERATION);
    public static final MoobloomVariants YELLOW = new MoobloomVariants("yellow", 1, Items.DANDELION.getDefaultInstance(), MobEffects.SATURATION);

    /** The name open-eyeblossom is stored under, which is what its constructor call below builds. */
    private static final String OPEN_EYEBLOSSOM_VARIANT = "gray_cow_2";

    /** The fifteenth variant, or null until {@link #values()} has decided whether it exists. */
    private static MoobloomVariants grayOpenEyeblossom;
    private static boolean grayOpenEyeblossomResolved;
    private static List<MoobloomVariants> values;

    public final String path;
    public final ItemStack flower;
    public final MobEffect effect;
    public final int effectDuration;

    MoobloomVariants(String path, int variant, ItemStack flower, MobEffect effect){
        this.path = path.concat("_cow_").concat(Integer.toString(variant));
        this.flower = flower;
        this.effect = effect;
        this.effectDuration = 20 * 15;
    }

    /**
     * Builds (once) and returns the live variant list: the fourteen constants above, plus
     * open-eyeblossom appended at the end exactly when Vanilla Backport's handle for it resolves and
     * the eyeblossomMoobloom toggle is on. Cached after the first call, same as the fourteen constants
     * are effectively cached by being static fields — a mod being unloaded or a config value flipping
     * both require a restart already, so re-checking every call would only cost cycles for no benefit.
     */
    private static List<MoobloomVariants> values() {
        if (values == null) {
            List<MoobloomVariants> built = new ArrayList<>(List.of(
                    ANCIENT, AQUA, BLACK, BLUE, GRAY, ORANGE, PINK, PURPLE,
                    RED_1, RED_2, WHITE_1, WHITE_2, WHITE_3, YELLOW));
            MoobloomVariants openEyeblossom = resolveGrayOpenEyeblossom();
            if (openEyeblossom != null) {
                built.add(openEyeblossom);
            }
            values = List.copyOf(built);
        }
        return values;
    }

    private static MoobloomVariants resolveGrayOpenEyeblossom() {
        if (!grayOpenEyeblossomResolved) {
            if (BackportedContent.OPEN_EYEBLOSSOM.isPresent() && NekomasFixedConfig.EYEBLOSSOM_MOOBLOOM.get()) {
                grayOpenEyeblossom = new MoobloomVariants("gray", 2,
                        new ItemStack(BackportedContent.OPEN_EYEBLOSSOM.get()), MobEffects.SATURATION);
            }
            grayOpenEyeblossomResolved = true;
        }
        return grayOpenEyeblossom;
    }

    /**
     * The texture set a stored variant is drawn with. Every variant is drawn with its own except
     * open-eyeblossom, which has no skin of its own and borrows the plain gray cow's — the flower on
     * its back is what tells the two apart anyway. Draw a gray_cow_2 set and this mapping can go.
     */
    public static String textureFor(String variant) {
        return OPEN_EYEBLOSSOM_VARIANT.equals(variant) ? GRAY.path : variant;
    }

    public static MoobloomVariants getRandomVariant(){
        List<MoobloomVariants> variants = values();
        // Torchflower, wither rose and (when present) open-eyeblossom are the "special" flowers -
        // reachable through breeding/shearing/fromFlower, never handed out to a freshly spawned Moobloom.
        int randInt = new Random().nextInt(0, variants.size());
        while (variants.get(randInt).flower.is(Items.WITHER_ROSE) ||
                variants.get(randInt).flower.is(Items.TORCHFLOWER) ||
                variants.get(randInt) == grayOpenEyeblossom)
            randInt = new Random().nextInt(0, variants.size());
        return variants.get(randInt);
    }

    public static MoobloomVariants fromPath(String path) {
        for (MoobloomVariants variant : values()) {
            if (variant.path.equals(path)) return variant;
        }
        return ANCIENT;
    }

    public static MoobloomVariants fromFlower(Item flower) {
        for (MoobloomVariants variant : values()) {
            if (variant.flower.is(flower)) return variant;
        }
        return ANCIENT;
    }
}
