package net.greenjab.nekomasfixed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.Set;
import java.util.function.Function;

/**
 * Stashing a live entity in a block entity or on an item stack, and getting it back. Shared by
 * {@code AnimalComponent} and {@code TermitehiveBlockEntity.TermiteData}, which use the identical
 * shape with different denylists; a helper owned by either would be reimplemented by the other and
 * the two would drift apart.
 * <p>
 * The loader, {@code EntityType.loadEntityRecursive(CompoundTag, Level, Function<Entity,Entity>)},
 * reads the entity type OUT OF THE TAG via {@code EntityType.by(tag)}, so {@link #store} has to
 * write an explicit {@code "id"} key alongside {@code saveWithoutId} — without it the blob loads
 * back as nothing at all.
 * <p>
 * Do not substitute {@code Entity#save(CompoundTag)} for {@code saveWithoutId} here: its body is
 * {@code return this.isPassenger() ? false : this.saveAsPassenger(tag);}, so it silently writes
 * nothing for an entity that happens to be riding something. That is a data-loss path with no
 * compile error and no log line.
 */
public final class EntityNbtHelper {
    private EntityNbtHelper() {
    }

    /** {@code saveWithoutId} plus an explicit {@code "id"} key, with the denylist pruned. */
    public static CompoundTag store(Entity entity, Set<String> denylist) {
        CompoundTag tag = entity.saveWithoutId(new CompoundTag());
        for (String key : denylist) {
            tag.remove(key);
        }
        tag.putString("id", EntityType.getKey(entity.getType()).toString());
        return tag;
    }

    /** {@code EntityType.loadEntityRecursive(tag, level, e -> e)} — the type comes out of the tag. */
    @Nullable
    public static Entity load(CompoundTag tag, Level level) {
        return EntityType.loadEntityRecursive(tag, level, Function.identity());
    }
}
