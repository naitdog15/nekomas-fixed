package net.greenjab.nekomasfixed.registry.worldgen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.greenjab.nekomasfixed.config.NekomasFixedConfig;
import net.greenjab.nekomasfixed.registry.block.ClamBlock;
import net.greenjab.nekomasfixed.registry.registries.BlockEntityTypeRegistry;
import net.greenjab.nekomasfixed.registry.registries.BlockRegistry;
import net.greenjab.nekomasfixed.registry.registries.LootTableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;

public class ClamFeature extends Feature<CountConfiguration> {
	public ClamFeature(Codec<CountConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<CountConfiguration> context) {
		if (!NekomasFixedConfig.CLAM_GENERATION.get()) return false;

		int i = 0;
		RandomSource random = context.random();
		WorldGenLevel level = context.level();
		BlockPos blockPos = context.origin();
		int j = context.config().count().sample(random);

		for (int k = 0; k < j; k++) {
			int l = random.nextInt(8) - random.nextInt(8);
			int m = random.nextInt(8) - random.nextInt(8);
			int n = level.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX() + l, blockPos.getZ() + m);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + l, n, blockPos.getZ() + m);
			Block clamType =  getClam(level.getRandom().nextFloat());
			BlockState blockState =clamType.defaultBlockState().setValue(ClamBlock.WATERLOGGED, true).setValue(ClamBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
			if (level.getBlockState(blockPos2).is(Blocks.WATER) &&
					level.getBlockState(blockPos2.above()).is(Blocks.WATER) &&
					level.getBlockState(blockPos2.below()).is(Blocks.SAND) &&
					blockState.canSurvive(level, blockPos2)) {
				level.setBlock(blockPos2, blockState, Block.UPDATE_CLIENTS);
				level.getBlockEntity(blockPos2, BlockEntityTypeRegistry.CLAM_BLOCK_ENTITY.get())
						.ifPresent(blockEntity -> {
							// Clams roll their pearl from the fishing loot table the moment they generate.
							LootTable lootTable = level.getServer()
									.getLootData()
									.getLootTable(LootTableRegistry.CLAM_LOOT_TABLE);

							LootParams lootContextParameterSet = (new LootParams.Builder(level.getLevel())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos2)).withParameter(LootContextParams.TOOL, null).withParameter(LootContextParams.THIS_ENTITY, null).withLuck(getLuck(clamType)).create(LootContextParamSets.FISHING);

							ObjectArrayList<ItemStack> loots = lootTable.getRandomItems(lootContextParameterSet);
							if (!loots.isEmpty()) blockEntity.setHeldStack(loots.get(0));
						});
				i++;
			}
		}

		return i > 0;
	}

	@Unique
	private Block getClam(float rarity) {
		if (rarity>0.5) return BlockRegistry.CLAM.get();
		if (rarity>0.25) return BlockRegistry.CLAM_BLUE.get();
		if (rarity>0.125) return BlockRegistry.CLAM_PINK.get();
		if (rarity>0.0625) return BlockRegistry.CLAM_PURPLE.get();
		return BlockRegistry.CLAM.get();
	}

	public static int getLuck(Block clamType) {
		if (clamType==BlockRegistry.CLAM.get()) return 0;
		if (clamType==BlockRegistry.CLAM_BLUE.get()) return 1;
		if (clamType==BlockRegistry.CLAM_PINK.get()) return 2;
		if (clamType==BlockRegistry.CLAM_PURPLE.get()) return 3;
		return 0;
	}
}
