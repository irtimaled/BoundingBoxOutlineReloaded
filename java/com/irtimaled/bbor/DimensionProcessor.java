package com.irtimaled.bbor;

import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DimensionProcessor extends BoundingBoxCache {

    private ConfigManager configManager;
    private World world;

    public DimensionProcessor(ConfigManager configManager, World world, int dimensionId, IChunkProvider chunkProvider) {
        this.configManager = configManager;
        this.world = world;
        this.dimensionId = dimensionId;
        this.chunkProvider = chunkProvider;
        villageCache = new HashSet<BoundingBox>();
        slimeChunkCache = new HashSet<BoundingBox>();
        worldSpawnCache = new HashSet<BoundingBox>();
    }

    private int dimensionId;
    private IChunkProvider chunkProvider;
    private Set<BoundingBox> villageCache;
    private Set<BoundingBox> slimeChunkCache;
    private Set<BoundingBox> worldSpawnCache;

    private boolean closed = false;

    @Override
    public void close() {
        closed = true;
        chunkProvider = null;
        villageCache.clear();
        slimeChunkCache.clear();
        worldSpawnCache.clear();
        super.close();
    }

    private static <T extends IChunkProvider> Collection<StructureStart> getStructures(T chunkProvider, int method) {
        Class<T> cpClass = (Class<T>) chunkProvider.getClass();
        Object structureGenerator = ReflectionHelper.getPrivateValue(cpClass, chunkProvider, method);
        if (structureGenerator instanceof MapGenStructure) {
            Map<ChunkCoordIntPair, StructureStart> structureMap = ReflectionHelper.getPrivateValue(MapGenStructure.class, (MapGenStructure) structureGenerator, 1);
            return structureMap.values();
        }
        return Collections.emptyList();
    }

    private Map<StructureType, Collection<StructureStart>> getStructures() {

        Map<StructureType, Collection<StructureStart>> structureMap = new HashMap<StructureType, Collection<StructureStart>>();
        if (chunkProvider instanceof ChunkProviderGenerate) {
            if (configManager.drawDesertTemples.getBoolean()) {
                structureMap.put(StructureType.DesertTemple, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.DesertPyramid.class));
            }

            if (configManager.drawJungleTemples.getBoolean()) {
                structureMap.put(StructureType.JungleTemple, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.JunglePyramid.class));
            }

            if (configManager.drawWitchHuts.getBoolean()) {
                structureMap.put(StructureType.WitchHut, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.SwampHut.class));
            }

            if (configManager.drawOceanMonuments.getBoolean()) {
                structureMap.put(StructureType.OceanMonument, getStructures(chunkProvider, 22));
            }

            if (configManager.drawStrongholds.getBoolean()) {
                structureMap.put(StructureType.Stronghold, getStructures(chunkProvider, 17));
            }

            if (configManager.drawMineShafts.getBoolean()) {
                structureMap.put(StructureType.MineShaft, getStructures(chunkProvider, 19));
            }
        } else if (chunkProvider instanceof ChunkProviderHell) {

            if (configManager.drawNetherFortresses.getBoolean()) {
                structureMap.put(StructureType.NetherFortress, getStructures(chunkProvider, 22));
            }
        }

        return structureMap;
    }

    private Collection<StructureStart> getStructuresWithComponent(Collection<StructureStart> structures, Class structureComponent) {
        Collection<StructureStart> validStructures = new HashSet<StructureStart>();
        for (StructureStart structure : structures) {
            if (structure.getComponents().getFirst().getClass().equals(structureComponent)) {
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
                BoundingBox boundingBox = BoundingBoxStructure.from(structureStart.getBoundingBox(), color);
                if (!cache.containsKey(boundingBox)) {
                    Set<BoundingBox> structureBoundingBoxes = new HashSet<BoundingBox>();
                    Iterator structureComponents = structureStart.getComponents().iterator();
                    while (structureComponents.hasNext()) {
                        StructureComponent structureComponent = (StructureComponent) structureComponents.next();
                        structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), color));
                    }
                    cache.put(boundingBox, structureBoundingBoxes);
                    FMLLog.info("[%d] new boundingBoxCacheMap entries: %d", dimensionId, structureBoundingBoxes.size());
                }
            }
        }

        if (configManager.drawVillages.getBoolean() &&
                (world.villageCollectionObj != null)) {

            Set<BoundingBox> villageBoundingBoxes = new HashSet<BoundingBox>();
            List<Village> villages = world.villageCollectionObj.getVillageList();
            int c = 0;
            for (Village village : villages) {
                BlockPos center = ReflectionHelper.getPrivateValue(Village.class, village, 3);
                Integer radius = ReflectionHelper.getPrivateValue(Village.class, village, 4);
                villageBoundingBoxes.add(BoundingBoxVillage.from(center, radius, village.getNumVillagers(), village.getNumVillageDoors()));
            }
            processDelta(villageCache, villageBoundingBoxes);

            villageCache = villageBoundingBoxes;
        }
    }

    private void processDelta(Set<BoundingBox> oldBoundingBoxes, Set<BoundingBox> newBoundingBoxes) {
        for (BoundingBox bb : oldBoundingBoxes) {
            if (!newBoundingBoxes.contains(bb)) {
                cache.remove(bb);
                BoundingBoxOutlineReloaded.proxy.boundingBoxRemoved(bb);
            } else {
                if (!cache.containsKey(bb)) {
                    Set<BoundingBox> boundingBoxes = new HashSet<BoundingBox>();
                    boundingBoxes.add(bb);
                    cache.put(bb, boundingBoxes);
                }
            }
        }
    }
}