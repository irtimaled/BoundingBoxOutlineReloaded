package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.HashSet;
import java.util.Set;

public class CommonProxy {
    protected DimensionCache dimensionCache;
    protected Set<VillageProcessor> villageProcessors = new HashSet<>();

    private IVillageEventHandler eventHandler = null;

    public void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            dimensionCache.setWorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ());
            DimensionType dimensionType = world.dimension.getType();
            Logger.info("create world dimension: %s, %s (seed: %d)", dimensionType, world.getClass().toString(), world.getSeed());
            DimensionProcessor boundingBoxCache = new DimensionProcessor(dimensionType);
            dimensionCache.put(dimensionType, boundingBoxCache);
            if (ConfigManager.drawVillages.getBoolean()) {
                villageProcessors.add(new VillageProcessor(world, dimensionType, eventHandler, boundingBoxCache));
            }
        }
    }

    public void chunkLoaded(Chunk chunk) {
        DimensionType dimensionType = chunk.getWorld().dimension.getType();
        BoundingBoxCache cache = dimensionCache.get(dimensionType);
        if(cache instanceof DimensionProcessor) {
            ((DimensionProcessor)cache).processChunk(chunk);
        }
    }

    public void tick() {
        villageProcessors.forEach(VillageProcessor::process);
    }

    public void init() {
        dimensionCache = new DimensionCache();
    }

    public void setEventHandler(IVillageEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public DimensionCache getDimensionCache() {
        return dimensionCache;
    }
}
