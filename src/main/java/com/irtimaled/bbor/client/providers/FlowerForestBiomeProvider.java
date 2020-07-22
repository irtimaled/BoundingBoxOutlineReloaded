package com.irtimaled.bbor.client.providers;

import java.util.HashMap;
import java.util.Map;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.BiomeBorderHelper;
import com.irtimaled.bbor.client.interop.FlowerForestHelper;
import com.irtimaled.bbor.client.models.BoundingBoxFlowerForest;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public class FlowerForestBiomeProvider implements IBoundingBoxProvider<BoundingBoxFlowerForest> {

	private static Coords lastPlayerCoords = null;
	private static Boolean lastRenderAllTransitions = null;
	private static Integer lastRenderDistance = null;
	private static Map<Coords, BoundingBoxFlowerForest> lastFlowerStates = new HashMap<>();

	@Override
	public boolean canProvide(DimensionId dimensionId) {
		return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.FlowerForestFlowers);
	}

	@Override
	public Iterable<BoundingBoxFlowerForest> get(DimensionId dimensionId) {

		Coords playerCoords = Player.getCoords();
		Integer renderDistance = ConfigManager.biomeBordersRenderDistance.get();
		Boolean renderAllTransitions = !ConfigManager.renderOnlyCurrentBiome.get();
		if (!playerCoords.equals(lastPlayerCoords) || !renderDistance.equals(lastRenderDistance)
				|| renderAllTransitions != lastRenderAllTransitions) {
			lastPlayerCoords = playerCoords;
			lastRenderDistance = renderDistance;
			lastRenderAllTransitions = renderAllTransitions;
			lastFlowerStates = getBiomeBorders();
		}
		return lastFlowerStates.values();
	}

	public static void clear() {
		lastFlowerStates = new HashMap<>();
		lastPlayerCoords = null;
	}

	private Map<Coords, BoundingBoxFlowerForest> getBiomeBorders() {
		int renderDistance = lastRenderDistance;
		Coords playerCoords = lastPlayerCoords;
		boolean renderAllTransitions = lastRenderAllTransitions;

		int width = MathHelper.floor(Math.pow(2, 3 + renderDistance));

		int blockX = playerCoords.getX();
		int minX = blockX - width;
		int maxX = blockX + width;

		int blockZ = playerCoords.getZ();
		int minZ = blockZ - width;
		int maxZ = blockZ + width;

		int size = (width * 2) + 1;
		int[][] biomeIds = new int[size][size];
		for (int x = minX; x <= maxX; x++) {
			int matchX = (x - minX);
			for (int z = minZ; z <= maxZ; z++) {
				int matchZ = (z - minZ);
				biomeIds[matchX][matchZ] = BiomeBorderHelper.getBiomeId(x, 255, z);
			}
		}

		Map<Coords, BoundingBoxFlowerForest> borders = new HashMap<>();
		for (int matchX = 1; matchX < size - 2; matchX++) {
			for (int matchZ = 1; matchZ < size - 2; matchZ++) {
				int x = matchX + minX;
				int z = matchZ + minZ;
				int biomeId = biomeIds[matchX][matchZ];
				if (renderAllTransitions || biomeId == Registry.BIOME.getRawId(Biomes.FLOWER_FOREST)) {
					int y = getMaxYForPos(x, z);
					if (y == 0) {
						continue;
					}
					Coords coords = new Coords(x, y + 1, z);
					if (lastFlowerStates.containsKey(coords)) {
						borders.put(coords, lastFlowerStates.get(coords));
					} else {
						borders.put(coords,
								new BoundingBoxFlowerForest(coords, FlowerForestHelper.getFlowerAtPos(coords)));
					}
				}
			}
		}
		return borders;
	}

	private static int getMaxYForPos(int x, int z) {
		int y = 256;
		while (MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)) != Blocks.GRASS_BLOCK
				.getDefaultState() && y-- > 0)
			;

		return y;
	}

}
