package net.greenjab.nekomasfixed.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.registry.other.ComboComponent;
import net.greenjab.nekomasfixed.registry.other.StoredTimeComponent;
import net.greenjab.nekomasfixed.registry.other.TermitesComponent;
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
 * Every piece of item data this mod owns lives here, in one sub-compound of the stack tag, and is
 * read and written through this class only — nothing else in the mod should touch that compound
 * by name.
 * <p>
 * Reserved key names inside the compound: {@code stored_time}, {@code combo_multiplier},
 * {@code clam_state}, {@code animal}, {@code termites}, and {@code ingredients} (the last one
 * declared next to its reader on {@code SpecialSoupItem}). Nothing else may claim a name in here
 * without being added to that list.
 * <p>
 * Network sync is free: the whole stack tag already goes to the client as part of the stack's own
 * network representation, so none of this needs a packet of its own.
 * <p>
 * Never-write-defaults rule: a value equal to its default is never written, and the key — and the
 * sub-compound itself once it is empty — is pruned instead. That keeps a stack that has been
 * emptied of mod data byte-identical to a fresh one, so the two still stack together. Prefer
 * {@link #writeOrRemove} (or one of the typed pairs below, which all use it) over the bare
 * {@link #write} for anything that has a meaningful default.
 */
public final class StackData {
    private StackData() {
    }

    /** Codec failures are logged, never swallowed. */
    private static final Logger LOGGER = LoggerFactory.getLogger("nekomasfixed");

    /** One sub-compound, never the bare stack tag — keeps this mod's data out of every other mod's way. */
    private static final String ROOT = "nekomasfixed";

    public static final String KEY_STORED_TIME = "stored_time";
    public static final String KEY_COMBO_MULTIPLIER = "combo_multiplier";
    public static final String KEY_CLAM_STATE = "clam_state";
    public static final String KEY_ANIMAL = "animal";
    public static final String KEY_TERMITES = "termites";

    private static final Codec<Integer> CLAM_STATE_CODEC = ExtraCodecs.intRange(0, 3);

    // --- Generic core. Any consumer may call these directly with its own codec, as
    // SpecialSoupItem does for the stew's ingredient list. ---

    /** True when {@code key} is present on this stack, without decoding it. */
    public static boolean contains(ItemStack stack, String key) {
        CompoundTag root = stack.getTagElement(ROOT);
        return root != null && root.contains(key);
    }

    /**
     * Reads {@code key}, falling back to {@code fallback} when it is absent or unreadable.
     * <p>
     * Decoding uses {@code resultOrPartial} rather than {@code result().orElse(fallback)}: the
     * latter turns malformed or version-incompatible stored data into an apparently valid default
     * with no log line at all, which is a data-integrity failure dressed up as success. This form
     * logs the decoder's own error message and still yields a partial value when one exists. An
     * absent key — the overwhelmingly common case, given the never-write-defaults rule — short
     * circuits above and logs nothing.
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
     * Writes {@code value} under {@code key} and reports whether it landed.
     * <p>
     * A caller has to be able to tell: silently leaving the PREVIOUS value in place when encoding
     * fails means a caller that believes it wrote a new value reads the stale one back. So on
     * failure the key is REMOVED and {@code false} returned — a failed write degrades to "absent",
     * which every reader resolves to the default, and never to "silently unchanged". A partial
     * encode counts as a failure here for the same reason; half a value is not the value.
     */
    public static <T> boolean write(ItemStack stack, String key, Codec<T> codec, T value) {
        DataResult<Tag> encoded = codec.encodeStart(NbtOps.INSTANCE, value);
        Optional<Tag> tag = encoded.result();
        if (tag.isEmpty()) {
            LOGGER.error("nekomasfixed: failed to encode stack NBT under '{}/{}': {}", ROOT, key,
                    encoded.error().map(DataResult.PartialResult::message).orElse("unknown error"));
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

    /**
     * Removes {@code key}, prunes the sub-compound once it is empty, and lets the stack tag itself
     * go back to null once THAT is empty. The last step matters: a leftover empty tag is not equal
     * to no tag, so a stack that kept one would refuse to stack with a fresh one.
     */
    public static void remove(ItemStack stack, String key) {
        CompoundTag root = stack.getTagElement(ROOT);
        if (root == null) {
            return;
        }
        root.remove(key);
        if (root.isEmpty()) {
            stack.removeTagKey(ROOT);
        }
    }

    // --- Typed pairs. Each is read/writeOrRemove against the component's own default, so a
    // caller never has to remember to prune. ---

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

    /** 0 closed, 1 open, 2 open with a pearl. Anything else decodes back to 0. */
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

    public static TermitesComponent readTermites(ItemStack stack) {
        return read(stack, KEY_TERMITES, TermitesComponent.CODEC, TermitesComponent.DEFAULT);
    }

    public static boolean writeTermites(ItemStack stack, TermitesComponent value) {
        return writeOrRemove(stack, KEY_TERMITES, TermitesComponent.CODEC, value, TermitesComponent.DEFAULT);
    }
}
