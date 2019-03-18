package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.InitializeClientReceived;
import com.irtimaled.bbor.common.models.WorldData;

public class InitializeClient {
    public static final String NAME = "bbor:initialize";

    public static PayloadBuilder getPayload(WorldData worldData) {
        return PayloadBuilder.clientBound(NAME)
                .writeLong(worldData.getSeed())
                .writeInt(worldData.getSpawnX())
                .writeInt(worldData.getSpawnZ());
    }

    public static InitializeClientReceived getEvent(PayloadReader reader) {
        long seed = reader.readLong();
        int spawnX = reader.readInt();
        int spawnZ = reader.readInt();
        return new InitializeClientReceived(seed, spawnX, spawnZ);
    }
}
