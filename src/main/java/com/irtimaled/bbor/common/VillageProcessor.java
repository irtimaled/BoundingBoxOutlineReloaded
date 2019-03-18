package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.events.VillageRemoved;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageDoorInfo;

import java.util.*;

class VillageProcessor {
    private final BoundingBoxCache boundingBoxCache;

    private Map<Integer, BoundingBoxVillage> villageCache = new HashMap<>();
    private int dimensionId;

    VillageProcessor(int dimensionId, BoundingBoxCache boundingBoxCache) {
        this.dimensionId = dimensionId;
        this.boundingBoxCache = boundingBoxCache;
    }

    void process(VillageCollection villageCollection) {
        Map<Integer, BoundingBoxVillage> oldVillages = new HashMap<>(villageCache);
        Map<Integer, BoundingBoxVillage> newVillages = new HashMap<>();
        for (Village village : villageCollection.getVillageList()) {
            int villageId = village.hashCode();
            BoundingBoxVillage newVillage = oldVillages.get(villageId);
            if (areEquivalent(village, newVillage)) {
                oldVillages.remove(villageId);
            } else {
                newVillage = buildBoundingBox(village);
                boundingBoxCache.addBoundingBox(newVillage);
            }
            newVillages.put(villageId, newVillage);
        }
        for (BoundingBoxVillage village : oldVillages.values()) {
            boundingBoxCache.removeBoundingBox(village);
            EventBus.publish(new VillageRemoved(dimensionId, village));
        }
        villageCache = newVillages;
    }

    private static Set<Coords> getDoorsFromVillage(Village village) {
        Set<Coords> doors = new HashSet<>();
        List<VillageDoorInfo> doorInfoList = village.getVillageDoorInfoList();
        for (VillageDoorInfo doorInfo : doorInfoList) {
            doors.add(new Coords(doorInfo.getDoorBlockPos()));
        }
        return doors;
    }

    private boolean areEquivalent(Village village, BoundingBoxVillage newVillage) {
        if (newVillage == null) return false;
        Coords center = new Coords(village.getCenter());
        int radius = village.getVillageRadius();
        boolean spawnsIronGolems = VillageHelper.shouldSpawnIronGolems(village.getNumVillagers(), village.getNumVillageDoors());
        Set<Coords> doors = getDoorsFromVillage(village);
        int villageHash = VillageHelper.computeHash(center, radius, spawnsIronGolems, doors);
        return newVillage.getVillageHash() == villageHash;
    }

    private BoundingBoxVillage buildBoundingBox(Village village) {
        Coords center = new Coords(village.getCenter());
        int radius = village.getVillageRadius();
        Set<Coords> doors = getDoorsFromVillage(village);
        return BoundingBoxVillage.from(center, radius, village.hashCode(), village.getNumVillagers(), doors);
    }

    void clear() {
        villageCache.clear();
    }
}