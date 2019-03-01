package com.irtimaled.bbor.client;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.DimensionCache;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class NBTFileParser {
    static void loadLocalDatFiles(String host, int port, DimensionCache dimensionCache) {
        Logger.info("Looking for local structures (host:port=%s:%d)", host, port);
        String path = String.format("BBOutlineReloaded%s%s%s%d", File.separator, host, File.separator, port);
        File localStructuresFolder = new File(ConfigManager.configDir, path);
        Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        if (!localStructuresFolder.exists()) {
            path = String.format("BBOutlineReloaded%s%s", File.separator, host);
            localStructuresFolder = new File(ConfigManager.configDir, path);
            Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        }
        if (!localStructuresFolder.exists()) {
            path = String.format("BBOutlineReloaded%s%s,%d", File.separator, host, port);
            localStructuresFolder = new File(ConfigManager.configDir, path);
            Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        }
        if (!localStructuresFolder.exists()) {
            Logger.info("No local structures folders found");
            return;
        }
        loadWorldData(localStructuresFolder, dimensionCache);
        populateBoundingBoxCache(localStructuresFolder, dimensionCache);
    }

    private static void loadWorldData(File localStructuresFolder, DimensionCache dimensionCache) {
        File file = new File(localStructuresFolder, "level.dat");
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound data = nbt.getCompoundTag("Data");
        long seed = data.getLong("RandomSeed");
        int spawnX = data.getInteger("SpawnX");
        int spawnZ = data.getInteger("SpawnZ");
        Logger.info("Loaded level.dat (seed: %d, spawn: %d,%d)", seed, spawnX, spawnZ);
        dimensionCache.setWorldData(seed, spawnX, spawnZ);
    }

    private static void populateBoundingBoxCache(File localStructuresFolder, DimensionCache dimensionCache) {
        dimensionCache.put(DimensionType.OVERWORLD, loadOverworldStructures(localStructuresFolder));
        dimensionCache.put(DimensionType.NETHER, loadNetherStructures(localStructuresFolder));
        dimensionCache.put(DimensionType.THE_END, loadEndStructures(localStructuresFolder));
    }

    private static BoundingBoxCache loadOverworldStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeDP", BoundingBoxType.DesertTemple);
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeJP", BoundingBoxType.JungleTemple);
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeSH", BoundingBoxType.WitchHut);
        loadStructure(localStructuresFolder, cache, "Monument.dat", "*", BoundingBoxType.OceanMonument);
        loadStructure(localStructuresFolder, cache, "Stronghold.dat", "*", BoundingBoxType.Stronghold);
        loadStructure(localStructuresFolder, cache, "Mansion.dat", "*", BoundingBoxType.Mansion);
        loadStructure(localStructuresFolder, cache, "Mineshaft.dat", "*", BoundingBoxType.MineShaft);
        loadVillages(localStructuresFolder, cache, "Villages.dat");
        return cache;
    }

    private static BoundingBoxCache loadNetherStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        loadStructure(localStructuresFolder, cache, "Fortress.dat", "*", BoundingBoxType.NetherFortress);
        loadVillages(localStructuresFolder, cache, "villages_nether.dat");
        return cache;
    }

    private static BoundingBoxCache loadEndStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        loadVillages(localStructuresFolder, cache, "Villages_end.dat");
        loadStructure(localStructuresFolder, cache, "EndCity.dat", "*", BoundingBoxType.EndCity);
        return cache;
    }

    private static void loadStructure(File localStructuresFolder, BoundingBoxCache cache, String fileName, String id, BoundingBoxType type) {
        if(!type.shouldRender()) return;

        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound features = nbt.getCompoundTag("data")
                .getCompoundTag("Features");
        int loadedStructureCount = 0;
        for (Object key : features.getKeySet()) {
            NBTTagCompound feature = features.getCompoundTag((String) key);
            BoundingBox structure = BoundingBoxStructure.from(feature.getIntArray("BB"), type);
            Set<BoundingBox> boundingBoxes = new HashSet<>();
            NBTTagCompound[] children = getChildCompoundTags(feature, "Children");
            for (NBTTagCompound child : children) {
                if (id.equals(child.getString("id")) || id.equals("*"))
                    boundingBoxes.add(BoundingBoxStructure.from(child.getIntArray("BB"), type));
            }
            if (boundingBoxes.size() > 0)
                ++loadedStructureCount;
            cache.addBoundingBoxes(structure, boundingBoxes);
        }

        Logger.info("Loaded %s (%d structures with type %s)", fileName, loadedStructureCount, id);
    }

    private static void loadVillages(File localStructuresFolder, BoundingBoxCache cache, String fileName) {
        if(!BoundingBoxType.Village.shouldRender()) return;

        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound[] villages = getChildCompoundTags(nbt.getCompoundTag("data"), "Villages");
        for (NBTTagCompound village : villages) {
            BlockPos center = new BlockPos(village.getInteger("CX"), village.getInteger("CY"), village.getInteger("CZ"));
            int radius = village.getInteger("Radius");
            int population = village.getInteger("PopSize");
            Set<BlockPos> doors = getDoors(village);
            BoundingBox boundingBox = BoundingBoxVillage.from(center, radius, village.hashCode(), population, doors);
            cache.addBoundingBox(boundingBox);
        }

        Logger.info("Loaded %s (%d villages)", fileName, villages.length);
    }

    private static Set<BlockPos> getDoors(NBTTagCompound village) {
        Set<BlockPos> doors = new HashSet<>();
        for (NBTTagCompound door : getChildCompoundTags(village, "Doors")) {
            doors.add(new BlockPos(door.getInteger("X"), door.getInteger("Y"), door.getInteger("Z")));
        }
        return doors;
    }

    private static NBTTagCompound loadNbtFile(File file) {
        if (!file.exists())
            return null;
        try {
            return CompressedStreamTools.readCompressed(new FileInputStream(file));
        } catch (IOException e) {
            return null;
        }
    }

    private static NBTTagCompound[] getChildCompoundTags(NBTTagCompound parent, String key) {
        NBTTagList tagList = parent.getTagList(key, 10);
        NBTTagCompound[] result = new NBTTagCompound[tagList.size()];
        for (int index = 0; index < tagList.size(); index++) {
            result[index] = tagList.getCompoundTagAt(index);
        }
        return result;
    }
}
