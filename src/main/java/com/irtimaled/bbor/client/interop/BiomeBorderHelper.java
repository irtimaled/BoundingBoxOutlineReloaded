package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeBorderHelper {
    public static int getBiomeId(Coords coords) {
        return getBiomeId(coords.getX(), coords.getY(), coords.getZ());
    }

    public static int getBiomeId(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        ClientWorld world = Minecraft.getInstance().world;
        Biome biome = world.getBiome(pos);
        return world.func_241828_r().getRegistry(Registry.BIOME_KEY).getId(biome);
    }
}
