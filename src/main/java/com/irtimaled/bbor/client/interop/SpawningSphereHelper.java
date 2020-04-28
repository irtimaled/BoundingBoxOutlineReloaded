package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class SpawningSphereHelper {
    private static EntityType entityType = EntityType.Builder.create(EntityClassification.MONSTER).size(0f, 0f).disableSerialization().build(null);
    
    public static int findSpawnableSpaces(Point center, Coords coords, int width, int height, BlockProcessor blockProcessor) {
        ClientWorld world = Minecraft.getInstance().world;
        
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
                if(!isWithinSpawnSphere(x, (int) Math.round(center.getY()), z, center)) continue;// Doesn't check the biome if it's out of the sphere anyway
                if(!isBiomeHostileSpawnable(world, new BlockPos(x, 1, z))) continue;
                for (int y = minY; y < maxY; y++) {
                    if (isWithinSpawnSphere(x, y, z, center) && isSpawnable(new BlockPos(x, y, z), world) && blockProcessor.process(x, y, z)) {
                        processed++;
                    }
                }
            }
        }
        return processed;
    }

    private static boolean isBiomeHostileSpawnable(ClientWorld world, BlockPos pos){
        Biome biome = world.func_225523_d_().func_226836_a_(pos);
        
        boolean spawningChanceNotZero = biome.getSpawningChance() > 0;
        if(!spawningChanceNotZero) return false;
        
        return !biome.getSpawns(EntityClassification.MONSTER).isEmpty();
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

    private static boolean isSpawnable(BlockPos pos, ClientWorld world) {
        BlockPos down = pos.down();
        BlockState spawnBlockState = world.getBlockState(down);
        BlockState upperBlockState = world.getBlockState(pos);
        VoxelShape collisionShape = upperBlockState.getCollisionShape(world, pos);

        boolean isNether = world.dimension.isNether();
        return spawnBlockState.canEntitySpawn(world, down, isNether ? EntityType.ZOMBIE_PIGMAN : entityType) &&
                !Block.doesSideFillSquare(collisionShape, Direction.UP) &&
                !upperBlockState.canProvidePower() &&
                !upperBlockState.isIn(BlockTags.RAILS) &&
                collisionShape.getEnd(Direction.Axis.Y) <= 0 &&
                upperBlockState.getFluidState().isEmpty() &&
                (isNether || world.func_226658_a_(LightType.BLOCK, pos) <= 7);
    }
}
