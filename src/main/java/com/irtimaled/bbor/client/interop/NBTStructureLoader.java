package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldSavedDataStorage;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

class NBTStructureLoader {
    private final int dimensionId;
    private final Set<String> loadedChunks = new HashSet<>();

    private LegacyStructureDataUtil legacyStructureDataUtil = null;
    private ISaveHandler saveHandler = null;
    private File chunkSaveLocation = null;

    NBTStructureLoader(int dimensionId, ISaveHandler saveHandler, File worldDirectory) {
        this.dimensionId = dimensionId;
        this.configure(saveHandler, worldDirectory);
    }

    void clear() {
        this.saveHandler = null;
        this.chunkSaveLocation = null;
        this.loadedChunks.clear();
    }

    void configure(ISaveHandler saveHandler, File worldDirectory) {
        this.saveHandler = saveHandler;
        if(worldDirectory != null) {
            this.chunkSaveLocation = DimensionType.getById(dimensionId).getDirectory(worldDirectory);
        }
    }

    private LegacyStructureDataUtil getLegacyStructureDataUtil() {
        if (this.legacyStructureDataUtil == null) {
            this.legacyStructureDataUtil = LegacyStructureDataUtil.func_212183_a(DimensionType.getById(dimensionId), new WorldSavedDataStorage(saveHandler));
        }
        return this.legacyStructureDataUtil;
    }

    private NBTTagCompound loadStructureStarts(int chunkX, int chunkZ) {
        try {
            DataInputStream stream = RegionFileCache.getChunkInputStream(chunkSaveLocation, chunkX, chunkZ);
            if (stream != null) {
                NBTTagCompound compound = CompressedStreamTools.read(stream);
                stream.close();
                int dataVersion = compound.contains("DataVersion", 99) ? compound.getInt("DataVersion") : -1;
                if (dataVersion < 1493) {
                    if (compound.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                        compound = getLegacyStructureDataUtil().func_212181_a(compound);
                    }
                }
                return compound.getCompound("Level").getCompound("Structures").getCompound("Starts");
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    void loadStructures(int chunkX, int chunkZ) {
        if (saveHandler == null) return;

        if (!loadedChunks.add(String.format("%s,%s", chunkX, chunkZ))) return;

        NBTTagCompound structureStarts = loadStructureStarts(chunkX, chunkZ);
        if (structureStarts == null || structureStarts.size() == 0) return;

        Map<String, StructureStart> structureStartMap = new HashMap<>();
        for (String key : structureStarts.keySet()) {
            NBTTagCompound compound = structureStarts.getCompound(key);
            if (compound.contains("BB")) {
                structureStartMap.put(key, new SimpleStructureStart(compound));
            }
        }

        EventBus.publish(new StructuresLoaded(structureStartMap, dimensionId));
    }

    private static class SimpleStructureStart extends StructureStart {
        SimpleStructureStart(NBTTagCompound compound) {
            this.boundingBox = new MutableBoundingBox(compound.getIntArray("BB"));

            NBTTagList children = compound.getList("Children", 10);
            for (int index = 0; index < children.size(); ++index) {
                NBTTagCompound child = children.getCompound(index);
                if (child.contains("BB")) this.components.add(new SimpleStructurePiece(child));
            }
        }
    }

    private static class SimpleStructurePiece extends StructurePiece {
        SimpleStructurePiece(NBTTagCompound compound) {
            this.boundingBox = new MutableBoundingBox(compound.getIntArray("BB"));
        }

        @Override
        protected void writeAdditional(NBTTagCompound nbtTagCompound) {
        }

        @Override
        protected void readAdditional(NBTTagCompound nbtTagCompound, TemplateManager templateManager) {
        }

        @Override
        public boolean addComponentParts(IWorld iWorld, Random random, MutableBoundingBox mutableBoundingBox, ChunkPos chunkPos) {
            return false;
        }
    }
}
