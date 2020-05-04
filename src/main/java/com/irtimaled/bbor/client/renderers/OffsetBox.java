package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.Coords;

class OffsetBox {
    private final OffsetPoint min;
    private final OffsetPoint max;

    OffsetBox(Coords minCoords, Coords maxCoords) {
        this.min = new OffsetPoint(minCoords);
        this.max = new OffsetPoint(maxCoords).offset(1, 1, 1);
    }

    OffsetBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.min = new OffsetPoint(minX, minY, minZ);
        this.max = new OffsetPoint(maxX, maxY, maxZ);
    }

    OffsetBox(OffsetPoint min, OffsetPoint max) {
        this.min = min;
        this.max = max;
    }

    OffsetBox grow(double x, double y, double z) {
        return new OffsetBox(min.offset(-x, -y, -z), max.offset(x, y, z));
    }

    OffsetBox offset(double x, double y, double z) {
        return new OffsetBox(min.offset(x, y, z), max.offset(x, y, z));
    }

    OffsetBox nudge() {
        double growXZ = 0.001F;
        if (min.getY() == max.getY()) {
            return grow(growXZ, 0, growXZ).offset(0, growXZ, 0);
        }
        return grow(growXZ, growXZ, growXZ);
    }

    OffsetPoint getMin() {
        return min;
    }

    OffsetPoint getMax() {
        return max;
    }
}
