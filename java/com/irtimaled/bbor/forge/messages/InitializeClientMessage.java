package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.common.models.WorldData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class InitializeClientMessage implements IMessage {
    private WorldData worldData;

    public static InitializeClientMessage from(WorldData worldData) {
        InitializeClientMessage message = new InitializeClientMessage();
        message.worldData = new WorldData(worldData.getSeed(), worldData.getSpawnX(), worldData.getSpawnZ());
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long seed = buf.readLong();
        int spawnX = buf.readInt();
        int spawnZ = buf.readInt();
        worldData = new WorldData(seed, spawnX, spawnZ);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(worldData.getSeed());
        buf.writeInt(worldData.getSpawnX());
        buf.writeInt(worldData.getSpawnZ());
    }

    public WorldData getWorldData() {
        return worldData;
    }
}
