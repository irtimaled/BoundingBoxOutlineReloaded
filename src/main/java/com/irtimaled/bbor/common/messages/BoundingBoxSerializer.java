package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.*;
import net.minecraft.network.PacketBuffer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

class BoundingBoxSerializer {
    private static final Map<Class, BiConsumer<BoundingBox, PacketBuffer>> serializers = new HashMap<>();

    static {
        serializers.put(BoundingBoxVillage.class, (bb, buf) -> serializeVillage((BoundingBoxVillage) bb, buf));
        serializers.put(BoundingBoxStructure.class, (bb, buf) -> serializeStructure((BoundingBoxStructure) bb, buf));
        serializers.put(BoundingBoxMobSpawner.class, (bb, buf) -> serializeMobSpawner((BoundingBoxMobSpawner) bb, buf));
    }

    static boolean canSerialize(BoundingBox key) {
        return serializers.containsKey(key.getClass());
    }

    static void serialize(BoundingBox boundingBox, PacketBuffer buf) {
        BiConsumer<BoundingBox, PacketBuffer> serializer = serializers.get(boundingBox.getClass());
        if (serializer == null) return;

        serializer.accept(boundingBox, buf);
    }

    private static void serializeVillage(BoundingBoxVillage boundingBox, PacketBuffer buf) {
        buf.writeChar('V');
        serializeCoords(boundingBox.getCenter(), buf);
        buf.writeVarInt(boundingBox.getRadius());
        buf.writeBoolean(boundingBox.getSpawnsIronGolems());
        serializeColor(boundingBox.getColor(), buf);
        for (Coords door : boundingBox.getDoors()) {
            serializeCoords(door, buf);
        }
    }

    private static void serializeStructure(BoundingBoxStructure boundingBox, PacketBuffer buf) {
        buf.writeChar('S');
        buf.writeInt(boundingBox.getTypeName().hashCode());
        serializeCuboid(boundingBox, buf);
    }

    private static void serializeMobSpawner(BoundingBoxMobSpawner boundingBox, PacketBuffer buf) {
        buf.writeChar('M');
        serializeCoords(boundingBox.getCoords(), buf);
    }

    private static void serializeColor(Color color, PacketBuffer buf) {
        buf.writeVarInt(color.getRGB());
    }

    private static void serializeCuboid(BoundingBox boundingBox, PacketBuffer buf) {
        serializeCoords(boundingBox.getMinCoords(), buf);
        serializeCoords(boundingBox.getMaxCoords(), buf);
    }

    private static void serializeCoords(Coords coords, PacketBuffer buf) {
        buf.writeVarInt(coords.getX());
        buf.writeVarInt(coords.getY());
        buf.writeVarInt(coords.getZ());
    }
}
