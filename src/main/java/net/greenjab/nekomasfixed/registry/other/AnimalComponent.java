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
 * REWRITTEN, not ported: {@code TypedEntityData}, {@code
 * ProblemReporter.ScopedCollector}, {@code TagValueOutput}, {@code TooltipProvider}, {@code
 * DataComponentGetter} do not exist on 1.20.1. Holds a full serialized entity NBT blob (minus the
 * denylist below, plus the {@code EntityType} key written by {@link EntityNbtHelper#store}); the
 * entity is genuinely reconstructed and spawned by the two live callers (nautilus-block item
 * destroyed -> animal released, and the nautilus block entity) — a tooltip-only record is not an
 * option.
 * <p>
 * Read/written through {@code StackData} under key {@code "animal"}, never a 1.21+ data component.
 */
public record AnimalComponent(List<AnimalComponent.StoredEntityData> animal) {
    /**
     * The 26.2 source's list carried
     * post-1.21 snake_case key names that simply do not appear in a 1.20.1 entity tag, so pruning
     * them removed nothing and captured animals kept stale fall/sleep/leash/hive/drop-chance state.
     * Every replacement name below was re-verified against forge-1.20.1-mapped-src:
     * <ul>
     *   <li>{@code fall_distance} -> {@code FallDistance} — Entity.java:1609</li>
     *   <li>{@code sleeping_pos} -> {@code SleepingX}/{@code SleepingY}/{@code SleepingZ} (three
     *       separate int fields on 1.20.1, not one compound) — LivingEntity.java:687-689</li>
     *   <li>{@code drop_chances} -> {@code ArmorDropChances} + {@code HandDropChances} (two lists)
     *       — Mob.java:399,406</li>
     *   <li>{@code leash} -> {@code Leash} — Mob.java:419-421 ({@code Mob.LEASH_TAG}, line 85)</li>
     *   <li>{@code hive_pos} -> {@code HivePos} — Bee.java:184 ({@code Bee.TAG_HIVE_POS}, line 116)</li>
     * </ul>
     * Names already correct for 1.20.1 and kept verbatim: Air, Brain, CanPickUpLoot, DeathTime,
     * FallFlying, Fire, HurtByTimestamp, HurtTime, LeftHanded, Motion, NoGravity, OnGround,
     * PortalCooldown, Pos, Rotation, CannotEnterHiveTicks (Bee.java:111), Passengers, UUID.
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
     * Mirrors the deleted {@code addToTooltip}'s single-line summary, for whichever mixin/item
     * override re-homes it into {@code appendHoverText}.
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
