package com.irtimaled.bbor.common;

public class MathHelper {
    public static int floor(double value) {
        int intValue = (int) value;
        return value >= intValue ? intValue : intValue - 1;
    }
}
