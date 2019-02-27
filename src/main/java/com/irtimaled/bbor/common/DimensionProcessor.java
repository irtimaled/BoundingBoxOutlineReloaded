package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.awt.*;
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

    private void addStructures(Setting drawStructure, StructureType structureType, Map<String, StructureStart> structureMap) {
        if (!drawStructure.getBoolean()) return;

        StructureStart structureStart = structureMap.get(structureType.getName());
        if (structureStart == null) return;
        Color color = structureType.getColor();
        MutableBoundingBox bb = structureStart.getBoundingBox();
        if (bb == null)
            return;

        BoundingBox boundingBox = BoundingBoxStructure.from(bb, color);
        if (isCached(boundingBox)) return;

        Set<BoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.getComponents()) {
            structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), color));
        }
        addBoundingBoxes(boundingBox, structureBoundingBoxes);
        Logger.info("[%s] new dimensionCache entries: %d", dimensionType, structureBoundingBoxes.size());
    }

    public synchronized void processChunk(Chunk chunk) {
        if (closed) return;

        Map<String, StructureStart> structureMap = chunk.getStructureStarts();
        if (structureMap.size() > 0) {
            addStructures(ConfigManager.drawDesertTemples, StructureType.DesertTemple, structureMap);
            addStructures(ConfigManager.drawJungleTemples, StructureType.JungleTemple, structureMap);
            addStructures(ConfigManager.drawWitchHuts, StructureType.WitchHut, structureMap);
            addStructures(ConfigManager.drawOceanMonuments, StructureType.OceanMonument, structureMap);
            addStructures(ConfigManager.drawStrongholds, StructureType.Stronghold, structureMap);
            addStructures(ConfigManager.drawMansions, StructureType.Mansion, structureMap);
            addStructures(ConfigManager.drawMineShafts, StructureType.MineShaft, structureMap);
            addStructures(ConfigManager.drawShipwrecks, StructureType.Shipwreck, structureMap);
            addStructures(ConfigManager.drawOceanRuins, StructureType.OceanRuin, structureMap);
            addStructures(ConfigManager.drawBuriedTreasure, StructureType.BuriedTreasure, structureMap);
            addStructures(ConfigManager.drawIgloos, StructureType.Igloo, structureMap);
            addStructures(ConfigManager.drawNetherFortresses, StructureType.NetherFortress, structureMap);
            addStructures(ConfigManager.drawEndCities, StructureType.EndCity, structureMap);
            addStructures(ConfigManager.drawPillagerOutposts, StructureType.PillagerOutpost, structureMap);
        }
        if (ConfigManager.drawMobSpawners.getBoolean()) {
            Collection<TileEntity> tileEntities = chunk.getTileEntityMap().values();
            for (TileEntity tileEntity : tileEntities) {
                if (tileEntity instanceof TileEntityMobSpawner) {
                    addBoundingBox(BoundingBoxMobSpawner.from(tileEntity.getPos()));
                }
            }
        }
    }
}
