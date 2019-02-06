package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

class BoundingBoxSerializer {
    private static final Map<Class, BiConsumer<AbstractBoundingBox, PayloadBuilder>> serializers = new HashMap<>();

    static {
        serializers.put(BoundingBoxStructure.class, (bb, pb) -> serializeStructure((BoundingBoxStructure) bb, pb));
        serializers.put(BoundingBoxMobSpawner.class, (bb, pb) -> serializeMobSpawner((BoundingBoxMobSpawner) bb, pb));
    }

    static boolean canSerialize(AbstractBoundingBox key) {
        return serializers.containsKey(key.getClass());
    }

    static void serialize(AbstractBoundingBox boundingBox, PayloadBuilder builder) {
        BiConsumer<AbstractBoundingBox, PayloadBuilder> serializer = serializers.get(boundingBox.getClass());
        if (serializer == null) return;

        serializer.accept(boundingBox, builder);
    }

    private static void serializeStructure(BoundingBoxStructure boundingBox, PayloadBuilder builder) {
        builder.writeChar('S')
                .writeInt(boundingBox.getTypeName().hashCode())
                .writeCoords(boundingBox.getMinCoords())
                .writeCoords(boundingBox.getMaxCoords());
    }

    private static void serializeMobSpawner(BoundingBoxMobSpawner boundingBox, PayloadBuilder builder) {
        builder.writeChar('M')
                .writeCoords(boundingBox.getCoords());
    }
}
