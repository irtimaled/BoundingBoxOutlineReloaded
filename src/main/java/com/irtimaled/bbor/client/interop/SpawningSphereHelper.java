package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.Point;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawningSphereHelper {
    public static int findSpawnableSpaces(Point center, Coords coords, int width, int height, BlockProcessor blockProcessor) {
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
        int processed = 0;
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                double closestX = x + 0.5D;
                double closestZ = z + 0.5D;
                double distance = center.getDistance(new Point(closestX, center.getY(), closestZ));
                if (distance > BoundingBoxSpawningSphere.SPAWN_RADIUS) continue;

                if (SpawnableBlocksHelper.isBiomeHostileSpawnProof(world, new BlockPos(x, 1, z))) continue;

                IBlockState upperBlockState = world.getBlockState(new BlockPos(x, minY - 1, z));
                for (int y = minY; y < maxY; y++) {
                    IBlockState spawnBlockState = upperBlockState;
                    BlockPos pos = new BlockPos(x, y, z);
                    upperBlockState = world.getBlockState(pos);
                    distance = center.getDistance(new Point(closestX, y, closestZ));
                    if (isWithinSpawnableZone(distance) &&
                            SpawnableBlocksHelper.isSpawnable(world, pos, spawnBlockState, upperBlockState) &&
                            blockProcessor.process(x, y, z)) {
                        processed++;
                    }
                }
            }
        }
        return processed;
    }

    private static boolean isWithinSpawnableZone(double distance) {
        return distance <= BoundingBoxSpawningSphere.SPAWN_RADIUS &&
                distance > BoundingBoxSpawningSphere.SAFE_RADIUS;
    }
}
