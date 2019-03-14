package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.events.VillageRemoved;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

class VillageProcessor {
    private final DimensionType dimensionType;
    private final BoundingBoxCache boundingBoxCache;

    private Map<Integer, BoundingBoxVillage> villageCache = new HashMap<>();

    VillageProcessor(DimensionType dimensionType, BoundingBoxCache boundingBoxCache) {

        this.dimensionType = dimensionType;
        this.boundingBoxCache = boundingBoxCache;
    }

    void process(VillageCollection villageCollection) {
        Map<Integer, BoundingBoxVillage> oldVillages = new HashMap<>(villageCache);
        Map<Integer, BoundingBoxVillage> newVillages = new HashMap<>();
        for (Village village : villageCollection.getVillageList()) {
            int villageId = village.hashCode();
            BoundingBoxVillage newVillage = oldVillages.get(villageId);
            if (newVillage != null && newVillage.matches(village)) {
                oldVillages.remove(villageId);
            } else {
                newVillage = BoundingBoxVillage.from(village);
                boundingBoxCache.addBoundingBox(newVillage);
            }
            newVillages.put(villageId, newVillage);
        }
        for (BoundingBoxVillage village : oldVillages.values()) {
            boundingBoxCache.removeBoundingBox(village);
            EventBus.publish(new VillageRemoved(dimensionType, village));
        }
        villageCache = newVillages;
    }

    void clear() {
        villageCache.clear();
    }
}