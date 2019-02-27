package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.RemoveBoundingBoxReceived;
import com.irtimaled.bbor.common.models.BoundingBox;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class RemoveBoundingBox {
    public static final ResourceLocation NAME = new ResourceLocation("bbor:remove_bounding_box");

    public static SPacketCustomPayload getPayload(DimensionType dimensionType, BoundingBox key) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeVarInt(dimensionType.getId());
        BoundingBoxSerializer.serialize(key, buf);

        return new SPacketCustomPayload(NAME, buf);
    }

    public static RemoveBoundingBoxReceived getEvent(PacketBuffer buf) {
        DimensionType dimensionType = DimensionType.getById(buf.readVarInt());
        BoundingBox key = BoundingBoxDeserializer.deserialize(buf);
        return new RemoveBoundingBoxReceived(dimensionType, key);
    }
}
