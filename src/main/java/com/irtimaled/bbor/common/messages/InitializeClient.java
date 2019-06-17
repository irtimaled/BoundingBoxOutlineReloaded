package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.InitializeClientReceived;

public class InitializeClient {
    public static final String NAME = "bbor:initialize";

    public static PayloadBuilder getPayload(long seed, int spawnX, int spawnZ) {
        return PayloadBuilder.clientBound(NAME)
                .writeLong(seed)
                .writeInt(spawnX)
                .writeInt(spawnZ);
    }

    public static InitializeClientReceived getEvent(PayloadReader reader) {
        long seed = reader.readLong();
        int spawnX = reader.readInt();
        int spawnZ = reader.readInt();
        return new InitializeClientReceived(seed, spawnX, spawnZ);
    }
}
