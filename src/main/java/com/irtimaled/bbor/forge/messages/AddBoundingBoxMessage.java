package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.common.models.BoundingBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.HashSet;
import java.util.Set;

public class AddBoundingBoxMessage implements IMessage {
    private DimensionType dimensionType;
    private BoundingBox key;
    private Set<BoundingBox> boundingBoxes;

    public static AddBoundingBoxMessage from(DimensionType dimensionType, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        AddBoundingBoxMessage message = new AddBoundingBoxMessage();
        message.dimensionType = dimensionType;
        message.key = key;
        message.boundingBoxes = boundingBoxes;
        return message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionType = DimensionType.getById(ByteBufUtils.readVarInt(buf, 5));
        key = BoundingBoxDeserializer.deserialize(buf);
        boundingBoxes = new HashSet<>();
        while (buf.isReadable()) {
            BoundingBox boundingBox = BoundingBoxDeserializer.deserialize(buf);
            boundingBoxes.add(boundingBox);
        }
        if (boundingBoxes.size() == 0)
            boundingBoxes.add(key);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, dimensionType.getId(), 5);
        BoundingBoxSerializer.serialize(key, buf);
        if (boundingBoxes != null &&
                boundingBoxes.size() > 1) {
            for (BoundingBox boundingBox : boundingBoxes) {
                BoundingBoxSerializer.serialize(boundingBox, buf);
            }
        }
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    BoundingBox getKey() {
        return key;
    }

    Set<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }
}
