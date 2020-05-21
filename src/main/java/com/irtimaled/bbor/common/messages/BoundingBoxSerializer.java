package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

class BoundingBoxSerializer {
    private static final Map<Class, BiConsumer<AbstractBoundingBox, PayloadBuilder>> serializers = new HashMap<>();

    static {
        serializers.put(BoundingBoxCuboid.class, (bb, pb) -> serializeStructure((BoundingBoxCuboid) bb, pb));
    }

    static boolean canSerialize(AbstractBoundingBox key) {
        return serializers.containsKey(key.getClass());
    }

    static void serialize(AbstractBoundingBox boundingBox, PayloadBuilder builder) {
        BiConsumer<AbstractBoundingBox, PayloadBuilder> serializer = serializers.get(boundingBox.getClass());
        if (serializer == null) return;

        serializer.accept(boundingBox, builder);
    }

    private static void serializeStructure(BoundingBoxCuboid boundingBox, PayloadBuilder builder) {
        builder.writeChar('S')
                .writeInt(boundingBox.getType().hashCode())
                .writeCoords(boundingBox.getMinCoords())
                .writeCoords(boundingBox.getMaxCoords());
    }
}
