package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.common.models.Coords;

public class OffsetPoint {
    private final double x;
    private final double y;
    private final double z;

    OffsetPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    OffsetPoint(Coords coords) {
        this.x = coords.getX();
        this.y = coords.getY();
        this.z = coords.getZ();
    }

    double getX() {
        return x - PlayerCoords.getX();
    }

    double getY() {
        return y - PlayerCoords.getY();
    }

    double getZ() {
        return z - PlayerCoords.getZ();
    }

    public OffsetPoint add(double x, double y, double z) {
        return new OffsetPoint(this.x + x, this.y + y, this.z + z);
    }

    public double getDistance(OffsetPoint point) {
        double dx = this.x - point.x;
        double dy = this.y - point.y;
        double dz = this.z - point.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double getDistanceFromPlayer() {
        double dx = this.x - PlayerCoords.getX();
        double dy = this.y - PlayerCoords.getY();
        double dz = this.z - PlayerCoords.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
