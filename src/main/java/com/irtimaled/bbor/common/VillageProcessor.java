package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillageProcessor {
    private World world;
    private DimensionType dimensionType;
    private IVillageEventHandler eventHandler;
    private BoundingBoxCache boundingBoxCache;
    private Map<Integer, BoundingBoxVillage> villageCache = new HashMap<>();
    private boolean closed = false;

    VillageProcessor(World world, DimensionType dimensionType, IVillageEventHandler eventHandler, BoundingBoxCache boundingBoxCache) {
        this.world = world;
        this.dimensionType = dimensionType;
        this.eventHandler = eventHandler;
        this.boundingBoxCache = boundingBoxCache;
    }

    synchronized void process() {
        if (closed) return;

        Map<Integer, BoundingBoxVillage> oldVillages = new HashMap<>(villageCache);
        Map<Integer, BoundingBoxVillage> newVillages = new HashMap<>();
        VillageCollection villageCollection = world.getVillageCollection();
        if (villageCollection != null) {
            List<Village> villages = villageCollection.getVillageList();
            for(int i = 0; i < villages.size(); i++) {
                Village village = villages.get(i);
                int villageId = village.hashCode();
                BoundingBoxVillage newVillage = oldVillages.get(villageId);
                if (newVillage != null && newVillage.matches(village)) {
                    oldVillages.remove(villageId);
                } else {
                    newVillage = BoundingBoxVillage.from(village);
                }
                newVillages.put(villageId, newVillage);
            }

        }
        for (BoundingBoxVillage village : oldVillages.values()) {
            boundingBoxCache.removeBoundingBox(village);
            if (eventHandler != null) {
                eventHandler.villageRemoved(dimensionType, village);
            }
        }
        for (BoundingBoxVillage village : newVillages.values()) {
            boundingBoxCache.addBoundingBox(village);
        }
        villageCache = newVillages;
    }

    public void close() {
        closed = true;
        world = null;
        eventHandler = null;
        boundingBoxCache = null;
        villageCache.clear();
    }
}
