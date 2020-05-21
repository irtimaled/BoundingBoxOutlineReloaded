package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

class NBTStructureLoader {
    private final int dimensionId;
    private final Set<String> loadedChunks = new HashSet<>();

    private LegacyStructureDataUtil legacyStructureDataUtil = null;
    private SaveHandler saveHandler = null;
    private File chunkSaveLocation = null;
    private ChunkLoader chunkLoader;

    NBTStructureLoader(int dimensionId, SaveHandler saveHandler, File worldDirectory) {
        this.dimensionId = dimensionId;
        this.configure(saveHandler, worldDirectory);
    }

    void clear() {
        this.saveHandler = null;
        this.chunkSaveLocation = null;
        this.loadedChunks.clear();

        if (this.chunkLoader == null) return;
        try {
            this.chunkLoader.close();
        } catch (IOException ignored) {
        }
    }

    void configure(SaveHandler saveHandler, File worldDirectory) {
        this.saveHandler = saveHandler;
        if (worldDirectory != null) {
            this.chunkSaveLocation = new File(DimensionType.getById(dimensionId).getDirectory(worldDirectory), "region");
            this.chunkLoader = new ChunkLoader(this.chunkSaveLocation);
        }
    }

    private LegacyStructureDataUtil getLegacyStructureDataUtil() {
        if (this.legacyStructureDataUtil == null) {
            File dataFolder = new File(DimensionType.OVERWORLD.getDirectory(this.saveHandler.getWorldDirectory()), "data");
            this.legacyStructureDataUtil = LegacyStructureDataUtil.func_215130_a(DimensionType.getById(dimensionId),
                    new DimensionSavedDataManager(dataFolder, this.saveHandler.getFixer()));
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

        Map<String, StructureStart> structureStartMap = new HashMap<>();
        for (String key : structureStarts.keySet()) {
            CompoundNBT compound = structureStarts.getCompound(key);
            if (compound.contains("BB")) {
                structureStartMap.put(key, new SimpleStructureStart(compound));
            }
        }

        EventBus.publish(new StructuresLoaded(structureStartMap, dimensionId));
    }

    private static class SimpleStructureStart extends StructureStart {
        SimpleStructureStart(CompoundNBT compound) {
            super(null,
                    0,
                    0,
                    null,
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
        public void init(ChunkGenerator<?> chunkGenerator, TemplateManager templateManager, int i, int i1, Biome biome) {

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
        public boolean addComponentParts(IWorld iWorld, Random random, MutableBoundingBox mutableBoundingBox, ChunkPos chunkPos) {
            return false;
        }
    }

    private static class ChunkLoader {
        private final RegionFileCache regionFileCache;

        public ChunkLoader(File file) {
            this.regionFileCache = new RegionFileCache(file) {
            };
        }

        public CompoundNBT readChunk(int chunkX, int chunkZ) throws IOException {
            return regionFileCache.readChunk(new ChunkPos(chunkX, chunkZ));
        }

        public void close() throws IOException {
            regionFileCache.close();
        }
    }
}
