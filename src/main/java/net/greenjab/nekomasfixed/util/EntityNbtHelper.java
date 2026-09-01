package net.greenjab.nekomasfixed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.Set;
import java.util.function.Function;

/**
 * Shared by {@code AnimalComponent} and
 * {@code TermitehiveBlockEntity.TermiteData}, which use the identical shape with different
 * denylists; a helper owned by either would be reimplemented by the other and the two would
 * diverge.
 * <p>
 * 1.20.1's only loader is {@code EntityType.loadEntityRecursive(CompoundTag, Level,
 * Function<Entity,Entity>)}, which reads the type OUT OF THE TAG via {@code EntityType.by(tag)} —
 * the {@code (type, tag, level, ...)} overload the 26.2 source uses does not exist here. So
 * {@link #store} must write an explicit {@code "id"} key alongside {@code saveWithoutId}.
 * <p>
 * Do not substitute {@code Entity#save(CompoundTag)}: on 1.20.1 its body is
 * {@code return this.isPassenger() ? false : this.saveAsPassenger(tag);} — it silently writes
 * nothing for a passenger, a data-loss path with no compile error and no log line.
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
