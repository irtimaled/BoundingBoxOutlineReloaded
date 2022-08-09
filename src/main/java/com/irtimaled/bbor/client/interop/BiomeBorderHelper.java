package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.models.Coords;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;

public class BiomeBorderHelper {

    private static final Long2ObjectMap<Long2IntMap> biomeCache = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());

    public static void onChunkLoaded(int chunkX, int chunkZ) {

    }

    public static void onChunkUnload(int chunkX, int chunkZ) {
        biomeCache.remove(ChunkPos.toLong(chunkX, chunkZ));
    }

    public static void onDisconnect() {
        biomeCache.clear();
    }

    public static int getBiomeId(Coords coords) {
        return getBiomeId(coords.getX(), coords.getY(), coords.getZ());
    }

    public static int getBiomeId(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        final Long2IntMap biomeArray = biomeCache.computeIfAbsent(ChunkPos.toLong(pos), key -> createNewMap());
        final ClientWorld world = MinecraftClient.getInstance().world;
        return biomeArray.computeIfAbsent(pos.asLong(), key -> {
            assert world != null;
            return world.getRegistryManager().get(Registry.BIOME_KEY).getRawId(world.getBiome(pos).value());
        });
    }

    private static Long2IntMap createNewMap() {
        final Long2IntOpenHashMap long2IntOpenHashMap = new Long2IntOpenHashMap();
        long2IntOpenHashMap.defaultReturnValue(-1);
        return Long2IntMaps.synchronize(long2IntOpenHashMap);
    }
}
