package com.irtimaled.bbor;

import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {

    public Map<Integer, BoundingBoxCache> boundingBoxCacheMap = new ConcurrentHashMap<Integer, BoundingBoxCache>();

    public ConfigManager configManager;
    protected SimpleNetworkWrapper network;

    public void init() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("bbor");
        network.registerMessage(BoundingBoxMessageHandler.class, BoundingBoxMessage.class, 0, Side.CLIENT);
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        IChunkProvider chunkProvider = event.world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            chunkProvider = ((ChunkProviderServer) chunkProvider).serverChunkGenerator;
            long seed = event.world.getSeed();
            int dimensionId = event.world.provider.getDimensionId();
            FMLLog.info("create world dimension: %d, %s (chunkprovider: %s) (seed: %d)", dimensionId, event.world.getClass().toString(), chunkProvider.getClass().toString(), seed);
            boundingBoxCacheMap.put(dimensionId, new DimensionProcessor(configManager, event.world, seed, dimensionId, chunkProvider));
        }
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        int dimensionId = event.world.provider.getDimensionId();
        if (boundingBoxCacheMap.containsKey(dimensionId)) {
            boundingBoxCacheMap.get(dimensionId).refresh();
        }
    }
}
