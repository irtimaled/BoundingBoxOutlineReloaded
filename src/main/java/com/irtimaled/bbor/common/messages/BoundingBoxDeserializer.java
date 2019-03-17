package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.*;
import net.minecraft.network.PacketBuffer;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class BoundingBoxDeserializer {
    static BoundingBox deserialize(PacketBuffer buf) {
        if (!buf.isReadable(2)) return null;

        char type = buf.readChar();
        switch (type) {
            case 'V':
                return deserializeVillage(buf);
            case 'S':
                return deserializeStructure(buf);
            case 'M':
                return deserializeMobSpawner(buf);
        }
        return null;
    }

    private static BoundingBox deserializeStructure(PacketBuffer buf) {
        BoundingBoxType type = BoundingBoxType.getByNameHash(buf.readInt());
        if (type == null) return null;
        Coords minCoords = deserializeCoords(buf);
        Coords maxCoords = deserializeCoords(buf);
        return BoundingBoxStructure.from(minCoords, maxCoords, type);
    }

    private static BoundingBox deserializeVillage(PacketBuffer buf) {
        Coords center = deserializeCoords(buf);
        int radius = buf.readVarInt();
        boolean spawnsIronGolems = buf.readBoolean();
        Color color = new Color(buf.readVarInt());
        Set<Coords> doors = new HashSet<>();
        while (buf.isReadable()) {
            Coords door = deserializeCoords(buf);
            doors.add(door);
        }
        return BoundingBoxVillage.from(center, radius, color, spawnsIronGolems, doors);
    }

    private static BoundingBox deserializeMobSpawner(PacketBuffer buf) {
        Coords center = deserializeCoords(buf);
        return BoundingBoxMobSpawner.from(center);
    }

    private static Coords deserializeCoords(PacketBuffer buf) {
        int x = buf.readVarInt();
        int y = buf.readVarInt();
        int z = buf.readVarInt();
        return new Coords(x, y, z);
    }
}
