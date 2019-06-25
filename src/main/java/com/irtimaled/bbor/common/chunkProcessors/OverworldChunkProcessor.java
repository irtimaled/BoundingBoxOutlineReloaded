package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.world.gen.structure.*;

public class OverworldChunkProcessor extends AbstractChunkProcessor {
    public OverworldChunkProcessor(BoundingBoxCache boundingBoxCache) {
        super(boundingBoxCache);
        supportedStructures.put(BoundingBoxType.DesertTemple, (chunkGenerator) -> getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.DesertPyramid.class));
        supportedStructures.put(BoundingBoxType.JungleTemple, (chunkGenerator) -> getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.JunglePyramid.class));
        supportedStructures.put(BoundingBoxType.WitchHut, (chunkGenerator) -> getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.SwampHut.class));
        supportedStructures.put(BoundingBoxType.OceanMonument, (chunkGenerator) -> getStructures(chunkGenerator, StructureOceanMonument.class));
        supportedStructures.put(BoundingBoxType.Stronghold, (chunkGenerator) -> getStructures(chunkGenerator, MapGenStronghold.class));
        supportedStructures.put(BoundingBoxType.Mansion, (chunkGenerator) -> getStructures(chunkGenerator, WoodlandMansion.class));
        supportedStructures.put(BoundingBoxType.MineShaft, (chunkGenerator) -> getStructures(chunkGenerator, MapGenMineshaft.class));
        supportedStructures.put(BoundingBoxType.Igloo, (chunkGenerator) -> getStructuresWithComponent(getStructures(chunkGenerator, MapGenScatteredFeature.class), ComponentScatteredFeaturePieces.Igloo.class));
    }
}
