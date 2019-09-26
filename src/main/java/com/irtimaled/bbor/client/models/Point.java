package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.models.Coords;

public class Point {
    private final double x;
    private final double y;
    private final double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Coords Coords) {
        this.x = Coords.getX();
        this.y = Coords.getY();
        this.z = Coords.getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Point offset(double x, double y, double z) {
        return new Point(this.x + x, this.y + y, this.z + z);
    }

    public double getDistance(Point point) {
        double dx = this.x - point.x;
        double dy = this.y - point.y;
        double dz = this.z - point.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public Coords getCoords() {
        return new Coords(x, y, z);
    }
}
