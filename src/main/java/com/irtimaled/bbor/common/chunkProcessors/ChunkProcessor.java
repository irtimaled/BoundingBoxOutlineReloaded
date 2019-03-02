package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkProcessor {
    protected Set<BoundingBoxType> supportedStructures = new HashSet<>();

    ChunkProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    protected final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, Map<String, StructureStart> structureMap) {
        StructureStart structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;

        MutableBoundingBox bb = structureStart.getBoundingBox();
        if (bb == null) return;

        BoundingBox boundingBox = BoundingBoxStructure.from(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<BoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.getComponents()) {
            structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private void addMobSpawners(Chunk chunk) {
        Collection<TileEntity> tileEntities = chunk.getTileEntityMap().values();
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity instanceof TileEntityMobSpawner) {
                boundingBoxCache.addBoundingBox(BoundingBoxMobSpawner.from(tileEntity.getPos()));
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
