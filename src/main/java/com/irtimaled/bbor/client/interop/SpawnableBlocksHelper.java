package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SpawnableBlocksHelper {
    public static void findSpawnableBlocks(Coords coords, int width, int height, BlockProcessor blockProcessor) {
        int blockX = coords.getX();
        int minX = blockX - width;
        int maxX = blockX + width + 1;

        int blockZ = coords.getZ();
        int minZ = blockZ - width;
        int maxZ = blockZ + width + 1;

        int blockY = coords.getY();
        int minY = Math.max(1, blockY - height);
        int maxY = Math.min(255, blockY + height);

        World world = Minecraft.getInstance().world;
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                if (isBiomeHostileSpawnProof(world, new BlockPos(x, 1, z))) continue;

                IBlockState upperBlockState = world.getBlockState(new BlockPos(x, minY - 1, z));
                for (int y = Math.max(1, minY); y < maxY; y++) {
                    IBlockState spawnBlockState = upperBlockState;
                    BlockPos pos = new BlockPos(x, y, z);
                    upperBlockState = world.getBlockState(pos);
                    if (isSpawnable(world, pos, spawnBlockState, upperBlockState)) {
                        blockProcessor.process(x, y, z);
                    }
                }
            }
        }
    }

    static boolean isBiomeHostileSpawnProof(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        return biome.getSpawningChance() == 0 ||
                biome.getSpawns(EnumCreatureType.MONSTER).isEmpty();
    }

    static boolean isSpawnable(World world, BlockPos pos, IBlockState spawnBlockState, IBlockState upperBlockState) {
        Block spawnBlock = spawnBlockState.getBlock();
        return spawnBlock != Blocks.AIR &&
                spawnBlock != Blocks.BEDROCK &&
                spawnBlock != Blocks.BARRIER &&
                spawnBlockState.isTopSolid() &&
                !upperBlockState.isBlockNormalCube() &&
                !upperBlockState.canProvidePower() &&
                !upperBlockState.isIn(BlockTags.RAILS) &&
                upperBlockState.getCollisionShape(world, pos).getEnd(EnumFacing.Axis.Y) <= 0 &&
                upperBlockState.getFluidState().isEmpty() &&
                (world.dimension.isNether() || world.getLightFor(EnumLightType.BLOCK, pos) <= 7);
    }
}
