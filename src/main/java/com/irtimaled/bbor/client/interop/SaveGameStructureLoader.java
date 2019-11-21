package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SaveGameStructureLoader {
    private static final Map<Integer, NBTStructureLoader> nbtStructureLoaders = new HashMap<>();
    private static SaveHandler saveHandler = null;
    private static File worldDirectory = null;

    static void loadSaveGame(String fileName) {
        Minecraft minecraft = Minecraft.getInstance();
        SaveFormat saveLoader = minecraft.getSaveLoader();
        saveHandler = saveLoader.getSaveLoader(fileName, null);
        worldDirectory = saveLoader.func_215781_c().resolve(fileName).toFile();

        for (int dimensionId : nbtStructureLoaders.keySet()) {
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

    private static NBTStructureLoader getNBTStructureLoader(int dimensionId) {
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
