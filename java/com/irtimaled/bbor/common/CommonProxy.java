package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.ReflectionHelper;
import com.irtimaled.bbor.common.models.WorldData;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    public Map<DimensionType, BoundingBoxCache> boundingBoxCacheMap = new ConcurrentHashMap<>();

    public ConfigManager configManager;
    protected WorldData worldData;
    private IEventHandler eventHandler = null;

    public void init(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            IChunkGenerator chunkGenerator = ReflectionHelper.getPrivateValue(ChunkProviderServer.class, (ChunkProviderServer) chunkProvider, IChunkGenerator.class);
            setWorldData(new WorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ()));
            DimensionType dimensionType = world.provider.getDimensionType();
            Logger.info("create world dimension: %s, %s (chunkprovider: %s) (seed: %d)", dimensionType, world.getClass().toString(), chunkGenerator.getClass().toString(), worldData.getSeed());
            boundingBoxCacheMap.put(dimensionType, new DimensionProcessor(eventHandler, configManager, world, dimensionType, chunkGenerator));
        }
    }

    public void chunkLoaded(Chunk chunk) {
        DimensionType dimensionType = chunk.getWorld().provider.getDimensionType();
        if (boundingBoxCacheMap.containsKey(dimensionType)) {
            boundingBoxCacheMap.get(dimensionType).refresh();
        }
    }

    public WorldData getWorldData() {
        return worldData;
    }

    public void setWorldData(WorldData worldData) {
        this.worldData = worldData;
    }

    public void setEventHandler(IEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}
