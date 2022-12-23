package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.providers.BiomeBorderProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public class BiomeBorderHelper {

    static {
        EventBus.subscribe(ClientWorldUpdateTracker.WorldResetEvent.class, worldResetEvent -> onDisconnect());
    }

    private static final Long2ObjectMap<Long2IntMap> biomeCache = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());

    public static void onChunkLoaded(int chunkX, int chunkZ) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        for(int i = world.getBottomSectionCoord(); i < world.getTopSectionCoord(); ++i) {
            BiomeBorderProvider.refreshIfNeeded(ChunkSectionPos.from(chunkX, i, chunkZ));
        }
    }

    public static void onChunkUnload(int chunkX, int chunkZ) {
    }

    public static void onDisconnect() {
    }

    public static int getBiomeId(Coords coords) {
        return getBiomeId(coords.getX(), coords.getY(), coords.getZ());
    }

    public static int getBiomeId(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
//        final Long2IntMap biomeArray = biomeCache.computeIfAbsent(ChunkPos.toLong(pos), key -> createNewMap());
        final ClientWorld world = MinecraftClient.getInstance().world;
//        final Chunk chunk = world.getChunk(pos);
//        if (chunk instanceof EmptyChunk) {
//            throw new IllegalStateException("Chunk not loaded");
//        }
////        if (true) {
////            return world.getRegistryManager().get(Registry.BIOME_KEY).getRawId(world.getBiome(pos).value());
////        }
//        final RegistryEntry<Biome> biome = world.getBiomeAccess().withSource(chunk).getBiome(pos);
//        return world.getRegistryManager().get(RegistryKeys.BIOME).getRawId(biome.value());
        return world.getRegistryManager().get(RegistryKeys.BIOME).getRawId(world.getBiome(pos).value());
    }

    private static Long2IntMap createNewMap() {
        final Long2IntOpenHashMap long2IntOpenHashMap = new Long2IntOpenHashMap(256);
        long2IntOpenHashMap.defaultReturnValue(-1);
        return Long2IntMaps.synchronize(long2IntOpenHashMap);
    }
}
