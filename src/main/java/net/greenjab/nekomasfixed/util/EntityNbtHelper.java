package net.greenjab.nekomasfixed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.Set;
import java.util.function.Function;

/**
 * loadEntityRecursive reads the entity type out of the tag via EntityType.by(tag), so store() writes
 * an explicit "id" key alongside saveWithoutId - without it the blob loads back as nothing.
 * don't use Entity#save here instead: it silently saves nothing for an entity riding something.
 */
public final class EntityNbtHelper {
    private EntityNbtHelper() {
    }

    public static CompoundTag store(Entity entity, Set<String> denylist) {
        CompoundTag tag = entity.saveWithoutId(new CompoundTag());
        for (String key : denylist) {
            tag.remove(key);
        }
        tag.putString("id", EntityType.getKey(entity.getType()).toString());
        return tag;
    }

    @Nullable
    public static Entity load(CompoundTag tag, Level level) {
        return EntityType.loadEntityRecursive(tag, level, Function.identity());
    }
}
