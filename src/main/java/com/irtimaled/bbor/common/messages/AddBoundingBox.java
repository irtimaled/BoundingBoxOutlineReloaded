package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.Set;

public class AddBoundingBox {

    public static final String NAME = "bbor:add_bounding_box_v2";

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
}
