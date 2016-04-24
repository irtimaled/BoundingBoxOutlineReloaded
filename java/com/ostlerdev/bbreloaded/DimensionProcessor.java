package com.ostlerdev.bbreloaded;

import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.structure.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DimensionProcessor extends BoundingBoxCache {

    private ConfigManager configManager;
    private World world;
    private IEventHandler eventHandler;

    public DimensionProcessor(IEventHandler eventHandler, ConfigManager configManager, World world, int dimensionId, IChunkProvider chunkProvider) {
        this.eventHandler = eventHandler;
        this.configManager = configManager;
        this.world = world;
        this.dimensionId = dimensionId;
        this.chunkProvider = chunkProvider;
        villageCache = new HashMap<Integer, BoundingBoxVillage>();
        slimeChunkCache = new HashSet<BoundingBox>();
        worldSpawnCache = new HashSet<BoundingBox>();
    }

    private int dimensionId;
    private IChunkProvider chunkProvider;
    private Map<Integer, BoundingBoxVillage> villageCache;
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

    private static <T extends IChunkProvider, R extends MapGenStructure> Collection<StructureStart> getStructures(T chunkProvider, Class<R> providerClass) {
        Class<T> chunkProviderClass = (Class<T>) chunkProvider.getClass();
        R structureGenerator = ReflectionHelper.getPrivateValue(chunkProviderClass, chunkProvider, providerClass);
        if (structureGenerator != null) {
            Map<ChunkCoordIntPair, StructureStart> structureMap = ReflectionHelper.getPrivateValue(MapGenStructure.class, structureGenerator, Map.class);
            return structureMap.values();
        }
        return Collections.emptyList();
    }

    private Map<StructureType, Collection<StructureStart>> getStructures() {

        Map<StructureType, Collection<StructureStart>> structureMap = new HashMap<StructureType, Collection<StructureStart>>();
        if (chunkProvider instanceof ChunkProviderServer) {
            if (configManager.drawDesertTemples.getBoolean()) {
                structureMap.put(StructureType.DesertTemple, getStructuresWithComponent(getStructures(chunkProvider, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.DesertPyramid.class));
            }

            if (configManager.drawJungleTemples.getBoolean()) {
                structureMap.put(StructureType.JungleTemple, getStructuresWithComponent(getStructures(chunkProvider, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.JunglePyramid.class));
            }

            if (configManager.drawWitchHuts.getBoolean()) {
                structureMap.put(StructureType.WitchHut, getStructuresWithComponent(getStructures(chunkProvider, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.SwampHut.class));
            }

            if (configManager.drawOceanMonuments.getBoolean()) {
                structureMap.put(StructureType.OceanMonument, getStructures(chunkProvider, StructureOceanMonument.class));
            }

            if (configManager.drawStrongholds.getBoolean()) {
                structureMap.put(StructureType.Stronghold, getStructures(chunkProvider, MapGenStronghold.class));
            }

            if (configManager.drawMineShafts.getBoolean()) {
                structureMap.put(StructureType.MineShaft, getStructures(chunkProvider, MapGenMineshaft.class));
            }
        } else if (chunkProvider instanceof ChunkProviderHell) {

            if (configManager.drawNetherFortresses.getBoolean()) {
                structureMap.put(StructureType.NetherFortress, getStructures(chunkProvider, MapGenNetherBridge.class));
            }
        }

        return structureMap;
    }

    private Collection<StructureStart> getStructuresWithComponent(Collection<StructureStart> structures, Class structureComponent) {
        Collection<StructureStart> validStructures = new HashSet<StructureStart>();
        for (StructureStart structure : structures) {
            if (structure.func_186161_c().get(0).getClass().equals(structureComponent)) {
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
                if (!isCached(boundingBox)) {
                    Set<BoundingBox> structureBoundingBoxes = new HashSet<BoundingBox>();
                    Iterator structureComponents = structureStart.func_186161_c().iterator();
                    while (structureComponents.hasNext()) {
                        StructureComponent structureComponent = (StructureComponent) structureComponents.next();
                        structureBoundingBoxes.add(BoundingBoxStructure.from(structureComponent.getBoundingBox(), color));
                    }
                    addBoundingBoxes(boundingBox, structureBoundingBoxes);
                    Logger.info("[%d] new boundingBoxCacheMap entries: %d", dimensionId, structureBoundingBoxes.size());
                }
            }
        }

        if (configManager.drawVillages.getBoolean() &&
                (world.getVillageCollection() != null)) {

            Map<Integer, BoundingBoxVillage> villageBoundingBoxes = new HashMap<Integer, BoundingBoxVillage>();
            List<Village> villages = world.getVillageCollection().getVillageList();
            for (Village village : villages) {
                int villageId = village.hashCode();
                BlockPos center = village.getCenter();
                Color color = null;
                if (villageCache.containsKey(villageId)) {
                    BoundingBoxVillage boundingBoxVillage = villageCache.get(villageId);
                    if (boundingBoxVillage.getCenter() == center) {
                        villageBoundingBoxes.put(villageId, boundingBoxVillage);
                        villageCache.remove(villageId);
                        continue;
                    }
                    color = boundingBoxVillage.getColor();
                }

                Integer radius = village.getVillageRadius();
                int population = village.getNumVillagers();
                Set<BlockPos> doors = getDoorsFromVillage(village);
                villageBoundingBoxes.put(villageId, BoundingBoxVillage.from(center, radius, color, population, doors));
            }
            processDelta(villageCache, villageBoundingBoxes);
            villageCache = villageBoundingBoxes;
        }
    }

    private Set<BlockPos> getDoorsFromVillage(Village village) {
        Set<BlockPos> doors = new HashSet<BlockPos>();
        for (Object doorInfo : village.getVillageDoorInfoList()) {
            VillageDoorInfo villageDoorInfo = (VillageDoorInfo) doorInfo;
            doors.add(villageDoorInfo.getDoorBlockPos());
        }
        return doors;
    }

    private void processDelta(Map<Integer, BoundingBoxVillage> oldVillages, Map<Integer, BoundingBoxVillage> newVillages) {
        for (BoundingBox village : oldVillages.values()) {
            removeBoundingBox(village);
            if(eventHandler!=null) {
                eventHandler.boundingBoxRemoved(village);
            }
        }
        for (BoundingBox village : newVillages.values()) {
            if (!isCached(village))
                addBoundingBox(village);
        }
    }
}