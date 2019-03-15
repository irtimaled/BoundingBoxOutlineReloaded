package com.irtimaled.bbor.common.messages;


public class InitializeClient {
    public static final String NAME = "bbor:initialize";

    public static PayloadBuilder getPayload(long seed, int spawnX, int spawnZ) {
        return PayloadBuilder.clientBound(NAME)
                .writeLong(seed)
                .writeInt(spawnX)
                .writeInt(spawnZ);
    }
}
