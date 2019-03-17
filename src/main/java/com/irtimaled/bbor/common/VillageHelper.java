package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.Coords;

import java.util.Set;

public class VillageHelper {
    public static boolean shouldSpawnIronGolems(int population, int doorCount) {
        return population >= 10 && doorCount >= 21;
    }

    public static int computeHash(Coords center, Integer radius, boolean spawnsIronGolems, Set<Coords> doors) {
        int result = (center.hashCode() * 31) + radius;
        for (Coords door : doors) {
            result = (31 * result) + door.hashCode();
        }
        if (spawnsIronGolems) {
            result = 31 * result;
        }
        return result;
    }
}
