package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.ReflectionHelper;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.HashSet;
import java.util.Set;

public class CommonProxy {
    protected DimensionCache dimensionCache = new DimensionCache();
    protected Set<VillageProcessor> villageProcessors = new HashSet<>();

    private IVillageEventHandler eventHandler = null;

    public void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            IChunkGenerator chunkGenerator = ReflectionHelper.getPrivateValue(ChunkProviderServer.class, (ChunkProviderServer) chunkProvider, IChunkGenerator.class);
            dimensionCache.setWorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ());
            DimensionType dimensionType = world.provider.getDimensionType();
            Logger.info("create world dimension: %s, %s (chunkprovider: %s) (seed: %d)", dimensionType, world.getClass().toString(), chunkGenerator.getClass().toString(), world.getSeed());
            DimensionProcessor boundingBoxCache = new DimensionProcessor(dimensionType, chunkGenerator);
            dimensionCache.put(dimensionType, boundingBoxCache);
            if (ConfigManager.drawVillages.getBoolean()) {
                villageProcessors.add(new VillageProcessor(world, dimensionType, eventHandler, boundingBoxCache));
            }
        }
    }

    public void chunkLoaded(Chunk chunk) {
        DimensionType dimensionType = chunk.getWorld().provider.getDimensionType();
        dimensionCache.refresh(dimensionType);
    }

    public void tick() {
        villageProcessors.forEach(VillageProcessor::process);
    }

    public void init() {
    }

    public void setEventHandler(IVillageEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public DimensionCache getDimensionCache() {
        return dimensionCache;
    }
}
