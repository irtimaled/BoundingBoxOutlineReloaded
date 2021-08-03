package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.BeaconRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxBeacon extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxBeacon> RENDERER = ClientRenderer.registerRenderer(BoundingBoxBeacon.class, new BeaconRenderer());

    private final Coords coords;
    private final int level;

    private BoundingBoxBeacon(Coords coords, Coords minCoords, Coords maxCoords, int level, BoundingBoxType type) {
        super(minCoords, maxCoords, type);
        this.coords = coords;
        this.level = level;
    }

    public static BoundingBoxBeacon from(Coords coords, int level) {
        return from(coords, level, BoundingBoxType.Beacon);
    }

    public static BoundingBoxBeacon from(Coords coords, int level, BoundingBoxType type) {
        int range = 10 + (10 * level);
        Coords minCoords = new Coords(coords.getX() - range, coords.getY() - range, coords.getZ() - range);
        Coords maxCoords = new Coords(coords.getX() + range, 324 + range, coords.getZ() + range);
        return new BoundingBoxBeacon(coords, minCoords, maxCoords, level, type);
    }

    @Override
    public int hashCode() {
        return coords.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBoxBeacon other = (BoundingBoxBeacon) obj;
        return coords.equals(other.coords);
    }

    public Coords getCoords() {
        return coords;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
