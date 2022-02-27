package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.mixin.access.IStructureStart;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StructureProcessor {
    private static final Map<String, BoundingBoxType> supportedStructures = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    public static final Set<String> supportedStructureIds = ObjectSets.synchronize(new ObjectOpenHashSet<>());

    public static void registerSupportedStructure(BoundingBoxType type) {
        supportedStructures.put(type.getName(), type);
    }

    StructureProcessor(BoundingBoxCache boundingBoxCache) {
        this.boundingBoxCache = boundingBoxCache;
    }

    private final BoundingBoxCache boundingBoxCache;

    private void addStructures(BoundingBoxType type, StructureStart structureStart) {
        if (structureStart == null) return;

        try {
            structureStart.getBoundingBox();
        } catch (Throwable ignored) {
        }

        BlockBox bb = ((IStructureStart) structureStart).getBoundingBox1();
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
        Coords min = new Coords(bb.getMinX(), bb.getMinY(), bb.getMinZ());
        Coords max = new Coords(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
        return BoundingBoxCuboid.from(min, max, type);
    }

    void process(Map<String, StructureStart> structures) {
        for (Map.Entry<String, StructureStart> entry : structures.entrySet()) {
            final BoundingBoxType type = supportedStructures.get("structure:" + entry.getKey());
            if (type != null) {
                addStructures(type, entry.getValue());
            }
        }
    }
}
