package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.BoundingBox;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RemoveBoundingBoxMessage implements IMessage {
    private int dimension;
    private BoundingBox key;

    public static RemoveBoundingBoxMessage from(int dimension, BoundingBox key) {
        RemoveBoundingBoxMessage message = new RemoveBoundingBoxMessage();
        message.dimension = dimension;
        message.key = key;
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimension = ByteBufUtils.readVarInt(buf, 5);
        key = BoundingBoxDeserializer.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, dimension, 5);
        BoundingBoxSerializer.serialize(key, buf);
    }

    public int getDimension() {
        return dimension;
    }

    public BoundingBox getKey() {
        return key;
    }
}
