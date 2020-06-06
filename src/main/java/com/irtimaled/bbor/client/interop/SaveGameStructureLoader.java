package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveGameStructureLoader {
    private static final Map<DimensionId, NBTStructureLoader> nbtStructureLoaders = new HashMap<>();
    private static LevelStorage.Session saveHandler = null;
    private static File worldDirectory = null;

    static void loadSaveGame(String fileName) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        LevelStorage saveLoader = minecraft.getLevelStorage();
        try {
            saveHandler = saveLoader.createSession(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        worldDirectory = saveLoader.getSavesDirectory().resolve(fileName).toFile();

        for (DimensionId dimensionId : nbtStructureLoaders.keySet()) {
            NBTStructureLoader dimensionProcessor = getNBTStructureLoader(dimensionId);
            dimensionProcessor.configure(saveHandler, worldDirectory);
        }

        loadChunksAroundPlayer();
    }

    private static void loadChunksAroundPlayer() {
        NBTStructureLoader dimensionProcessor = getNBTStructureLoader(Player.getDimensionId());
        int renderDistance = ClientInterop.getRenderDistanceChunks();

        int playerChunkX = (int) Player.getX() >> 4;
        int minChunkX = playerChunkX - renderDistance;
        int maxChunkX = playerChunkX + renderDistance;

        int playerChunkZ = (int) Player.getZ() >> 4;
        int minChunkZ = playerChunkZ - renderDistance;
        int maxChunkZ = playerChunkZ + renderDistance;

        for (int chunkX = minChunkX; chunkX < maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ < maxChunkZ; chunkZ++) {
                dimensionProcessor.loadStructures(chunkX, chunkZ);
            }
        }
    }

    static void loadStructures(int chunkX, int chunkZ) {
        NBTStructureLoader dimensionProcessor = getNBTStructureLoader(Player.getDimensionId());
        dimensionProcessor.loadStructures(chunkX, chunkZ);
    }

    private static NBTStructureLoader getNBTStructureLoader(DimensionId dimensionId) {
        return nbtStructureLoaders.computeIfAbsent(dimensionId,
                id -> new NBTStructureLoader(id, saveHandler, worldDirectory));
    }

    public static void clear() {
        nbtStructureLoaders.values().forEach(NBTStructureLoader::clear);
        nbtStructureLoaders.clear();
        saveHandler = null;
        worldDirectory = null;
    }
}
