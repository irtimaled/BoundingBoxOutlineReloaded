package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DimensionProcessor extends BoundingBoxCache {
    DimensionProcessor(DimensionType dimensionType) {
        this.dimensionType = dimensionType;
    }

    private DimensionType dimensionType;

    private boolean closed = false;

    @Override
    public void close() {
        closed = true;
        super.close();
    }

    private void addStructures(BoundingBoxType type, Map<String, StructureStart> structureMap) {
        if (!type.shouldRender()) return;

        StructureStart structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;
        MutableBoundingBox bb = structureStart.getBoundingBox();
        if (bb == null) return;

        BoundingBox boundingBox = BoundingBoxStructure.from(bb, type);
        if (isCached(boundingBox)) return;

        Set<BoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.getComponents()) {
            structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), type));
        }
        addBoundingBoxes(boundingBox, structureBoundingBoxes);
        Logger.info("[%s] new dimensionCache entries: %d", dimensionType, structureBoundingBoxes.size());
    }

    private void addMobSpawners(Chunk chunk) {
        Collection<TileEntity> tileEntities = chunk.getTileEntityMap().values();
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity instanceof TileEntityMobSpawner) {
                addBoundingBox(BoundingBoxMobSpawner.from(tileEntity.getPos()));
            }
        }
    }

    synchronized void processChunk(Chunk chunk) {
        if (closed) return;

        Map<String, StructureStart> structureMap = chunk.getStructureStarts();
        if (structureMap.size() > 0) {
            addStructures(BoundingBoxType.DesertTemple, structureMap);
            addStructures(BoundingBoxType.JungleTemple, structureMap);
            addStructures(BoundingBoxType.WitchHut, structureMap);
            addStructures(BoundingBoxType.OceanMonument, structureMap);
            addStructures(BoundingBoxType.Stronghold, structureMap);
            addStructures(BoundingBoxType.Mansion, structureMap);
            addStructures(BoundingBoxType.MineShaft, structureMap);
            addStructures(BoundingBoxType.Shipwreck, structureMap);
            addStructures(BoundingBoxType.OceanRuin, structureMap);
            addStructures(BoundingBoxType.BuriedTreasure, structureMap);
            addStructures(BoundingBoxType.Igloo, structureMap);
            addStructures(BoundingBoxType.NetherFortress, structureMap);
            addStructures(BoundingBoxType.EndCity, structureMap);
            addStructures(BoundingBoxType.PillagerOutpost, structureMap);
        }
        if (BoundingBoxType.MobSpawner.shouldRender()) {
            addMobSpawners(chunk);
        }
    }
}
