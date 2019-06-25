package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.RemoveBoundingBoxReceived;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;

public class RemoveBoundingBox {
    public static final String NAME = "bbor:removebb";

    public static PayloadBuilder getPayload(int dimensionId, AbstractBoundingBox key) {
        if (!BoundingBoxSerializer.canSerialize(key)) return null;

        PayloadBuilder builder = PayloadBuilder.clientBound(NAME)
                .writeVarInt(dimensionId);
        BoundingBoxSerializer.serialize(key, builder);
        return builder;
    }

    public static RemoveBoundingBoxReceived getEvent(PayloadReader reader) {
        int dimensionId = reader.readVarInt();
        AbstractBoundingBox key = BoundingBoxDeserializer.deserialize(reader);
        if (key == null) return null;

        return new RemoveBoundingBoxReceived(dimensionId, key);
    }
}
