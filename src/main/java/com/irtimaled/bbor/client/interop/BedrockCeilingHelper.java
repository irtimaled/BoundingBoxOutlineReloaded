package com.irtimaled.bbor.client.interop;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;

import java.util.Random;

public class BedrockCeilingHelper {
    public static boolean isBedrock(int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = MinecraftClient.getInstance().world.getBlockState(pos);
        return blockState == Blocks.BEDROCK.getDefaultState();
    }

    public static boolean chunkLoaded(int chunkX, int chunkZ) {
        return MinecraftClient.getInstance().world.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }

    public static Random getRandomForChunk(int chunkX, int chunkZ) {
        ChunkRandom random = new ChunkRandom();
        random.setTerrainSeed(chunkX, chunkZ);
        return random;
    }
}
