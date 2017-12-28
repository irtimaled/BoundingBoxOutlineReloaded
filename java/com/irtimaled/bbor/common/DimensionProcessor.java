package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.ReflectionHelper;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.*;

import java.awt.*;
import java.util.*;

public class DimensionProcessor extends BoundingBoxCache {
    DimensionProcessor(DimensionType dimensionType, IChunkGenerator chunkGenerator) {
        this.dimensionType = dimensionType;
        this.chunkGenerator = chunkGenerator;
    }

    private DimensionType dimensionType;
    private IChunkGenerator chunkGenerator;

    private boolean closed = false;

    @Override
    public void close() {
        closed = true;
        chunkGenerator = null;
        super.close();
    }

    private static <T extends IChunkGenerator, R extends MapGenStructure> Collection<StructureStart> getStructures(T chunkGenerator, Class<R> generatorClass) {
        Class<T> chunkGeneratorClass = (Class<T>) chunkGenerator.getClass();
        R structureGenerator = ReflectionHelper.getPrivateValue(chunkGeneratorClass, chunkGenerator, generatorClass);
        if (structureGenerator != null) {
            Map<ChunkPos, StructureStart> structureMap = ReflectionHelper.getPrivateValue(MapGenStructure.class, structureGenerator, Map.class);
            return structureMap.values();
        }
        return Collections.emptyList();
    }

    private Map<StructureType, Collection<StructureStart>> getStructures() {
        Map<StructureType, Collection<StructureStart>> structureMap = new HashMap<>();
        if (chunkGenerator instanceof ChunkGeneratorOverworld) {
            if (ConfigManager.drawDesertTemples.getBoolean()) {
                structureMap.put(StructureType.DesertTemple, getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.DesertPyramid.class));
            }

            if (ConfigManager.drawJungleTemples.getBoolean()) {
                structureMap.put(StructureType.JungleTemple, getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.JunglePyramid.class));
            }

            if (ConfigManager.drawWitchHuts.getBoolean()) {
                structureMap.put(StructureType.WitchHut, getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.SwampHut.class));
            }

            if (ConfigManager.drawOceanMonuments.getBoolean()) {
                structureMap.put(StructureType.OceanMonument, getStructures(chunkGenerator, StructureOceanMonument.class));
            }

            if (ConfigManager.drawStrongholds.getBoolean()) {
                structureMap.put(StructureType.Stronghold, getStructures(chunkGenerator, MapGenStronghold.class));
            }

            if (ConfigManager.drawMansions.getBoolean()) {
                structureMap.put(StructureType.Mansion, getStructures(chunkGenerator, WoodlandMansion.class));
            }

            if (ConfigManager.drawMineShafts.getBoolean()) {
                structureMap.put(StructureType.MineShaft, getStructures(chunkGenerator, MapGenMineshaft.class));
            }
        } else if (chunkGenerator instanceof ChunkGeneratorHell) {
            if (ConfigManager.drawNetherFortresses.getBoolean()) {
                structureMap.put(StructureType.NetherFortress, getStructures(chunkGenerator, MapGenNetherBridge.class));
            }
        } else if (chunkGenerator instanceof ChunkGeneratorEnd) {
            if (ConfigManager.drawEndCities.getBoolean()) {
                structureMap.put(StructureType.EndCity, getStructures(chunkGenerator, MapGenEndCity.class));
            }
        }

        return structureMap;
    }

    private Collection<StructureStart> getStructuresWithComponent(Collection<StructureStart> structures, Class structureComponent) {
        Collection<StructureStart> validStructures = new HashSet<>();
        for (StructureStart structure : structures) {
            if (structure.getComponents().get(0).getClass().equals(structureComponent)) {
                validStructures.add(structure);
            }
        }
        return validStructures;
    }

    @Override
    public synchronized void refresh() {
        if (closed) return;

        Map<StructureType, Collection<StructureStart>> structureMap = getStructures();
        for (StructureType structureType : structureMap.keySet()) {
            Color color = structureType.getColor();
            for (StructureStart structureStart : structureMap.get(structureType)) {
                if (structureStart.getBoundingBox() != null) {
                    BoundingBox boundingBox = BoundingBoxStructure.from(structureStart.getBoundingBox(), color);
                    if (!isCached(boundingBox)) {
                        Set<BoundingBox> structureBoundingBoxes = new HashSet<>();
                        for (StructureComponent structureComponent : structureStart.getComponents()) {
                            structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), color));
                        }
                        addBoundingBoxes(boundingBox, structureBoundingBoxes);
                        Logger.info("[%s] new dimensionCache entries: %d", dimensionType, structureBoundingBoxes.size());
                    }
                }
            }
        }
    }
}
