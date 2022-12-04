package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowerForestHelper {

    private static final Map<BlockState, Setting<HexColor>> flowerColorMap = new HashMap<>();

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

        // TODO [VanillaCopy] net.minecraft.world.gen.feature.VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST
        final PlacedFeature placedFeature = new RandomPatchFeatureConfig(
                96,
                6,
                2,
                PlacedFeatures.createEntry(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(
                                new NoiseBlockStateProvider(
                                        2345L,
                                        new DoublePerlinNoiseSampler.NoiseParameters(0, 1.0),
                                        0.020833334F,
                                        List.of(
                                                Blocks.DANDELION.getDefaultState(),
                                                Blocks.POPPY.getDefaultState(),
                                                Blocks.ALLIUM.getDefaultState(),
                                                Blocks.AZURE_BLUET.getDefaultState(),
                                                Blocks.RED_TULIP.getDefaultState(),
                                                Blocks.ORANGE_TULIP.getDefaultState(),
                                                Blocks.WHITE_TULIP.getDefaultState(),
                                                Blocks.PINK_TULIP.getDefaultState(),
                                                Blocks.OXEYE_DAISY.getDefaultState(),
                                                Blocks.CORNFLOWER.getDefaultState(),
                                                Blocks.LILY_OF_THE_VALLEY.getDefaultState()
                                        )
                                )
                        )
                )
        ).feature().value();
        final var configuredFeature = (ConfiguredFeature<SimpleBlockFeatureConfig, SimpleBlockFeature>) placedFeature.feature().value();
        blockStateProvider = configuredFeature.config().toPlace();
    }

    public static Setting<HexColor> getFlowerColorAtPos(Coords coords) {
        int x = coords.getX();
        int z = coords.getZ();
        BlockState blockState = blockStateProvider.get(new LocalRandom(seed), new BlockPos(x, coords.getY(), z));
        return flowerColorMap.get(blockState);
    }

    public static boolean canGrowFlower(int x, int y, int z) {
        return MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS_BLOCK;
    }
}
