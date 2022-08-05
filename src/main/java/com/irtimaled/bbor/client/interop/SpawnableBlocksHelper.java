package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnableBlocksHelper {
//    private static final EntityType entityType = EntityType.Builder.create(SpawnGroup.MONSTER)
//            .setDimensions(0f, 0f).disableSaving().build(null);

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

        World world = MinecraftClient.getInstance().world;
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                BlockState upperBlockState = world.getBlockState(new BlockPos(x, minY - 1, z));
                for (int y = Math.max(1, minY); y < maxY; y++) {
                    BlockState spawnBlockState = upperBlockState;
                    BlockPos pos = new BlockPos(x, y, z);
                    upperBlockState = world.getBlockState(pos);
                    if (isSpawnable(world, pos, spawnBlockState, upperBlockState)) {
                        blockProcessor.process(pos);
                    }
                }
            }
        }
    }

    private static final ConcurrentHashMap<Biome, Boolean> isNether = new ConcurrentHashMap<>();

    static boolean isSpawnable(World world, BlockPos pos, BlockState spawnBlockState, BlockState upperBlockState) {
        VoxelShape collisionShape = upperBlockState.getCollisionShape(world, pos);
        Biome biome = world.getBiome(pos).value();
        final boolean isNether = SpawnableBlocksHelper.isNether.computeIfAbsent(biome, biome1 -> {
            final SpawnSettings spawnSettings = biome.getSpawnSettings();
            final List<SpawnSettings.SpawnEntry> entries = spawnSettings.getSpawnEntries(SpawnGroup.MONSTER).getEntries();
            for (SpawnSettings.SpawnEntry entry : entries) {
                if (entry.type.getBaseClass() == ZombifiedPiglinEntity.class) return true;
            }
            return false;
        });
        final Identifier id = BuiltinRegistries.BIOME.getId(biome);
        return (id == null || !id.equals(new Identifier("minecraft", "mushroom_fields"))) &&
                spawnBlockState.allowsSpawning(world, pos.down(), isNether ? EntityType.ZOMBIFIED_PIGLIN : EntityType.ZOMBIE) &&
                !Block.isFaceFullSquare(collisionShape, Direction.UP) &&
                !upperBlockState.emitsRedstonePower() &&
                !upperBlockState.isIn(BlockTags.RAILS) &&
                collisionShape.getMax(Direction.Axis.Y) <= 0 &&
                upperBlockState.getFluidState().isEmpty() &&
                (isNether || world.getLightLevel(LightType.BLOCK, pos) <= 0);
    }
}
