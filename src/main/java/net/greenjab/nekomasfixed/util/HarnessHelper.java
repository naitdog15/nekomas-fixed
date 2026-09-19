package net.greenjab.nekomasfixed.util;

import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.sounds.SoundEvents;

public class HarnessHelper {

    public static Equippable ofHarness(ModColors color) {
        HolderGetter<EntityType<?>> registryEntryLookup =
                BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);

        return Equippable.builder(EquipmentSlot.BODY)
                .setEquipSound(SoundEvents.HARNESS_EQUIP)
                .setAsset(ModEquipmentAssets.HARNESS_FROM_MOD_COLOR.get(color))
                .setAllowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_EQUIP_HARNESS))
                .setEquipOnInteract(true)
                .setCanBeSheared(true)
                .setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.HARNESS_UNEQUIP))
                .build();
    }
}