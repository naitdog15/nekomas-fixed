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

/**
 * A creature a nautilus shell is carrying, as a full serialized entity NBT blob — minus the denylist
 * below, plus the {@code EntityType} key {@link EntityNbtHelper#store} writes. It has to be a real
 * blob rather than a tooltip-only summary: the entity is genuinely reconstructed and spawned by both
 * live callers, the shell item being destroyed and the nautilus block entity releasing its guest.
 * <p>
 * None of the machinery the Fabric version built this from — {@code TypedEntityData},
 * {@code ProblemReporter.ScopedCollector}, {@code TagValueOutput}, {@code TooltipProvider},
 * {@code DataComponentGetter} — has a 1.20.1 counterpart, so this record stands on plain codecs
 * instead. Read and written through {@code StackData} under key {@code "animal"}, never a 1.21+
 * data component.
 */
public record AnimalComponent(List<AnimalComponent.StoredEntityData> animal) {
    /**
     * The 26.2 source's list carried
     * post-1.21 snake_case key names that simply do not appear in a 1.20.1 entity tag, so pruning
     * them removed nothing and captured animals kept stale fall/sleep/leash/hive/drop-chance state.
     * The 1.20.1 names for the same data:
     * <ul>
     *   <li>{@code fall_distance} -> {@code FallDistance}</li>
     *   <li>{@code sleeping_pos} -> {@code SleepingX}/{@code SleepingY}/{@code SleepingZ} (three
     *       separate int fields on 1.20.1, not one compound)</li>
     *   <li>{@code drop_chances} -> {@code ArmorDropChances} + {@code HandDropChances} (two lists)</li>
     *   <li>{@code leash} -> {@code Leash}</li>
     *   <li>{@code hive_pos} -> {@code HivePos}</li>
     * </ul>
     * Names already correct for 1.20.1 and kept verbatim: Air, Brain, CanPickUpLoot, DeathTime,
     * FallFlying, Fire, HurtByTimestamp, HurtTime, LeftHanded, Motion, NoGravity, OnGround,
     * PortalCooldown, Pos, Rotation, CannotEnterHiveTicks, Passengers, UUID.
     */
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

    /**
     * The "Holding: …" summary line, keyed {@code container.nautilus}. Empty when nothing is stored,
     * and only ever the FIRST creature — a nautilus shell holds one.
     * <p>
     * Called from {@code Item#appendHoverText} (the base-class injection in {@code mixin/ItemMixin});
     * on 26.2 this was the record's own {@code addToTooltip}, which 1.20.1 has no interface for.
     * <p>
     * The {@code variant} handling differs from the original in one way that matters: 26.2 assumed a
     * namespaced value and took {@code split(":")[1]} unconditionally, which throws on an unnamespaced
     * variant. This takes the last segment when there is one and the whole string otherwise.
     */
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
