package com.irtimaled.bbor.common;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StructureProcessor {

    public static final Object mutex = new Object();

    private static final Map<String, BoundingBoxType> supportedStructures = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(), mutex);
    public static final Set<String> supportedStructureIds = ObjectSets.synchronize(new ObjectOpenHashSet<>(), mutex);

    public static void registerSupportedStructure(@NotNull BoundingBoxType type) {
        if (!type.getName().startsWith("structure:")) {
            throw new IllegalArgumentException("type need start with \"structure:\"");
        }
        supportedStructures.put(type.getName(), type);
        supportedStructureIds.add(type.getName().substring("structure:".length()));
    }

    StructureProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, Object structureStart) {
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
        if (structures.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : structures.entrySet()) {
            final BoundingBoxType type = supportedStructures.get("structure:" + entry.getKey());
            if (type != null) {
                addStructures(type, entry.getValue());
            }
        }
    }
}
