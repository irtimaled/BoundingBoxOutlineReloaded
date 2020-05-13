package com.irtimaled.bbor.common;

public class MathHelper {
    public static int floor(double value) {
        int intValue = (int) value;
        return value >= intValue ? intValue : intValue - 1;
    }

    public static double snapToNearest(double value, double nearest) {
        double multiplier = 2.0 / nearest;
        int floor = floor(value);
        int fraction = floor((value - floor) * multiplier);
        int midpoint = (int) (multiplier / 2);
        if (fraction % midpoint == 1) fraction++;
        return floor + (fraction / multiplier);
    }
}
