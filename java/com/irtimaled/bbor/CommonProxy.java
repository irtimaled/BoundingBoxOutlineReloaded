package com.irtimaled.bbor;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy implements IEventHandler {

    public Map<Integer, BoundingBoxCache> boundingBoxCacheMap = new ConcurrentHashMap<Integer, BoundingBoxCache>();

    public ConfigManager configManager;
    protected WorldData worldData;

    public void init(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            chunkProvider = ReflectionHelper.getPrivateValue(ChunkProviderServer.class, (ChunkProviderServer) chunkProvider, IChunkProvider.class);
            setWorldData(new WorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ()));
            int dimensionId = world.provider.getDimensionId();
            Logger.info("create world dimension: %d, %s (chunkprovider: %s) (seed: %d)", dimensionId, world.getClass().toString(), chunkProvider.getClass().toString(), worldData.getSeed());
            boundingBoxCacheMap.put(dimensionId, new DimensionProcessor(this, configManager, world, dimensionId, chunkProvider));
        }
    }

    public void chunkLoaded(Chunk chunk) {
        int dimensionId = chunk.getWorld().provider.getDimensionId();
        if (boundingBoxCacheMap.containsKey(dimensionId)) {
            boundingBoxCacheMap.get(dimensionId).refresh();
        }
    }

    public void boundingBoxRemoved(BoundingBox bb) {
    }

    public WorldData getWorldData() {
        return worldData;
    }

    public void setWorldData(WorldData worldData) {
        this.worldData = worldData;
    }
}
