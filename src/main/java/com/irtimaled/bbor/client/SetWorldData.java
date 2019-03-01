package com.irtimaled.bbor.client;

@FunctionalInterface
interface SetWorldData {
    void accept(Long seed, Integer spawnX, Integer spawnZ);
}
