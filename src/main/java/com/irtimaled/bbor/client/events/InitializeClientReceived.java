package com.irtimaled.bbor.client.events;

public class InitializeClientReceived {
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public InitializeClientReceived(long seed, int spawnX, int spawnZ) {
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
