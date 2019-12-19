package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.server.v1_16_R3.StructureBoundingBox;
import net.minecraft.server.v1_16_R3.StructurePiece;
import net.minecraft.server.v1_16_R3.StructureStart;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class StructureProcessor {
    private static final Set<BoundingBoxType> supportedStructures = new HashSet<>();

    static void registerSupportedStructure(BoundingBoxType type) {
        supportedStructures.add(type);
    }

    StructureProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, Map<String, StructureStart<?>> structureMap) {
        StructureStart<?> structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;

        StructureBoundingBox bb = structureStart.c();
        if (bb == null) return;

        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.d()) {
            structureBoundingBoxes.add(buildStructure(structureComponent.g(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private AbstractBoundingBox buildStructure(StructureBoundingBox bb, BoundingBoxType type) {
        Coords min = new Coords(bb.a, bb.b, bb.c);
        Coords max = new Coords(bb.d, bb.e, bb.f);
        return BoundingBoxCuboid.from(min, max, type);
    }

    void process(Map<String, StructureStart<?>> structures) {
        if (structures.size() > 0) {
            supportedStructures.forEach(type -> addStructures(type, structures));
        }
    }
}
