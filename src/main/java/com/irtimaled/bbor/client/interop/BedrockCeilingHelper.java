package com.irtimaled.bbor.client.interop;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BedrockCeilingHelper {
    public static boolean isBedrock(int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = Minecraft.getInstance().world.getBlockState(pos);
        return blockState == Blocks.BEDROCK.getDefaultState();
    }

    public static boolean chunkLoaded(int chunkX, int chunkZ) {
        return Minecraft.getInstance().world.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    public static Random getRandomForChunk(int chunkX, int chunkZ) {
        SharedSeedRandom random = new SharedSeedRandom();
        random.setBaseChunkSeed(chunkX, chunkZ);
        return random;
    }
}
