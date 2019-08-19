package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;

public class OverworldChunkProcessor extends AbstractChunkProcessor {
    public OverworldChunkProcessor(BoundingBoxCache boundingBoxCache) {
        super(boundingBoxCache);
        supportedStructures.add(BoundingBoxType.Village);
        supportedStructures.add(BoundingBoxType.DesertTemple);
        supportedStructures.add(BoundingBoxType.JungleTemple);
        supportedStructures.add(BoundingBoxType.WitchHut);
        supportedStructures.add(BoundingBoxType.OceanMonument);
        supportedStructures.add(BoundingBoxType.Stronghold);
        supportedStructures.add(BoundingBoxType.Mansion);
        supportedStructures.add(BoundingBoxType.MineShaft);
        supportedStructures.add(BoundingBoxType.Shipwreck);
        supportedStructures.add(BoundingBoxType.OceanRuin);
        supportedStructures.add(BoundingBoxType.BuriedTreasure);
        supportedStructures.add(BoundingBoxType.Igloo);
        supportedStructures.add(BoundingBoxType.PillagerOutpost);
    }
}
