package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.ReflectionHelper;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionBasedStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

class NBTStructureLoader {
    private final DimensionId dimensionId;
    private final Set<String> loadedChunks = new HashSet<>();

    private FeatureUpdater legacyStructureDataUtil = null;
    private LevelStorage.Session saveHandler = null;
    private Path chunkSaveLocation = null;
    private ChunkLoader chunkLoader;

    NBTStructureLoader(DimensionId dimensionId, LevelStorage.Session saveHandler, Path worldDirectory) {
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
            if(closeable == null) continue;
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    void configure(LevelStorage.Session saveHandler, Path worldDirectory) {
        this.saveHandler = saveHandler;
        if (worldDirectory != null) {
            this.chunkSaveLocation = DimensionType.getSaveDirectory(this.dimensionId.getDimensionType(), worldDirectory).resolve("region");
            this.chunkLoader = new ChunkLoader(this.chunkSaveLocation);
        }
    }

    private FeatureUpdater getLegacyStructureDataUtil() {
        if (this.legacyStructureDataUtil == null) {
            Path dataFolder = this.saveHandler.getWorldDirectory(World.OVERWORLD).resolve("data");
            this.legacyStructureDataUtil = FeatureUpdater.create(dimensionId.getDimensionType(),
                    new PersistentStateManager(dataFolder.toFile(), MinecraftClient.getInstance().getDataFixer()));
        }
        return this.legacyStructureDataUtil;
    }

    private NbtCompound loadStructureStarts(int chunkX, int chunkZ) {
        try {
            NbtCompound compound = this.chunkLoader.readChunk(chunkX, chunkZ);
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

        NbtCompound structureStarts = loadStructureStarts(chunkX, chunkZ);
        if (structureStarts == null || structureStarts.getSize() == 0) return;

        Map<String, StructureStart<?>> structureStartMap = new HashMap<>();
        for (String key : structureStarts.getKeys()) {
            NbtCompound compound = structureStarts.getCompound(key);
            if (compound.contains("BB")) {
                structureStartMap.put(key, new SimpleStructureStart(compound));
            }
        }

        EventBus.publish(new StructuresLoaded(structureStartMap, dimensionId));
    }

    private static class SimpleStructureStart extends StructureStart<FeatureConfig> {
        private final BlockBox parsedBoundingBox;

        SimpleStructureStart(NbtCompound compound) {
            super(null,
                    new ChunkPos(0, 0),
                    0,
                    new StructurePiecesList(createList(compound)));

            this.parsedBoundingBox = create(compound.getIntArray("BB"));
        }

        private static List<StructurePiece> createList(NbtCompound compound) {
            final ArrayList<StructurePiece> pieces = new ArrayList<>();
            NbtList children = compound.getList("Children", 10);
            for (int index = 0; index < children.size(); ++index) {
                NbtCompound child = children.getCompound(index);
                if (child.contains("BB")) pieces.add(new SimpleStructurePiece(child));
            }
            return pieces;
        }

        private static BlockBox create(int[] compound) {
            if (compound.length == 6)
                return new BlockBox(compound[0], compound[1], compound[2], compound[3], compound[4], compound[5]);
            else
                return new BlockBox(0, 0, 0, 0, 0, 0);
        }

        @Override
        public void place(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos) {
        }

        @Override
        public BlockBox getBoundingBox() {
            return this.parsedBoundingBox;
        }
    }

    private static class SimpleStructurePiece extends StructurePiece {
        SimpleStructurePiece(NbtCompound compound) {
            super(null, compound);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos, BlockPos blockPos) {
        }
    }

    private static class ChunkLoader implements AutoCloseable {
        private static final BiFunction<Path, Boolean, RegionBasedStorage> creator =
                ReflectionHelper.getPrivateInstanceBuilder(RegionBasedStorage.class, Path.class, boolean.class);

        private final RegionBasedStorage regionFileCache;

        public ChunkLoader(Path file) {
            this.regionFileCache = creator.apply(file, false);
        }

        public NbtCompound readChunk(int chunkX, int chunkZ) throws IOException {
            if (regionFileCache == null) return null;
            return regionFileCache.getTagAt(new ChunkPos(chunkX, chunkZ));
        }

        public void close() throws IOException {
            if (regionFileCache == null) return;
            regionFileCache.close();
        }
    }
}
