package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StructureProcessor {

    public static final Set<BoundingBoxType> supportedStructures = new HashSet<>();

    public static void registerSupportedStructure(BoundingBoxType type) {
        supportedStructures.add(type);
    }

    StructureProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, Map<String, StructureStart> structureMap) {
        StructureStart structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;

        StructureBoundingBox bb = structureStart.a();
        if (bb == null) return;


        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.i()) {
            structureBoundingBoxes.add(buildStructure(structureComponent.f(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private AbstractBoundingBox buildStructure(StructureBoundingBox bb, BoundingBoxType type) {
        Coords min = new Coords(bb.g(), bb.h(), bb.i());
        Coords max = new Coords(bb.j(), bb.k(), bb.l());
        return BoundingBoxCuboid.from(min, max, type);
    }

    void process(Map<String, StructureStart> structures) {
        if (structures.size() > 0) {
            supportedStructures.forEach(type -> addStructures(type, structures));
        }
    }
}
