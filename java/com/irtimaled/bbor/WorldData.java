package com.irtimaled.bbor;

public class WorldData {
    private long seed;
    private int spawnX;
    private int spawnZ;

    public WorldData(long seed, int spawnX, int spawnZ) {
        this.seed = seed;
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    public long getSeed() {
        return seed;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnZ() {
        return spawnZ;
    }
}
