package com.irtimaled.bbor.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class InitializeClientMessage implements IMessage {

    private long seed;

    public static InitializeClientMessage from(long seed) {
        InitializeClientMessage message = new InitializeClientMessage();
        message.seed = seed;
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        seed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(seed);
    }

    public long getSeed() {
        return seed;
    }
}
