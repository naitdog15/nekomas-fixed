package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.nekomasfixed.util.EntityNbtHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// needs the full serialized entity NBT, not a tooltip summary - both live callers (shell destroyed,
// nautilus block entity releasing its guest) actually reconstruct and spawn the entity from this.
// no 1.20.1 counterpart to the Fabric-side TypedEntityData/TagValueOutput/etc, so this is plain
// codecs instead, read/written through StackData under key "animal", never a 1.21+ data component.
public record AnimalComponent(List<AnimalComponent.StoredEntityData> animal) {
    // 1.20.1 NBT keys, not the post-1.21 snake_case names (fall_distance, sleeping_pos, etc) -
    // those don't exist in this NBT and pruning them silently keeps stale state
    public static final List<String> IRRELEVANT_ANIMAL_NBT_KEYS = Arrays.asList(
            "Air", "ArmorDropChances", "HandDropChances", "Brain", "CanPickUpLoot", "DeathTime",
            "FallDistance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion",
            "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation",
            "SleepingX", "SleepingY", "SleepingZ", "CannotEnterHiveTicks", "HivePos", "Passengers",
            "Leash", "UUID"
    );

    public record StoredEntityData(CompoundTag entityData, long tickEnteredHive) {

        public static final Codec<StoredEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CompoundTag.CODEC.fieldOf("entity_data").forGetter(StoredEntityData::entityData),
                Codec.LONG.fieldOf("tick_entered_hive").forGetter(StoredEntityData::tickEnteredHive)
        ).apply(instance, StoredEntityData::new));

        public static final Codec<List<StoredEntityData>> LIST_CODEC = CODEC.listOf();

        public static StoredEntityData of(Entity entity) {
            CompoundTag tag = EntityNbtHelper.store(entity, Set.copyOf(IRRELEVANT_ANIMAL_NBT_KEYS));
            return new StoredEntityData(tag, entity.level().getGameTime());
        }

        @Nullable
        public Entity loadEntity(Level level) {
            return EntityNbtHelper.load(this.entityData.copy(), level);
        }
    }

    public static final Codec<AnimalComponent> CODEC = StoredEntityData.LIST_CODEC
            .xmap(AnimalComponent::new, AnimalComponent::animal);

    public static final AnimalComponent DEFAULT = new AnimalComponent(List.of());

    // variant may not be namespaced here (unlike the original's assumed split(":")[1], which would
    // throw) - take the last segment when there is one, else the whole string
    public Optional<Component> tooltipLine() {
        if (this.animal.isEmpty()) {
            return Optional.empty();
        }
        CompoundTag nbt = this.animal.get(0).entityData();
        String name = nbt.contains("CustomName") ? nbt.getString("CustomName") : null;
        boolean hasAge = nbt.contains("Age");
        int age = hasAge ? nbt.getInt("Age") : 0;
        String variant = null;
        if (nbt.contains("variant")) {
            String raw = nbt.getString("variant");
            String[] parts = raw.split(":");
            String s = parts.length > 1 ? parts[1] : parts[0];
            variant = s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        String typeDescriptionId = EntityType.by(nbt).map(EntityType::getDescriptionId).orElse("entity.unknown");
        String finalVariant = variant;
        return Optional.of(Component.translatable("container.nautilus",
                (hasAge && age < 0 ? "Baby " : ""),
                finalVariant != null ? finalVariant + " " : "",
                Component.translatable(typeDescriptionId),
                name != null ? ": \"" + name + "\"" : ""
        ).withStyle(ChatFormatting.GRAY));
    }
}
