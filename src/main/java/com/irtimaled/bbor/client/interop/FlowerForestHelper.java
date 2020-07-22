package com.irtimaled.bbor.client.interop;

import java.util.Random;

import com.irtimaled.bbor.common.models.Coords;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

public class FlowerForestHelper {
	private static final ConfiguredFeature<?, ?> flowerForestFlowerFeature = ((DecoratedFeatureConfig) Biomes.FLOWER_FOREST
			.getFlowerFeatures().get(0).config).feature;

	private static Random random;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BlockState getFlowerAtPos(BlockPos pos) {
		return ((FlowerFeature) flowerForestFlowerFeature.feature).getFlowerState(random, pos,
				flowerForestFlowerFeature.config);
	}

	public static BlockState getFlowerAtPos(int x, int y, int z) {
		return getFlowerAtPos(new BlockPos(x, y, z));
	}

	public static void setRandom(Random _random) {
		random = _random;
	}

	public static BlockState getFlowerAtPos(Coords coords) {
		return getFlowerAtPos(coords.getX(), coords.getY(), coords.getZ());
	}
}
