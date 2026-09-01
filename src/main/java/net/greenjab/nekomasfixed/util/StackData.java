package net.greenjab.nekomasfixed.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.registry.other.ComboComponent;
import net.greenjab.nekomasfixed.registry.other.StoredTimeComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The central NBT facade every one of the ~29 data-component consumer
 * files talks to instead of a 1.21+ {@code DataComponentType}. {@code ComponentRegistry.java} is
 * deleted; this class is its replacement.
 * <p>
 * Network sync is free: 1.20.1 syncs the whole {@code ItemStack} tag to the client as part of the
 * stack's own network representation, so nothing here needs a packet.
 * <p>
 * Never-write-defaults rule: never write a value equal to a component's default, and prune
 * the key — and {@link #ROOT} itself once it is empty — when a value returns to default. Use
 * {@link #writeOrRemove} rather than {@link #write} for every mod-owned default-carrying
 * declaration (exactly ten: the 3 nautilus blocks' {@code ANIMAL} and the 7
 * sickles' {@code COMBO_MULTIPLIER}) — most importantly the two unguarded {@code builder.set(...)}
 * sites in {@code NautilusBlockEntity}/{@code TermitehiveBlockEntity}, which must guard both.
 */
public final class StackData {
    private StackData() {
    }

    /** Codec failures are logged, never swallowed. */
    private static final Logger LOGGER = LoggerFactory.getLogger("nekomasfixed");

    /** One sub-compound, never the bare stack tag — keeps this mod's NBT out of every other mod's way. */
    private static final String ROOT = "nekomasfixed";

    public static final String KEY_STORED_TIME = "stored_time";
    public static final String KEY_COMBO_MULTIPLIER = "combo_multiplier";
    public static final String KEY_CLAM_STATE = "clam_state";
    public static final String KEY_ANIMAL = "animal";
    public static final String KEY_TERMITES = "termites";

    private static final Codec<Integer> CLAM_STATE_CODEC = ExtraCodecs.intRange(0, 3);

    // --- Generic core — every consumer, including TermitesComponent
    // once it exists, may call these directly with its own Codec. ---

    /**
     * An earlier written form
     * was {@code .result().orElse(fallback)}, which turns malformed or version-incompatible stored
     * NBT into an apparently valid default with no log line — a data-integrity failure disguised as
     * success. {@code resultOrPartial(LOGGER::error)} logs the DataResult's own error message and
     * still yields the partial value when one exists; an absent key (the overwhelmingly common case,
     * given the never-write-defaults rule) short-circuits above and logs nothing.
     */
    public static <T> T read(ItemStack stack, String key, Codec<T> codec, T fallback) {
        CompoundTag root = stack.getTagElement(ROOT);
        if (root == null || !root.contains(key)) {
            return fallback;
        }
        return codec.parse(NbtOps.INSTANCE, root.get(key))
                .resultOrPartial(error -> LOGGER.error("nekomasfixed: malformed stack NBT under '{}/{}': {}", ROOT, key, error))
                .orElse(fallback);
    }

    /**
     * Observable success/failure contract: an earlier written form's
     * {@code .result().ifPresent(...)} silently left the PREVIOUS value in NBT when encoding failed,
     * so a caller believing it had written a new value would read back the stale one.
     * <p>
     * Documented policy on failure: the key is REMOVED (never left stale) and {@code false} is
     * returned, so a failed write degrades to "absent" — which every reader resolves to the
     * component's default — rather than to "silently unchanged". Returns {@code true} on success.
     */
    public static <T> boolean write(ItemStack stack, String key, Codec<T> codec, T value) {
        DataResult<Tag> encoded = codec.encodeStart(NbtOps.INSTANCE, value);
        Optional<Tag> tag = encoded.resultOrPartial(
                error -> LOGGER.error("nekomasfixed: failed to encode stack NBT under '{}/{}': {}", ROOT, key, error));
        if (tag.isEmpty()) {
            remove(stack, key);
            return false;
        }
        stack.getOrCreateTagElement(ROOT).put(key, tag.get());
        return true;
    }

    /** The never-write-defaults rule, generically. */
    public static <T> boolean writeOrRemove(ItemStack stack, String key, Codec<T> codec, T value, T defaultValue) {
        if (Objects.equals(value, defaultValue)) {
            remove(stack, key);
            return true;
        }
        return write(stack, key, codec, value);
    }

    /** Removes {@code key} from {@link #ROOT}, and prunes {@link #ROOT} itself once it is empty. */
    public static void remove(ItemStack stack, String key) {
        CompoundTag root = stack.getTagElement(ROOT);
        if (root == null) {
            return;
        }
        root.remove(key);
        if (root.isEmpty()) {
            stack.getOrCreateTag().remove(ROOT);
        }
    }

    // --- Typed convenience pairs, self-contained components only. TermitesComponent is not
    // wrapped here: it depends on TermitehiveBlockEntity.TermiteData, which has no Forge form yet.
    // Call read/write above directly with TermitesComponent.CODEC and StackData.KEY_TERMITES
    // once that lands. ---

    public static StoredTimeComponent readStoredTime(ItemStack stack) {
        return read(stack, KEY_STORED_TIME, StoredTimeComponent.CODEC, new StoredTimeComponent(0));
    }

    public static boolean writeStoredTime(ItemStack stack, StoredTimeComponent value) {
        return writeOrRemove(stack, KEY_STORED_TIME, StoredTimeComponent.CODEC, value, new StoredTimeComponent(0));
    }

    public static ComboComponent readCombo(ItemStack stack) {
        return read(stack, KEY_COMBO_MULTIPLIER, ComboComponent.CODEC, new ComboComponent(0));
    }

    public static boolean writeCombo(ItemStack stack, ComboComponent value) {
        return writeOrRemove(stack, KEY_COMBO_MULTIPLIER, ComboComponent.CODEC, value, new ComboComponent(0));
    }

    public static int readClamState(ItemStack stack) {
        return read(stack, KEY_CLAM_STATE, CLAM_STATE_CODEC, 0);
    }

    public static boolean writeClamState(ItemStack stack, int value) {
        return writeOrRemove(stack, KEY_CLAM_STATE, CLAM_STATE_CODEC, value, 0);
    }

    public static AnimalComponent readAnimal(ItemStack stack) {
        return read(stack, KEY_ANIMAL, AnimalComponent.CODEC, AnimalComponent.DEFAULT);
    }

    public static boolean writeAnimal(ItemStack stack, AnimalComponent value) {
        return writeOrRemove(stack, KEY_ANIMAL, AnimalComponent.CODEC, value, AnimalComponent.DEFAULT);
    }
}
