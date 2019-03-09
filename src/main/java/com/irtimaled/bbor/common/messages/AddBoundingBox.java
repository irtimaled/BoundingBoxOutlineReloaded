package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.AddBoundingBoxReceived;
import com.irtimaled.bbor.common.models.BoundingBox;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashSet;
import java.util.Set;

public class AddBoundingBox {
    public static final ResourceLocation NAME = new ResourceLocation("bbor:add_bounding_box");

    public static SPacketCustomPayload getPayload(DimensionType dimensionType, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        if(!BoundingBoxSerializer.canSerialize(key)) return null;

        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeVarInt(dimensionType.getId());
        BoundingBoxSerializer.serialize(key, buf);
        if (boundingBoxes != null && boundingBoxes.size() > 1) {
            for (BoundingBox boundingBox : boundingBoxes) {
                BoundingBoxSerializer.serialize(boundingBox, buf);
            }
        }
        return new SPacketCustomPayload(NAME, buf);
    }

    public static AddBoundingBoxReceived getEvent(PacketBuffer buf) {
        DimensionType dimensionType = DimensionType.getById(buf.readVarInt());
        BoundingBox key = BoundingBoxDeserializer.deserialize(buf);
        if (key == null) return null;

        Set<BoundingBox> boundingBoxes = new HashSet<>();
        while (buf.isReadable()) {
            BoundingBox boundingBox = BoundingBoxDeserializer.deserialize(buf);
            boundingBoxes.add(boundingBox);
        }
        if (boundingBoxes.size() == 0)
            boundingBoxes.add(key);
        return new AddBoundingBoxReceived(dimensionType, key, boundingBoxes);
    }
}
