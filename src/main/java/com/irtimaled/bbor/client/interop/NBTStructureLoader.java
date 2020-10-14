package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.ReflectionHelper;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

class NBTStructureLoader {
    private final DimensionId dimensionId;
    private final Set<String> loadedChunks = new HashSet<>();

    private LegacyStructureDataUtil legacyStructureDataUtil = null;
    private SaveFormat.LevelSave saveHandler = null;
    private File chunkSaveLocation = null;
    private ChunkLoader chunkLoader;

    NBTStructureLoader(DimensionId dimensionId, SaveFormat.LevelSave saveHandler, File worldDirectory) {
        this.dimensionId = dimensionId;
        this.configure(saveHandler, worldDirectory);
    }

    void clear() {
        this.legacyStructureDataUtil = null;
        this.chunkSaveLocation = null;
        this.loadedChunks.clear();
        close(this.saveHandler, this.chunkLoader);
        this.saveHandler = null;
        this.chunkLoader = null;
    }

    private void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) continue;
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    void configure(SaveFormat.LevelSave saveHandler, File worldDirectory) {
        this.saveHandler = saveHandler;
        if (worldDirectory != null) {
            this.chunkSaveLocation = new File(DimensionType.getDimensionFolder(dimensionId.getDimensionType(), worldDirectory), "region");
            this.chunkLoader = new ChunkLoader(this.chunkSaveLocation);
        }
    }

    private LegacyStructureDataUtil getLegacyStructureDataUtil() {
        if (this.legacyStructureDataUtil == null) {
            File dataFolder = new File(this.saveHandler.getDimensionFolder(World.OVERWORLD), "data");
            this.legacyStructureDataUtil = LegacyStructureDataUtil.func_236992_a_(dimensionId.getDimensionType(),
                    new DimensionSavedDataManager(dataFolder, Minecraft.getInstance().getDataFixer()));
        }
        return this.legacyStructureDataUtil;
    }

    private CompoundNBT loadStructureStarts(int chunkX, int chunkZ) {
        try {
            CompoundNBT compound = this.chunkLoader.readChunk(chunkX, chunkZ);
            if (compound == null) return null;
            int dataVersion = compound.contains("DataVersion", 99) ? compound.getInt("DataVersion") : -1;
            if (dataVersion < 1493) {
                if (compound.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                    compound = getLegacyStructureDataUtil().func_212181_a(compound);
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

        CompoundNBT structureStarts = loadStructureStarts(chunkX, chunkZ);
        if (structureStarts == null || structureStarts.size() == 0) return;

        Map<String, StructureStart<?>> structureStartMap = new HashMap<>();
        for (String key : structureStarts.keySet()) {
            CompoundNBT compound = structureStarts.getCompound(key);
            if (compound.contains("BB")) {
                structureStartMap.put(key, new SimpleStructureStart(compound));
            }
        }

        EventBus.publish(new StructuresLoaded(structureStartMap, dimensionId));
    }

    private static class SimpleStructureStart extends StructureStart<IFeatureConfig> {
        SimpleStructureStart(CompoundNBT compound) {
            super(null,
                    0,
                    0,
                    new MutableBoundingBox(compound.getIntArray("BB")),
                    0,
                    0);

            ListNBT children = compound.getList("Children", 10);
            for (int index = 0; index < children.size(); ++index) {
                CompoundNBT child = children.getCompound(index);
                if (child.contains("BB")) this.components.add(new SimpleStructurePiece(child));
            }
        }

        @Override
        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int i, int i1, Biome biome, IFeatureConfig iFeatureConfig) {

        }
    }

    private static class SimpleStructurePiece extends StructurePiece {
        SimpleStructurePiece(CompoundNBT compound) {
            super(null, compound);
        }

        @Override
        protected void readAdditional(CompoundNBT compoundNBT) {

        }

        @Override
        public boolean func_230383_a_(ISeedReader iSeedReader, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox mutableBoundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            return false;
        }
    }

    private static class ChunkLoader implements AutoCloseable {
        private static final BiFunction<File, Boolean, RegionFileCache> creator =
                ReflectionHelper.getPrivateInstanceBuilder(RegionFileCache.class, File.class, boolean.class);

        private final RegionFileCache regionFileCache;

        public ChunkLoader(File file) {
            this.regionFileCache = creator.apply(file, false);
        }

        public CompoundNBT readChunk(int chunkX, int chunkZ) throws IOException {
            if (regionFileCache == null) return null;
            return regionFileCache.readChunk(new ChunkPos(chunkX, chunkZ));
        }

        public void close() throws IOException {
            if (regionFileCache == null) return;
            regionFileCache.close();
        }
    }
}
