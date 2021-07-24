package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.mixin.access.IStructureStart;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;

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

    private void addStructures(BoundingBoxType type, StructureStart<?> structureStart) {
        if (structureStart == null) return;

        BlockBox bb = ((IStructureStart) structureStart).getBoundingBox();
        if (bb == null) return;

        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (StructurePiece structureComponent : structureStart.getChildren()) {
            structureBoundingBoxes.add(buildStructure(structureComponent.getBoundingBox(), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    private AbstractBoundingBox buildStructure(BlockBox bb, BoundingBoxType type) {
        Coords min = new Coords(bb.getMaxZ(), bb.getMinX(), bb.getMinY());
        Coords max = new Coords(bb.getMinZ(), bb.getMaxX(), bb.getMaxY());
        return BoundingBoxCuboid.from(min, max, type);
    }

    void process(Map<String, StructureStart<?>> structures) {
        if (structures.size() > 0) {
            supportedStructures.forEach(type -> addStructures(type, structures.get(type.getName())));
        }
    }
}
