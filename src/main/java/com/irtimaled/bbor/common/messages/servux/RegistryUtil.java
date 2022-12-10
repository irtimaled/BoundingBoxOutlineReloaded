package com.irtimaled.bbor.common.messages.servux;

import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.WorldGenSettings;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistryUtil {
    static final DynamicRegistryManager.Immutable REGISTRY_MANAGER;

    static {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
            SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, DataConfiguration.SAFE_MODE, false, true);
            SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
            CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(
                    serverConfig,
                    context -> new SaveLoading.LoadContext<>(
                            new WorldCreationSettings(
                                    new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager())), context.dataConfiguration()
                            ),
                            context.dimensionsRegistryManager()
                    ),
                    new SaveLoading.SaveApplierFactory<WorldCreationSettings, GeneratorOptionsHolder>() { // DO NOT CONVERT THIS TO LAMBDA DUE TO FREEZES
                        @Override
                        public GeneratorOptionsHolder create(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, WorldCreationSettings generatorOptions) {
                            resourceManager.close();
                            return new GeneratorOptionsHolder(generatorOptions.worldGenSettings(), combinedDynamicRegistries, dataPackContents, generatorOptions.dataConfiguration());
                        }
                    },
                    Util.getMainWorkerExecutor(),
                    executorService
            );
            final GeneratorOptionsHolder holder = completableFuture.join();
            REGISTRY_MANAGER = holder.getCombinedRegistryManager();
        } finally {
            executorService.shutdown();
        }
    }

    record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
    }

    public static void init() {
    }

}
