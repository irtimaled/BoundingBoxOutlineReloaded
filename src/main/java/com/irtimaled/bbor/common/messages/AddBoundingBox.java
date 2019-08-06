package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.client.events.AddBoundingBoxReceived;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashSet;
import java.util.Set;

public class AddBoundingBox {
    public static final String NAME = "bbor:add_bounding_box_v2";
    public static final String LEGACY = "bbor:add_bounding_box";

    public static PayloadBuilder getPayload(DimensionId dimensionId, AbstractBoundingBox key, Set<AbstractBoundingBox> boundingBoxes) {
        if (!BoundingBoxSerializer.canSerialize(key)) return null;

        PayloadBuilder builder = PayloadBuilder.clientBound(NAME)
                .writeDimensionId(dimensionId);
        BoundingBoxSerializer.serialize(key, builder);
        if (boundingBoxes != null && boundingBoxes.size() > 1) {
            for (AbstractBoundingBox boundingBox : boundingBoxes) {
                BoundingBoxSerializer.serialize(boundingBox, builder);
            }
        }
        return builder;
    }

    public static AddBoundingBoxReceived getEvent(PayloadReader reader, String name) {
        DimensionId dimensionId = getDimensionId(reader, name);
        AbstractBoundingBox key = BoundingBoxDeserializer.deserialize(reader);
        if (key == null) return null;

        Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
        while (reader.isReadable()) {
            AbstractBoundingBox boundingBox = BoundingBoxDeserializer.deserialize(reader);
            boundingBoxes.add(boundingBox);
        }
        if (boundingBoxes.size() == 0)
            boundingBoxes.add(key);
        return new AddBoundingBoxReceived(dimensionId, key, boundingBoxes);
    }

    private static DimensionId getDimensionId(PayloadReader reader, String name) {
        if (name.equals(NAME)) return reader.readDimensionId();

        int dimensionRawId = reader.readVarInt();
        DimensionType dimensionType = DimensionType.byRawId(dimensionRawId);
        return DimensionId.from(dimensionType);
    }
}
