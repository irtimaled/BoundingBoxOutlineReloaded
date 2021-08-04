package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.models.Coords;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.WorldChunk;

public class BiomeBorderHelper {

    private static final Long2ObjectOpenHashMap<BiomeArray> biomeCache = new Long2ObjectOpenHashMap<>();

    public static void onChunkLoaded(int chunkX, int chunkZ) {
        assert MinecraftClient.getInstance().world != null;
        final WorldChunk chunk = MinecraftClient.getInstance().world.getChunk(chunkX, chunkZ);
        if (chunk == null) return;
        biomeCache.put(ChunkPos.toLong(chunkX, chunkZ), chunk.getBiomeArray());
    }

    public static void onChunkUnload(int chunkX, int chunkZ) {
        biomeCache.remove(ChunkPos.toLong(chunkX, chunkZ));
    }

    public static int getBiomeId(Coords coords) {
        return getBiomeId(coords.getX(), coords.getY(), coords.getZ());
    }

    public static int getBiomeId(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        final BiomeArray biomeArray = biomeCache.get(ChunkPos.toLong(pos));
        final ClientWorld world = MinecraftClient.getInstance().world;
        final Biome biome;
        if (biomeArray != null) {
            biome = biomeArray.getBiomeForNoiseGen(BiomeCoords.fromBlock(x & 15), y, BiomeCoords.fromBlock(z & 15));
        } else {
            assert world != null;
            biome = world.getBiome(pos);
        }
        return world.getRegistryManager().get(Registry.BIOME_KEY).getRawId(biome);
    }
}
