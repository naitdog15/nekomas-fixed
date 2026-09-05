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
 * all item data lives in one sub-compound of the stack tag, read/written only through this class.
 * reserved keys: stored_time, combo_multiplier, clam_state, animal, termites, ingredients (declared
 * on SpecialSoupItem) - don't claim a name here without adding it to this list.
 * no packet needed - the stack tag already syncs to the client as part of the stack itself.
 * never-write-defaults: a value equal to its default is pruned, not written, so an emptied stack
 * stays byte-identical to a fresh one and still stacks. prefer writeOrRemove (or the typed pairs
 * below, which all use it) over the bare write for anything with a meaningful default.
 */
public final class StackData {
    private StackData() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("nekomasfixed");

    private static final String ROOT = "nekomasfixed";

    public static final String KEY_STORED_TIME = "stored_time";
    public static final String KEY_COMBO_MULTIPLIER = "combo_multiplier";
    public static final String KEY_CLAM_STATE = "clam_state";
    public static final String KEY_ANIMAL = "animal";
    public static final String KEY_TERMITES = "termites";

    private static final Codec<Integer> CLAM_STATE_CODEC = ExtraCodecs.intRange(0, 3);

    // generic core - any consumer may call these directly with its own codec, as SpecialSoupItem
    // does for the stew's ingredient list.

    /** true when {@code key} is present on this stack, without decoding it. */
    public static boolean contains(ItemStack stack, String key) {
        CompoundTag root = stack.getTagElement(ROOT);
        return root != null && root.contains(key);
    }

    /**
     * uses resultOrPartial rather than result().orElse(fallback): the latter turns malformed or
     * version-incompatible data into an apparently valid default with no log line. this logs the
     * decoder's error and still yields a partial value when one exists.
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
     * on encode failure the key is removed and false returned, rather than leaving the previous
     * value in place - a caller that believes it wrote a new value must not read back the stale one.
     * a partial encode counts as a failure too.
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

    /** the never-write-defaults rule, generically. */
    public static <T> boolean writeOrRemove(ItemStack stack, String key, Codec<T> codec, T value, T defaultValue) {
        if (Objects.equals(value, defaultValue)) {
            remove(stack, key);
            return true;
        }
        return write(stack, key, codec, value);
    }

    /**
     * prunes the sub-compound and then the stack tag once each is empty - a leftover empty tag
     * isn't equal to no tag, so a stack that kept one wouldn't stack with a fresh one.
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

    // typed pairs - each is read/writeOrRemove against the component's own default, so a caller
    // never has to remember to prune.

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

    /** 0 closed, 1 open, 2 open with a pearl. anything else decodes back to 0. */
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
