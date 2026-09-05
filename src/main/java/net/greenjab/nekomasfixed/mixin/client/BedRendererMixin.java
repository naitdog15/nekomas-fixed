package net.greenjab.nekomasfixed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.util.MessyBedAccessor;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// the four added colours share a dye slot with a vanilla one, so the block (not the colour) tells
// them apart. the inventory icon uses a shared block entity that keeps a plain red bed's state,
// so item icons are left alone.
@Mixin(BedRenderer.class)
public class BedRendererMixin {

    @Unique
    private static final Map<String, Material> NEKOMASFIXED$BEDDING = new ConcurrentHashMap<>();

    @ModifyVariable(method = "render", at = @At("STORE"))
    private Material nekomasfixed$bedding(Material material, @Local(argsOnly = true) BedBlockEntity bed) {
        BlockState state = bed.getBlockState();
        Block block = state.getBlock();

        String colour = null;
        if (block == BlockRegistry.AMBER_BED.get()) colour = "amber";
        else if (block == BlockRegistry.AQUA_BED.get()) colour = "aqua";
        else if (block == BlockRegistry.INDIGO_BED.get()) colour = "indigo";
        else if (block == BlockRegistry.MAROON_BED.get()) colour = "maroon";

        if (state.hasProperty(MessyBedAccessor.MESSY) && state.getValue(MessyBedAccessor.MESSY)
                && NekomasFixedConfig.MESSY_BEDS.get()) {
            return nekomasfixed$material((colour != null ? colour : bed.getColor().getName()) + "_messy");
        }
        return colour != null ? nekomasfixed$material(colour) : material;
    }

    @Unique
    private static Material nekomasfixed$material(String name) {
        return NEKOMASFIXED$BEDDING.computeIfAbsent(name,
                n -> new Material(Sheets.BED_SHEET, NekomasFixed.id("entity/bed/" + n)));
    }
}
