package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashSet;
import java.util.Set;

public class BoundingBoxSpawnableBlocks extends AbstractBoundingBox {
    private final Set<Coords> blocks = new HashSet<>();

    public BoundingBoxSpawnableBlocks() {
        super(BoundingBoxType.SpawnableBlocks);
    }

    public Set<Coords> getBlocks() {
        return blocks;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return true;
    }
}
