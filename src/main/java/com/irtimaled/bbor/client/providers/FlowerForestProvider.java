package com.irtimaled.bbor.client.providers;

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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

import java.util.HashMap;
import java.util.Map;

public class FlowerForestProvider implements IBoundingBoxProvider<BoundingBoxFlowerForest> {
    public static final int FLOWER_FOREST_BIOME_ID = Registry.BIOME.getId(Biomes.FLOWER_FOREST);
    private static Coords lastPlayerCoords = null;
    private static Integer lastRenderDistance = null;
    private static Map<Coords, BoundingBoxFlowerForest> lastBoundingBoxes = new HashMap<>();

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.FlowerForest);
    }

    @Override
    public Iterable<BoundingBoxFlowerForest> get(DimensionId dimensionId) {
        Coords playerCoords = Player.getCoords();
        Integer renderDistance = ConfigManager.flowerForestsRenderDistance.get();
        if (!playerCoords.equals(lastPlayerCoords) || !renderDistance.equals(lastRenderDistance)) {
            lastPlayerCoords = playerCoords;
            lastRenderDistance = renderDistance;
            lastBoundingBoxes = getBoundingBoxes();
        }
        return lastBoundingBoxes.values();
    }

    public static void clear() {
        lastBoundingBoxes = new HashMap<>();
        lastPlayerCoords = null;
    }

    private Map<Coords, BoundingBoxFlowerForest> getBoundingBoxes() {
        int renderDistance = lastRenderDistance;
        Coords playerCoords = lastPlayerCoords;

        int width = MathHelper.floor(Math.pow(2, 2 + renderDistance));

        int blockX = playerCoords.getX();
        int minX = blockX - width;
        int maxX = blockX + width;

        int blockZ = playerCoords.getZ();
        int minZ = blockZ - width;
        int maxZ = blockZ + width;

        Map<Coords, BoundingBoxFlowerForest> boundingBoxes = new HashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int biomeId = BiomeBorderHelper.getBiomeId(x, 255, z);
                if (biomeId == FLOWER_FOREST_BIOME_ID) {
                    int y = getMaxYForPos(x, playerCoords.getY() + 1, z);
                    if (y == 0) {
                        continue;
                    }
                    Coords coords = new Coords(x, y + 1, z);
                    BoundingBoxFlowerForest boundingBox = lastBoundingBoxes.containsKey(coords)
                            ? lastBoundingBoxes.get(coords)
                            : new BoundingBoxFlowerForest(coords, FlowerForestHelper.getFlowerColorAtPos(coords));
                    boundingBoxes.put(coords, boundingBox);
                }
            }
        }
        return boundingBoxes;
    }

    private static int getMaxYForPos(int x, int y, int z) {
        while (y > 0) {
            if (FlowerForestHelper.canGrowFlower(x, y, z)) return y;
            y--;
        }
        return 0;
    }
}
