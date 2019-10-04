package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.interop.BiomeBorderHelper;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.BoundingBoxBiomeBorder;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class BiomeBorderProvider implements IBoundingBoxProvider<BoundingBoxBiomeBorder> {
    private static Coords lastPlayerCoords = null;
    private static Boolean lastRenderAllTransitions = null;
    private static Integer lastRenderDistance = null;
    private static Map<Coords, BoundingBoxBiomeBorder> lastBorders = new HashMap<>();

    public Iterable<BoundingBoxBiomeBorder> get(int dimensionId) {
        if (!BoundingBoxType.BiomeBorder.shouldRender())
            return Iterators.empty();

        Coords playerCoords = PlayerCoords.get();
        Integer renderDistance = ConfigManager.biomeBordersRenderDistance.get();
        Boolean renderAllTransitions = !ConfigManager.renderOnlyCurrentBiome.get();
        if (!playerCoords.equals(lastPlayerCoords) || !renderDistance.equals(lastRenderDistance) || renderAllTransitions != lastRenderAllTransitions) {
            lastPlayerCoords = playerCoords;
            lastRenderDistance = renderDistance;
            lastRenderAllTransitions = renderAllTransitions;
            lastBorders = getBiomeBorders();
        }
        return lastBorders.values();
    }

    public static void clear() {
        lastBorders = new HashMap<>();
        lastPlayerCoords = null;
    }

    private Map<Coords, BoundingBoxBiomeBorder> getBiomeBorders() {
        Integer renderDistance = lastRenderDistance;
        Coords playerCoords = lastPlayerCoords;
        boolean renderAllTransitions = lastRenderAllTransitions;

        int width = MathHelper.floor(Math.pow(2, 3 + renderDistance));
        int blockX = playerCoords.getX();
        int minX = blockX - width;
        int maxX = blockX + width;

        int blockY = playerCoords.getY();

        int blockZ = playerCoords.getZ();
        int minZ = blockZ - width;
        int maxZ = blockZ + width;

        int size = (width * 2) + 1;
        int[][] biomeIds = new int[size][size];
        for (int x = minX; x <= maxX; x++) {
            int matchX = (x - minX);
            for (int z = minZ; z <= maxZ; z++) {
                int matchZ = (z - minZ);
                biomeIds[matchX][matchZ] = BiomeBorderHelper.getBiomeId(x, blockY, z);
            }
        }

        int playerBiomeId = BiomeBorderHelper.getBiomeId(playerCoords);

        Map<Coords, BoundingBoxBiomeBorder> borders = new HashMap<>();
        for (int matchX = 1; matchX < size - 2; matchX++) {
            for (int matchZ = 1; matchZ < size - 2; matchZ++) {
                int x = matchX + minX;
                int z = matchZ + minZ;
                int biomeId = biomeIds[matchX][matchZ];
                if (renderAllTransitions || biomeId == playerBiomeId) {
                    Coords coords = new Coords(x, blockY, z);
                    if (lastBorders.containsKey(coords)) {
                        borders.put(coords, lastBorders.get(coords));
                    } else {
                        boolean north = biomeIds[matchX][matchZ - 1] != biomeId;
                        boolean east = biomeIds[matchX + 1][matchZ] != biomeId;
                        boolean south = biomeIds[matchX][matchZ + 1] != biomeId;
                        boolean west = biomeIds[matchX - 1][matchZ] != biomeId;
                        if (north || east || south || west) {
                            borders.put(coords, BoundingBoxBiomeBorder.from(coords, north, east, south, west));
                        }
                    }
                }
            }
        }
        return borders;
    }
}
