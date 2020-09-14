package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.BiomeBorderHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBiomeBorder;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.HashMap;
import java.util.Map;

public class BiomeBorderProvider implements IBoundingBoxProvider<BoundingBoxBiomeBorder>, ICachingProvider {
    private static Coords lastPlayerCoords = null;
    private static Boolean lastRenderAllTransitions = null;
    private static Integer lastRenderDistance = null;
    private static Integer lastMaxY = null;
    private static Map<Coords, BoundingBoxBiomeBorder> lastBorders = new HashMap<>();

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.BiomeBorder);
    }

    @Override
    public Iterable<BoundingBoxBiomeBorder> get(DimensionId dimensionId) {
        Coords playerCoords = Player.getCoords();
        Integer renderDistance = ConfigManager.biomeBordersRenderDistance.get();
        Boolean renderAllTransitions = !ConfigManager.renderOnlyCurrentBiome.get();
        Integer maxY = (int) Player.getMaxY(ConfigManager.biomeBordersMaxY.get());
        if (!playerCoords.equals(lastPlayerCoords) ||
                !renderDistance.equals(lastRenderDistance) ||
                renderAllTransitions != lastRenderAllTransitions ||
                !maxY.equals(lastMaxY)) {
            lastPlayerCoords = playerCoords;
            lastRenderDistance = renderDistance;
            lastRenderAllTransitions = renderAllTransitions;
            lastMaxY = maxY;
            lastBorders = getBiomeBorders();
        }
        return lastBorders.values();
    }

    public void clearCache() {
        lastBorders = new HashMap<>();
        lastPlayerCoords = null;
    }

    private Map<Coords, BoundingBoxBiomeBorder> getBiomeBorders() {
        int renderDistance = lastRenderDistance;
        Coords playerCoords = lastPlayerCoords;
        boolean renderAllTransitions = lastRenderAllTransitions;
        int maxY = lastMaxY;

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
                biomeIds[matchX][matchZ] = BiomeBorderHelper.getBiomeId(x, maxY, z);
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
                    Coords coords = new Coords(x, maxY, z);
                    if (lastBorders.containsKey(coords)) {
                        borders.put(coords, lastBorders.get(coords));
                    } else {
                        boolean north = biomeIds[matchX][matchZ - 1] != biomeId;
                        boolean east = biomeIds[matchX + 1][matchZ] != biomeId;
                        boolean south = biomeIds[matchX][matchZ + 1] != biomeId;
                        boolean west = biomeIds[matchX - 1][matchZ] != biomeId;
                        if (north || east || south || west) {
                            borders.put(coords, new BoundingBoxBiomeBorder(coords, north, east, south, west));
                        }
                    }
                }
            }
        }
        return borders;
    }
}
