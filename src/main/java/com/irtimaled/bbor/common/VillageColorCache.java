package com.irtimaled.bbor.common;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VillageColorCache {
    private static int colorIndex = -1;

    public static void clear() {
        colorIndex = -1;
        villageColorCache.clear();
    }

    private static Color getNextColor() {
        switch (++colorIndex % 6) {
            case 0:
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.MAGENTA;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.CYAN;
        }
        return Color.WHITE;
    }

    private static final Map<Integer, Color> villageColorCache = new HashMap<>();

    public static Color getColor(int villageId) {
        return villageColorCache.computeIfAbsent(villageId, k -> getNextColor());
    }
}
