package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.AddBoundingBoxReceived;
import com.irtimaled.bbor.common.models.BoundingBox;

import java.util.HashSet;
import java.util.Set;

public class AddBoundingBox {
    public static final String NAME = "bbor:add_bounding_box";

    public static PayloadBuilder getPayload(int dimensionId, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        if (!BoundingBoxSerializer.canSerialize(key)) return null;

        PayloadBuilder builder = PayloadBuilder.clientBound(NAME)
                .writeVarInt(dimensionId);
        BoundingBoxSerializer.serialize(key, builder);
        if (boundingBoxes != null && boundingBoxes.size() > 1) {
            for (BoundingBox boundingBox : boundingBoxes) {
                BoundingBoxSerializer.serialize(boundingBox, builder);
            }
        }
        return builder;
    }

    public static AddBoundingBoxReceived getEvent(PayloadReader reader) {
        int dimensionId = reader.readVarInt();
        BoundingBox key = BoundingBoxDeserializer.deserialize(reader);
        if (key == null) return null;

        Set<BoundingBox> boundingBoxes = new HashSet<>();
        while (reader.isReadable()) {
            BoundingBox boundingBox = BoundingBoxDeserializer.deserialize(reader);
            boundingBoxes.add(boundingBox);
        }
        if (boundingBoxes.size() == 0)
            boundingBoxes.add(key);
        return new AddBoundingBoxReceived(dimensionId, key, boundingBoxes);
    }
}
