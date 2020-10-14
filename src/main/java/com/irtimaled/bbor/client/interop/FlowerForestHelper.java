package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FlowerForestHelper {
    private static final Random random = new Random();

    private static final Map<BlockState, Setting<HexColor>> flowerColorMap = new HashMap<>();
    private static final FlowersFeature flowersFeature;
    private static final IFeatureConfig flowersConfig;

    public static final Biome BIOME = WorldGenRegistries.BIOME.getValueForKey(Biomes.FLOWER_FOREST);

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
        flowersFeature = (FlowersFeature) config.feature;
        flowersConfig = config.config;
    }

    public static Setting<HexColor> getFlowerColorAtPos(Coords coords) {
        int x = coords.getX();
        int z = coords.getZ();
        BlockState blockState = flowersFeature.getFlowerToPlace(random, new BlockPos(x, coords.getY(), z), flowersConfig);
        return flowerColorMap.get(blockState);
    }

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static boolean canGrowFlower(int x, int y, int z) {
        return Minecraft.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS_BLOCK;
    }
}
