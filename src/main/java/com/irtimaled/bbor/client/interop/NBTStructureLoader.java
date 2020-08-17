package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.ReflectionHelper;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionBasedStorage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

class NBTStructureLoader {
    private final DimensionId dimensionId;
    private final Set<String> loadedChunks = new HashSet<>();

    private FeatureUpdater legacyStructureDataUtil = null;
    private LevelStorage.Session saveHandler = null;
    private File chunkSaveLocation = null;
    private ChunkLoader chunkLoader;

    NBTStructureLoader(DimensionId dimensionId, LevelStorage.Session saveHandler, File worldDirectory) {
        this.dimensionId = dimensionId;
        this.configure(saveHandler, worldDirectory);
    }

    void clear() {
        this.legacyStructureDataUtil = null;
        this.saveHandler = null;
        this.chunkSaveLocation = null;
        this.loadedChunks.clear();

        if (this.chunkLoader == null) return;
        try {
            this.chunkLoader.close();
        } catch (IOException ignored) {
        }
        this.chunkLoader = null;
    }

    void configure(LevelStorage.Session saveHandler, File worldDirectory) {
        this.saveHandler = saveHandler;
        if (worldDirectory != null) {
            this.chunkSaveLocation = new File(DimensionType.getSaveDirectory(this.dimensionId.getDimensionType(), worldDirectory), "region");
            this.chunkLoader = new ChunkLoader(this.chunkSaveLocation);
        }
    }

    private FeatureUpdater getLegacyStructureDataUtil() {
        if (this.legacyStructureDataUtil == null) {
            File dataFolder = new File(this.saveHandler.getWorldDirectory(World.OVERWORLD), "data");
            this.legacyStructureDataUtil = FeatureUpdater.create(dimensionId.getDimensionType(),
                    new PersistentStateManager(dataFolder, MinecraftClient.getInstance().getDataFixer()));
        }
        return this.legacyStructureDataUtil;
    }

    private CompoundTag loadStructureStarts(int chunkX, int chunkZ) {
        try {
            CompoundTag compound = this.chunkLoader.readChunk(chunkX, chunkZ);
            if (compound == null) return null;
            int dataVersion = compound.contains("DataVersion", 99) ? compound.getInt("DataVersion") : -1;
            if (dataVersion < 1493) {
                if (compound.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                    compound = getLegacyStructureDataUtil().getUpdatedReferences(compound);
                }
            }
            return compound.getCompound("Level").getCompound("Structures").getCompound("Starts");
        } catch (IOException ignored) {
        }
        return null;
    }

    void loadStructures(int chunkX, int chunkZ) {
        if (saveHandler == null) return;

        if (!loadedChunks.add(String.format("%s,%s", chunkX, chunkZ))) return;

        CompoundTag structureStarts = loadStructureStarts(chunkX, chunkZ);
        if (structureStarts == null || structureStarts.getSize() == 0) return;

        Map<String, StructureStart<?>> structureStartMap = new HashMap<>();
        for (String key : structureStarts.getKeys()) {
            CompoundTag compound = structureStarts.getCompound(key);
            if (compound.contains("BB")) {
                structureStartMap.put(key, new SimpleStructureStart(compound));
            }
        }

        EventBus.publish(new StructuresLoaded(structureStartMap, dimensionId));
    }

    private static class SimpleStructureStart extends StructureStart<FeatureConfig> {
        SimpleStructureStart(CompoundTag compound) {
            super(null,
                    0,
                    0,
                    new BlockBox(compound.getIntArray("BB")),
                    0,
                    0);

            ListTag children = compound.getList("Children", 10);
            for (int index = 0; index < children.size(); ++index) {
                CompoundTag child = children.getCompound(index);
                if (child.contains("BB")) this.children.add(new SimpleStructurePiece(child));
            }
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, FeatureConfig featureConfig) {
        }
    }

    private static class SimpleStructurePiece extends StructurePiece {
        SimpleStructurePiece(CompoundTag compound) {
            super(null, compound);
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {

        }

        @Override
        public boolean generate(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos, BlockPos blockPos) {
            return false;
        }
    }

    private static class ChunkLoader {
        private static final BiFunction<File, Boolean, RegionBasedStorage> creator =
                ReflectionHelper.getPrivateInstanceBuilder(RegionBasedStorage.class, File.class, boolean.class);

        private final RegionBasedStorage regionFileCache;

        public ChunkLoader(File file) {
            this.regionFileCache = creator.apply(file, false);
        }

        public CompoundTag readChunk(int chunkX, int chunkZ) throws IOException {
            if (regionFileCache == null) return null;
            return regionFileCache.getTagAt(new ChunkPos(chunkX, chunkZ));
        }

        public void close() throws IOException {
            if (regionFileCache == null) return;
            regionFileCache.close();
        }
    }
}
