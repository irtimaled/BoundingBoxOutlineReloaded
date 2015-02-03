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
    private long seed;

    public DimensionProcessor(ConfigManager configManager, World world, long seed, int dimensionId, IChunkProvider chunkProvider) {
        this.configManager = configManager;
        this.world = world;
        this.seed = seed;
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

    private static final int JUNGLE_TEMPLE = 1;
    private static final int DESERT_TEMPLE = 2;
    private static final int WITCH_HUT = 3;
    private static final int OCEAN_MONUMENT = 4;
    private static final int STRONGHOLD = 5;
    private static final int MINE_SHAFT = 6;
    private static final int NETHER_FORTRESS = 7;

    private Map<Integer, Collection<StructureStart>> getStructures() {

        Map<Integer, Collection<StructureStart>> structureMap = new HashMap<Integer, Collection<StructureStart>>();
        if (chunkProvider instanceof ChunkProviderGenerate) {
            if (configManager.drawDesertTemples.getBoolean()) {
                structureMap.put(DimensionProcessor.DESERT_TEMPLE, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.DesertPyramid.class));
            }

            if (configManager.drawJungleTemples.getBoolean()) {
                structureMap.put(DimensionProcessor.JUNGLE_TEMPLE, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.JunglePyramid.class));
            }

            if (configManager.drawWitchHuts.getBoolean()) {
                structureMap.put(DimensionProcessor.WITCH_HUT, getStructuresWithComponent(getStructures(chunkProvider, 20), ComponentScatteredFeaturePieces.SwampHut.class));
            }

            if (configManager.drawOceanMonuments.getBoolean()) {
                structureMap.put(DimensionProcessor.OCEAN_MONUMENT, getStructures(chunkProvider, 22));
            }

            if (configManager.drawStrongholds.getBoolean()) {
                structureMap.put(DimensionProcessor.STRONGHOLD, getStructures(chunkProvider, 17));
            }

            if (configManager.drawMineShafts.getBoolean()) {
                structureMap.put(DimensionProcessor.MINE_SHAFT, getStructures(chunkProvider, 19));
            }
        } else if (chunkProvider instanceof ChunkProviderHell) {

            if (configManager.drawNetherFortresses.getBoolean()) {
                structureMap.put(DimensionProcessor.NETHER_FORTRESS, getStructures(chunkProvider, 22));
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

        Map<Integer, Collection<StructureStart>> structureMap = getStructures();
        for (Integer structureType : structureMap.keySet()) {
            Color color = getStructureColor(structureType);
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

        if (dimensionId == 0) {
            if (configManager.drawWorldSpawn.getBoolean()) {
                Set<BoundingBox> worldSpawnBoundingBoxes = new HashSet<BoundingBox>();
                int spawnX = world.getWorldInfo().getSpawnX();
                int spawnZ = world.getWorldInfo().getSpawnZ();
                worldSpawnBoundingBoxes.add(getSpawnBoundingBox(spawnX, spawnZ));
                worldSpawnBoundingBoxes.add(getSpawnChunksBoundingBox(spawnX, spawnZ));
                processDelta(worldSpawnCache, worldSpawnBoundingBoxes);

                worldSpawnCache = worldSpawnBoundingBoxes;
            }


            if (configManager.drawSlimeChunks.getBoolean()) {
                Set<BoundingBox> slimeChunkBoundingBoxes = new HashSet<BoundingBox>();
                Set<ChunkCoordIntPair> activeChunks = ReflectionHelper.getPrivateValue(World.class, world, 33);
                for (ChunkCoordIntPair chunk : activeChunks) {
                    if (isSlimeChunk(chunk.chunkXPos, chunk.chunkZPos)) {
                        slimeChunkBoundingBoxes.add(BoundingBoxSlimeChunk.from(chunk, Color.GREEN));
                    }
                }

                processDelta(slimeChunkCache, slimeChunkBoundingBoxes);

                slimeChunkCache = slimeChunkBoundingBoxes;
            }

            if (configManager.drawVillages.getBoolean() &&
                    (world.villageCollectionObj != null)) {

                Set<BoundingBox> villageBoundingBoxes = new HashSet<BoundingBox>();
                List<Village> villages = world.villageCollectionObj.getVillageList();
                int c = 0;
                for (Village village : villages) {
                    BlockPos center = ReflectionHelper.getPrivateValue(Village.class, village, 3);
                    Integer radius = ReflectionHelper.getPrivateValue(Village.class, village, 4);
                    boolean spawnsIronGolems = village.getNumVillagers() >= 10 &&
                            village.getNumVillageDoors() >= 21;
                    Color color = getVillageColor(c % 6);
                    villageBoundingBoxes.add(BoundingBoxVillage.from(center, radius, spawnsIronGolems, color));
                    ++c;
                }
                processDelta(villageCache, villageBoundingBoxes);

                villageCache = villageBoundingBoxes;
            }
        }
    }

    private BoundingBox getSpawnChunksBoundingBox(int spawnX, int spawnZ) {
        double chunkSize = 16;
        double midOffset = chunkSize * 6;
        double midX = Math.round((float) (spawnX / chunkSize)) * chunkSize;
        double midZ = Math.round((float) (spawnZ / chunkSize)) * chunkSize;
        BlockPos minBlockPos = new BlockPos(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / chunkSize % 0.5D == 0.0D && spawnZ / chunkSize % 0.5D == 0.0D) {
            midX += chunkSize;
            midZ += chunkSize;
        }
        BlockPos maxBlockPos = new BlockPos(midX + midOffset, 0, midZ + midOffset);
        return new BoundingBoxWorldSpawn(minBlockPos, maxBlockPos, Color.RED);
    }

    private BoundingBoxWorldSpawn getSpawnBoundingBox(int spawnX, int spawnZ) {
        BlockPos minBlockPos = new BlockPos(spawnX - 10, 0, spawnZ - 10);
        BlockPos maxBlockPos = new BlockPos(spawnX + 10, 0, spawnZ + 10);

        return new BoundingBoxWorldSpawn(minBlockPos, maxBlockPos, Color.RED);
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

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    private Color getVillageColor(int c) {
        switch (c) {
            case 0:
                return Color.RED;
            case 1:
                return Color.MAGENTA;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.CYAN;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.YELLOW;
        }
        return Color.WHITE;
    }

    private Color getStructureColor(Integer structureType) {
        switch (structureType) {
            case DimensionProcessor.DESERT_TEMPLE:
                return Color.ORANGE;
            case DimensionProcessor.JUNGLE_TEMPLE:
                return Color.GREEN;
            case DimensionProcessor.WITCH_HUT:
                return Color.BLUE;
            case DimensionProcessor.MINE_SHAFT:
                return Color.LIGHT_GRAY;
            case DimensionProcessor.NETHER_FORTRESS:
                return Color.RED;
            case DimensionProcessor.OCEAN_MONUMENT:
                return Color.CYAN;
            case DimensionProcessor.STRONGHOLD:
                return Color.YELLOW;
        }
        return Color.WHITE;
    }
}