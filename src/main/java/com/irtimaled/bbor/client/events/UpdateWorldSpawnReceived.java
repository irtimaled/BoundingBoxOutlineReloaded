package com.irtimaled.bbor.client.events;

public class UpdateWorldSpawnReceived {
    private final int spawnX;
    private final int spawnZ;

    public UpdateWorldSpawnReceived(int spawnX, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnZ() {
        return spawnZ;
    }
}
