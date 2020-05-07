package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.BoundingBoxSphere;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxConduit extends BoundingBoxSphere {
    private final int level;

    private BoundingBoxConduit(Coords coords, int level, int radius) {
        super(coords, radius, BoundingBoxType.Conduit);
        setCenterOffsets(0.5, 0.5, 0.5);

        this.level = level;
    }

    public static BoundingBoxConduit from(Coords coords, int level) {
        int radius = 16 * level;
        return new BoundingBoxConduit(coords, level, radius);
    }

    @Override
    public int hashCode() {
        return TypeHelper.combineHashCodes(getType().hashCode(), getCenter().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBoxConduit other = (BoundingBoxConduit) obj;
        return getCenter().equals(other.getCenter());
    }

    public int getLevel() {
        return level;
    }
}
