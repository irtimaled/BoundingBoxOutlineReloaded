package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.common.models.BoundingBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RemoveBoundingBoxMessage implements IMessage {
    private DimensionType dimensionType;
    private BoundingBox key;

    public static RemoveBoundingBoxMessage from(DimensionType dimensionType, BoundingBox key) {
        RemoveBoundingBoxMessage message = new RemoveBoundingBoxMessage();
        message.dimensionType = dimensionType;
        message.key = key;
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionType = DimensionType.getById(ByteBufUtils.readVarInt(buf, 5));
        key = BoundingBoxDeserializer.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, dimensionType.getId(), 5);
        BoundingBoxSerializer.serialize(key, buf);
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    BoundingBox getKey() {
        return key;
    }
}
