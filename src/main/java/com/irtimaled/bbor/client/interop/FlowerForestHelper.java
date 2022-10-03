package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.HashMap;
import java.util.Map;

public class FlowerForestHelper {

    private static final Map<BlockState, Setting<HexColor>> flowerColorMap = new HashMap<>();

    public static final Biome BIOME = BuiltinRegistries.BIOME.get(BiomeKeys.FLOWER_FOREST);

    private static BlockStateProvider blockStateProvider;

    private static volatile long seed = 0;

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
        final PlacedFeature placedFeature = VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST.value().config().feature().value();
        final var configuredFeature = (ConfiguredFeature<SimpleBlockFeatureConfig, SimpleBlockFeature>) placedFeature.feature().value();
        blockStateProvider = configuredFeature.config().toPlace();
    }

    public static Setting<HexColor> getFlowerColorAtPos(Coords coords) {
        int x = coords.getX();
        int z = coords.getZ();
        BlockState blockState = blockStateProvider.getBlockState(new LocalRandom(seed), new BlockPos(x, coords.getY(), z));
        return flowerColorMap.get(blockState);
    }

    public static boolean canGrowFlower(int x, int y, int z) {
        return MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS_BLOCK;
    }
}
