package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

class BoundingBoxSerializer {
    private static final Map<Class, BiConsumer<AbstractBoundingBox, PayloadBuilder>> serializers = new HashMap<>();

    static {
        serializers.put(BoundingBoxVillage.class, (bb, pb) -> serializeVillage((BoundingBoxVillage) bb, pb));
        serializers.put(BoundingBoxCuboid.class, (bb, pb) -> serializeStructure((BoundingBoxCuboid)bb, pb));
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

    private static void serializeVillage(BoundingBoxVillage boundingBox, PayloadBuilder builder) {
        builder.writeChar('V')
                .writeCoords(boundingBox.getCenter())
                .writeVarInt(boundingBox.getRadius())
                .writeBoolean(boundingBox.getSpawnsIronGolems())
                .writeVarInt(boundingBox.getColor().getRGB());
        for (Coords door : boundingBox.getDoors()) {
            builder.writeCoords(door);
        }
    }

    private static void serializeStructure(BoundingBoxCuboid boundingBox, PayloadBuilder builder) {
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
