package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawningSphereHelper {
    public static void findSpawnableSpaces(Point center, Coords coords, int width, int height, BlockProcessor blockProcessor) {
        int blockX = coords.getX();
        int minX = blockX - width;
        int maxX = blockX + width + 1;

        int blockZ = coords.getZ();
        int minZ = blockZ - width;
        int maxZ = blockZ + width + 1;

        World world = MinecraftClient.getInstance().world;
        int blockY = coords.getY();
        int minY = Math.max(world.getBottomY() + 1, blockY - height);
        int maxY = Math.min(world.getTopY() - 1, blockY + height);

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                double closestX = x + 0.5D;
                double closestZ = z + 0.5D;
                double distance = center.getDistance(new Point(closestX, center.getY(), closestZ));
                if (distance > BoundingBoxSpawningSphere.SPAWN_RADIUS) continue;

                BlockState upperBlockState = world.getBlockState(new BlockPos(x, minY - 1, z));
                for (int y = minY; y < maxY; y++) {
                    BlockState spawnBlockState = upperBlockState;
                    BlockPos pos = new BlockPos(x, y, z);
                    upperBlockState = world.getBlockState(pos);
                    distance = center.getDistance(new Point(closestX, y, closestZ));
                    if (isWithinSpawnableZone(distance) &&
                            SpawnableBlocksHelper.isSpawnable(world, pos, spawnBlockState, upperBlockState)) {
                        blockProcessor.process(pos);
                    }
                }
            }
        }
    }

    private static boolean isWithinSpawnableZone(double distance) {
        return distance <= BoundingBoxSpawningSphere.SPAWN_RADIUS &&
                distance > BoundingBoxSpawningSphere.SAFE_RADIUS;
    }
}
