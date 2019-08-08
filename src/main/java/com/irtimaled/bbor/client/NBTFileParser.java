package com.irtimaled.bbor.client;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.Dimensions;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class NBTFileParser {
    static void loadLocalDatFiles(String host, int port, SetWorldData setWorldData, GetCache createCache) {
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
        loadWorldData(localStructuresFolder, setWorldData);
        populateBoundingBoxCache(localStructuresFolder, createCache);
    }

    private static void loadWorldData(File localStructuresFolder, SetWorldData setWorldData) {
        File file = new File(localStructuresFolder, "level.dat");
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound data = nbt.getCompoundTag("Data");
        long seed = data.getLong("RandomSeed");
        int spawnX = data.getInteger("SpawnX");
        int spawnZ = data.getInteger("SpawnZ");
        Logger.info("Loaded level.dat (seed: %d, spawn: %d,%d)", seed, spawnX, spawnZ);
        setWorldData.accept(seed, spawnX, spawnZ);
    }

    private static void populateBoundingBoxCache(File localStructuresFolder, GetCache createCache) {
        loadOverworldStructures(localStructuresFolder, createCache.apply(Dimensions.OVERWORLD));
        loadNetherStructures(localStructuresFolder, createCache.apply(Dimensions.NETHER));
        loadEndStructures(localStructuresFolder, createCache.apply(Dimensions.THE_END));
    }

    private static void loadOverworldStructures(File localStructuresFolder, BoundingBoxCache cache) {
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeDP", BoundingBoxType.DesertTemple);
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeJP", BoundingBoxType.JungleTemple);
        loadStructure(localStructuresFolder, cache, "Temple.dat", "TeSH", BoundingBoxType.WitchHut);
        loadStructure(localStructuresFolder, cache, "Monument.dat", "*", BoundingBoxType.OceanMonument);
        loadStructure(localStructuresFolder, cache, "Stronghold.dat", "*", BoundingBoxType.Stronghold);
        loadStructure(localStructuresFolder, cache, "Mansion.dat", "*", BoundingBoxType.Mansion);
        loadStructure(localStructuresFolder, cache, "Mineshaft.dat", "*", BoundingBoxType.MineShaft);
        loadVillages(localStructuresFolder, cache, "Villages.dat");
    }

    private static void loadNetherStructures(File localStructuresFolder, BoundingBoxCache cache) {
        loadStructure(localStructuresFolder, cache, "Fortress.dat", "*", BoundingBoxType.NetherFortress);
        loadVillages(localStructuresFolder, cache, "villages_nether.dat");
    }

    private static void loadEndStructures(File localStructuresFolder, BoundingBoxCache cache) {
        loadVillages(localStructuresFolder, cache, "Villages_end.dat");
        loadStructure(localStructuresFolder, cache, "EndCity.dat", "*", BoundingBoxType.EndCity);
    }

    private static void loadStructure(File localStructuresFolder, BoundingBoxCache cache, String fileName, String id, BoundingBoxType type) {
        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound features = nbt.getCompoundTag("data")
                .getCompoundTag("Features");
        int loadedStructureCount = 0;
        for (Object key : features.getKeySet()) {
            NBTTagCompound feature = features.getCompoundTag((String) key);
            AbstractBoundingBox structure = buildStructure(feature, type);
            Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
            NBTTagCompound[] children = getChildCompoundTags(feature, "Children");
            for (NBTTagCompound child : children) {
                if (id.equals(child.getString("id")) || id.equals("*"))
                    boundingBoxes.add(buildStructure(child, type));
            }
            if (boundingBoxes.size() > 0)
                ++loadedStructureCount;
            cache.addBoundingBoxes(structure, boundingBoxes);
        }

        Logger.info("Loaded %s (%d structures with type %s)", fileName, loadedStructureCount, id);
    }

    private static BoundingBoxStructure buildStructure(NBTTagCompound feature, BoundingBoxType type) {
        int[] bb = feature.getIntArray("BB");
        Coords minCoords = new Coords(bb[0], bb[1], bb[2]);
        Coords maxCoords = new Coords(bb[3], bb[4], bb[5]);
        return BoundingBoxStructure.from(minCoords, maxCoords, type);
    }

    private static void loadVillages(File localStructuresFolder, BoundingBoxCache cache, String fileName) {
        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound[] villages = getChildCompoundTags(nbt.getCompoundTag("data"), "Villages");
        for (NBTTagCompound village : villages) {
            Coords center = new Coords(village.getInteger("CX"), village.getInteger("CY"), village.getInteger("CZ"));
            int radius = village.getInteger("Radius");
            int population = village.getInteger("PopSize");
            Set<Coords> doors = getDoors(village);
            AbstractBoundingBox boundingBox = BoundingBoxVillage.from(center, radius, village.hashCode(), population, doors);
            cache.addBoundingBox(boundingBox);
        }

        Logger.info("Loaded %s (%d villages)", fileName, villages.length);
    }

    private static Set<Coords> getDoors(NBTTagCompound village) {
        Set<Coords> doors = new HashSet<>();
        for (NBTTagCompound door : getChildCompoundTags(village, "Doors")) {
            doors.add(new Coords(door.getInteger("X"), door.getInteger("Y"), door.getInteger("Z")));
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
        NBTTagCompound[] result = new NBTTagCompound[tagList.tagCount()];
        for (int index = 0; index < tagList.tagCount(); index++) {
            result[index] = tagList.getCompoundTagAt(index);
        }
        return result;
    }
}
