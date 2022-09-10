package com.irtimaled.bbor.common;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import org.jetbrains.annotations.NotNull;

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

    private void addStructures(BoundingBoxType type, Map<String, Object> structureMap) {
        Object structureStart = structureMap.get(type.getName());
        if (structureStart == null) return;

        Object bb = NMSHelper.structureStartGetBox(structureStart);
        if (bb == null) return;


        AbstractBoundingBox boundingBox = buildStructure(bb, type);
        if (boundingBoxCache.isCached(boundingBox)) return;

        Set<AbstractBoundingBox> structureBoundingBoxes = new HashSet<>();
        for (Object structureComponent : NMSHelper.structureStartGetPiece(structureStart)) {
            structureBoundingBoxes.add(buildStructure(NMSHelper.structurePieceGetBox(structureComponent), type));
        }
        boundingBoxCache.addBoundingBoxes(boundingBox, structureBoundingBoxes);
    }

    @NotNull
    private AbstractBoundingBox buildStructure(Object bb, BoundingBoxType type) {
        Coords min = new Coords(NMSHelper.structureBoundingBoxGetMinX(bb), NMSHelper.structureBoundingBoxGetMinY(bb), NMSHelper.structureBoundingBoxGetMinZ(bb));
        Coords max = new Coords(NMSHelper.structureBoundingBoxGetMaxX(bb), NMSHelper.structureBoundingBoxGetMaxY(bb), NMSHelper.structureBoundingBoxGetMaxZ(bb));
        return BoundingBoxCuboid.from(min, max, type);
    }

    void process(@NotNull Map<String, Object> structures) {
        if (structures.size() > 0) {
            supportedStructures.forEach(type -> addStructures(type, structures));
        }
    }
}
