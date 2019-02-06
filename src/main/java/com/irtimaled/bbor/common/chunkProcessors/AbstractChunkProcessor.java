package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChunkProcessor {
    Set<BoundingBoxType> supportedStructures = new HashSet<>();

    AbstractChunkProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, Map<String, StructureStart> structureMap) {
        StructureStart structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;

        MutableBoundingBox bb = structureStart.getBoundingBox();
        if (bb == null) return;

        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.getComponents()) {
            structureBoundingBoxes.add(buildStructure(structureComponent.getBoundingBox(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private AbstractBoundingBox buildStructure(MutableBoundingBox bb, BoundingBoxType type) {
        Coords min = new Coords(bb.minX, bb.minY, bb.minZ);
        Coords max = new Coords(bb.maxX, bb.maxY, bb.maxZ);
        return BoundingBoxStructure.from(min, max, type);
    }

    private void addMobSpawners(Chunk chunk) {
        Collection<TileEntity> tileEntities = chunk.getTileEntityMap().values();
        for (TileEntity tileEntity : tileEntities) {
            MobSpawnerTileEntity spawner = TypeHelper.as(tileEntity, MobSpawnerTileEntity.class);
            if (spawner != null) {
                Coords coords = new Coords(spawner.getPos());
                boundingBoxCache.addBoundingBox(BoundingBoxMobSpawner.from(coords));
            }
        }
    }

    public void process(Chunk chunk) {
        Map<String, StructureStart> structureMap = chunk.getStructureStarts();
        if (structureMap.size() > 0) {
            supportedStructures.forEach(type -> addStructures(type, structureMap));
        }
        addMobSpawners(chunk);
    }
}
