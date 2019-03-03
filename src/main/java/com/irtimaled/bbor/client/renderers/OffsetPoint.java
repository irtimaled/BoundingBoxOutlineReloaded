package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerData;
import net.minecraft.util.math.BlockPos;

public class OffsetPoint {
    private final double x;
    private final double y;
    private final double z;

    OffsetPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    OffsetPoint(BlockPos blockPos) {
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }

    double getX() {
        return x - PlayerData.getX();
    }

    double getY() {
        return y - PlayerData.getY();
    }

    double getZ() {
        return z - PlayerData.getZ();
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
        double dx = this.x - PlayerData.getX();
        double dy = this.y - PlayerData.getY();
        double dz = this.z - PlayerData.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
