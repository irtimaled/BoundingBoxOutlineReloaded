package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractChunkProcessor {
    Map<BoundingBoxType, Function<IChunkGenerator, Collection<StructureStart>>> supportedStructures = new HashMap<>();

    AbstractChunkProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    static <T extends IChunkGenerator, R extends MapGenStructure> Collection<StructureStart> getStructures(T chunkGenerator, Class<R> generatorClass) {
        Class<T> chunkGeneratorClass = (Class<T>) chunkGenerator.getClass();
        R structureGenerator = ReflectionHelper.getPrivateValue(chunkGeneratorClass, chunkGenerator, generatorClass);
        if (structureGenerator != null) {
            Map<ChunkPos, StructureStart> structureMap = ReflectionHelper.getPrivateValue(MapGenStructure.class, structureGenerator, Map.class);
            return structureMap.values();
        }
        return Collections.emptyList();
    }

    Collection<StructureStart> getStructuresWithComponent(Collection<StructureStart> structures, Class structureComponent) {
        Collection<StructureStart> validStructures = new HashSet<>();
        for (StructureStart structure : structures) {
            if (structure.getComponents().get(0).getClass().equals(structureComponent)) {
                validStructures.add(structure);
            }
        }
        return validStructures;
    }


    private void addStructures(BoundingBoxType type, StructureStart structureStart) {
        StructureBoundingBox bb = structureStart.getBoundingBox();
        if (bb == null) return;

        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructureComponent structureComponent : structureStart.getComponents()) {
            structureBoundingBoxes.add(buildStructure(structureComponent.getBoundingBox(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private AbstractBoundingBox buildStructure(StructureBoundingBox bb, BoundingBoxType type) {
        Coords min = new Coords(bb.minX, bb.minY, bb.minZ);
        Coords max = new Coords(bb.maxX, bb.maxY, bb.maxZ);
        return BoundingBoxStructure.from(min, max, type);
    }

    private void addMobSpawners(Chunk chunk) {
        Collection<TileEntity> tileEntities = chunk.getWorld().loadedTileEntityList;
        for (TileEntity tileEntity : tileEntities) {
            TileEntityMobSpawner spawner = TypeHelper.as(tileEntity, TileEntityMobSpawner.class);
            if (spawner != null) {
                Coords coords = new Coords(spawner.getPos());
                boundingBoxCache.addBoundingBox(BoundingBoxMobSpawner.from(coords));
            }
        }
    }

    public void process(Chunk chunk) {
        IChunkProvider chunkProvider = chunk.getWorld().getChunkProvider();
        IChunkGenerator chunkGenerator = ReflectionHelper.getPrivateValue(ChunkProviderServer.class, (ChunkProviderServer) chunkProvider, IChunkGenerator.class);
        supportedStructures.forEach((type, supplier) ->
                {
                    Collection<StructureStart> structureStarts = supplier.apply(chunkGenerator);
                    structureStarts.forEach(structureStart -> addStructures(type, structureStart));
                }
        );
        addMobSpawners(chunk);
    }
}
