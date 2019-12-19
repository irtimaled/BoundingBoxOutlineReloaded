package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class SpawningSphereHelper {
    private static EntityType entityType = EntityType.Builder.create(EntityCategory.MONSTER).setDimensions(0f, 0f).disableSaving().build(null);

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
        ClientWorld world = MinecraftClient.getInstance().world;
        BlockPos pos = new BlockPos(x, y, z);
        Biome biome = world.getBiomeAccess().getBiome(pos);
        return  biome.getMaxSpawnLimit() > 0 &&
                !biome.getEntitySpawnList(EntityCategory.MONSTER).isEmpty() &&
                isSpawnable(pos, world);
    }

    private static boolean isSpawnable(BlockPos pos, ClientWorld world) {
        BlockPos down = new BlockPos(pos.down());
        BlockState spawnBlockState = world.getBlockState(down);
        BlockState upperBlockState = world.getBlockState(pos);
        VoxelShape collisionShape = upperBlockState.getCollisionShape(world, pos);

        boolean isNether = world.dimension.isNether();
        return spawnBlockState.allowsSpawning(world, down, isNether ? EntityType.ZOMBIE_PIGMAN : entityType) &&
                !Block.isFaceFullSquare(collisionShape, Direction.UP) &&
                !upperBlockState.emitsRedstonePower() &&
                !upperBlockState.matches(BlockTags.RAILS) &&
                collisionShape.getMaximum(Direction.Axis.Y) <= 0 &&
                upperBlockState.getFluidState().isEmpty() &&
                (isNether || world.getLightLevel(LightType.BLOCK, pos) <= 7);
    }
}
