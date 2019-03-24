package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.biome.Biome;

public class SpawningSphereHelper {
    public static int findSpawnableSpaces(Point center, Coords coords, int width, int height, BlockProcessor blockProcessor) {
        int blockX = coords.getX();
        int minX = blockX - width;
        int maxX = blockX + width;

        int blockZ = coords.getZ();
        int minZ = blockZ - width;
        int maxZ = blockZ + width;

        int blockY = coords.getY();
        int minY = blockY - height;
        if (minY < 1) minY = 1;
        int maxY = blockY + height;
        if (maxY > 255) maxY = 255;

        int processed = 0;
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (isWithinSpawnSphere(x, y, z, center) && isSpawnable(x, y, z) && blockProcessor.process(x, y, z)) {
                        processed++;
                    }
                }
            }
        }
        return processed;
    }

    private static boolean isWithinSpawnSphere(int x, int y, int z, Point center) {
        int x1 = x+1;
        int z1 = z+1;
        int y1 = y+1;
        int closestX = Math.abs(center.getX()-x) < Math.abs(center.getX()-x1) ? x : x1;
        int closestY = Math.abs(center.getY()-y) < Math.abs(center.getY()-y1) ? y : y1;
        int closestZ = Math.abs(center.getZ()-z) < Math.abs(center.getZ()-z1) ? z : z1;
        double distance = center.getDistance(new Point(closestX, closestY, closestZ));
        return distance <= BoundingBoxSpawningSphere.SPAWN_RADIUS && distance >= (BoundingBoxSpawningSphere.SAFE_RADIUS-1);
    }

    private static boolean isSpawnable(int x, int y, int z) {
        WorldClient world = Minecraft.getInstance().world;
        BlockPos pos = new BlockPos(x, y, z);
        Biome biome = world.getBiome(pos);
        return  biome.getSpawningChance() > 0 &&
                !biome.getSpawns(EnumCreatureType.MONSTER).isEmpty() &&
                isSpawnable(pos, world);
    }

    private static boolean isSpawnable(BlockPos pos, WorldClient world) {
        IBlockState spawnBlockState = world.getBlockState(pos.down());
        Block spawnBlock = spawnBlockState.getBlock();
        IBlockState upperBlockState = world.getBlockState(pos);

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
