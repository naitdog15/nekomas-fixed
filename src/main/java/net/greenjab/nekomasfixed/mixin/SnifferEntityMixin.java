package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.mixin.accessor.SnifferAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Picks the sniffer's dig loot by biome instead of always rolling the one vanilla table. Loot tables
 * are plain ids here and are pulled straight off the server's loot data, so the whole drop is rebuilt
 * in place of vanilla's - same params, same item-entity placement, same sound.
 */
@Mixin(Sniffer.class)
public class SnifferEntityMixin {

    @Inject(method = "dropSeed", at = @At("HEAD"), cancellable = true)
    private void dropCustomLoot(CallbackInfo ci) {
        Sniffer sniffer = (Sniffer) (Object) this;
        SnifferAccessor accessor = (SnifferAccessor) sniffer;

        Level world = sniffer.level();
        if (!(world instanceof ServerLevel serverWorld)) return;
        if (sniffer.getEntityData().get(SnifferAccessor.getFinishDigTime()) != sniffer.tickCount) return;

        BlockPos blockPos = accessor.invokeGetDigPos();
        Holder<Biome> biome = serverWorld.getBiome(blockPos);
        String biomeName = biome.unwrapKey().map(key -> key.location().toString()).orElse("unknown");
        float temperature = biome.value().getBaseTemperature();

        ResourceLocation lootTableId;
        if (temperature <= 0.15f) {
            lootTableId = ResourceLocation.withDefaultNamespace("gameplay/sniffer_digging_snowy");
        } else if (biomeName.contains("des")) {
            lootTableId = ResourceLocation.withDefaultNamespace("gameplay/sniffer_digging_desert");
        } else if (biomeName.contains("badlands")) {
            lootTableId = ResourceLocation.withDefaultNamespace("gameplay/sniffer_digging_badlands");
        } else {
            lootTableId = BuiltInLootTables.SNIFFER_DIGGING;
        }

        LootTable lootTable = serverWorld.getServer().getLootData().getLootTable(lootTableId);
        LootParams lootParams = new LootParams.Builder(serverWorld)
                .withParameter(LootContextParams.ORIGIN, accessor.invokeGetDigOrigin())
                .withParameter(LootContextParams.THIS_ENTITY, sniffer)
                .create(LootContextParamSets.GIFT);

        for (ItemStack itemStack : lootTable.getRandomItems(lootParams)) {
            ItemEntity itemEntity = new ItemEntity(serverWorld,
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            serverWorld.addFreshEntity(itemEntity);
        }

        sniffer.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        ci.cancel();
    }
}
