package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.RemoveBoundingBoxReceived;
import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.dimension.DimensionType;

public class RemoveBoundingBox {
    public static final String NAME = "bbor:remove_bounding_box";

    public static PayloadBuilder getPayload(DimensionType dimensionType, BoundingBox key) {
        if (!BoundingBoxSerializer.canSerialize(key)) return null;

        PayloadBuilder builder = PayloadBuilder.clientBound(NAME)
                .writeVarInt(dimensionType.getId());
        BoundingBoxSerializer.serialize(key, builder);
        return builder;
    }

    public static RemoveBoundingBoxReceived getEvent(PayloadReader reader) {
        int dimensionId = reader.readVarInt();
        BoundingBox key = BoundingBoxDeserializer.deserialize(reader);
        if (key == null) return null;

        return new RemoveBoundingBoxReceived(DimensionType.getById(dimensionId), key);
    }
}
