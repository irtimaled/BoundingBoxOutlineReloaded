package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.awt.*;
import java.util.*;

public class DimensionProcessor extends BoundingBoxCache {
    DimensionProcessor(DimensionType dimensionType, ChunkProviderServer chunkProvider) {
        this.dimensionType = dimensionType;
        this.chunkProvider = chunkProvider;
    }

    private DimensionType dimensionType;
    private ChunkProviderServer chunkProvider;

    private boolean closed = false;

    @Override
    public void close() {
        closed = true;
        super.close();
    }

    private void addStructures(Setting drawStructure, StructureType structureType, Map<String, Collection<StructureStart>> structureMap) {
        if (!drawStructure.getBoolean()) return;

        Collection<StructureStart> structureStarts = structureMap.get(structureType.getName());
        if (structureStarts == null || structureStarts.size() == 0) return;
        Color color = structureType.getColor();
        for (StructureStart structureStart : structureStarts) {

            MutableBoundingBox bb = structureStart.getBoundingBox();
            if(bb == null)
                continue;

            BoundingBox boundingBox = BoundingBoxStructure.from(bb, color);
            if (isCached(boundingBox)) continue;

            Set<BoundingBox> structureBoundingBoxes = new HashSet<>();
            for (StructurePiece structureComponent : structureStart.getComponents()) {
                structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), color));
            }
            addBoundingBoxes(boundingBox, structureBoundingBoxes);
            Logger.info("[%s] new dimensionCache entries: %d", dimensionType, structureBoundingBoxes.size());
        }
    }

    private Map<String, Collection<StructureStart>> getStructureMap(ChunkProviderServer chunkProvider) {
        Map<String, Collection<StructureStart>> map = new HashMap<>();
        for (Chunk chunk : chunkProvider.getLoadedChunks()) {
            Map<String, StructureStart> structureStarts = chunk.getStructureStarts();
            for (String key : structureStarts.keySet())            {
                map.computeIfAbsent(key, s -> new HashSet<>())
                   .add(structureStarts.get(key));
            }
        }
        return map;
    }

    @Override
    public synchronized void refresh() {
        if (closed) return;

        Map<String, Collection<StructureStart>> structureMap = getStructureMap(chunkProvider);
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
        }
    }
}
