package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FlowerForestHelper {
    private static final Random random = new Random();

    private static final Map<BlockState, Setting<HexColor>> flowerColorMap = new HashMap<>();
    private static final FlowerFeature flowersFeature;
    private static final FeatureConfig flowersConfig;

    public static final Biome BIOME = BuiltinRegistries.BIOME.get(BiomeKeys.FLOWER_FOREST);

    static {
        flowerColorMap.put(Blocks.DANDELION.getDefaultState(), ConfigManager.colorFlowerForestDandelion);
        flowerColorMap.put(Blocks.POPPY.getDefaultState(), ConfigManager.colorFlowerForestPoppy);
        flowerColorMap.put(Blocks.ALLIUM.getDefaultState(), ConfigManager.colorFlowerForestAllium);
        flowerColorMap.put(Blocks.AZURE_BLUET.getDefaultState(), ConfigManager.colorFlowerForestAzureBluet);
        flowerColorMap.put(Blocks.RED_TULIP.getDefaultState(), ConfigManager.colorFlowerForestRedTulip);
        flowerColorMap.put(Blocks.ORANGE_TULIP.getDefaultState(), ConfigManager.colorFlowerForestOrangeTulip);
        flowerColorMap.put(Blocks.WHITE_TULIP.getDefaultState(), ConfigManager.colorFlowerForestWhiteTulip);
        flowerColorMap.put(Blocks.PINK_TULIP.getDefaultState(), ConfigManager.colorFlowerForestPinkTulip);
        flowerColorMap.put(Blocks.OXEYE_DAISY.getDefaultState(), ConfigManager.colorFlowerForestOxeyeDaisy);
        flowerColorMap.put(Blocks.CORNFLOWER.getDefaultState(), ConfigManager.colorFlowerForestCornflower);
        flowerColorMap.put(Blocks.LILY_OF_THE_VALLEY.getDefaultState(), ConfigManager.colorFlowerForestLilyOfTheValley);
        ConfiguredFeature<?, ?> config = BIOME.getGenerationSettings().getFlowerFeatures().get(0);
        flowersFeature = (FlowerFeature) config.feature;
        flowersConfig = config.config;
    }

    public static Setting<HexColor> getFlowerColorAtPos(Coords coords) {
        int x = coords.getX();
        int z = coords.getZ();
        BlockState blockState = flowersFeature.getFlowerState(random, new BlockPos(x, coords.getY(), z), flowersConfig);
        return flowerColorMap.get(blockState);
    }

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static boolean canGrowFlower(int x, int y, int z) {
        return MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS_BLOCK;
    }
}
