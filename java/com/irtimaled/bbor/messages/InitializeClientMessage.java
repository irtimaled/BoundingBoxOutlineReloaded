package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.IWorldData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class InitializeClientMessage implements IMessage, IWorldData {

    private long seed;
    private int spawnX;
    private int spawnZ;

    public static InitializeClientMessage from(long seed, int spawnX, int spawnZ) {
        InitializeClientMessage message = new InitializeClientMessage();
        message.seed = seed;
        message.spawnX = spawnX;
        message.spawnZ = spawnZ;
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        seed = buf.readLong();
        spawnX = buf.readInt();
        spawnZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(seed);
        buf.writeInt(spawnX);
        buf.writeInt(spawnZ);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public int getSpawnX() { return spawnX; }

    @Override
    public int getSpawnZ() { return spawnZ; }
}
