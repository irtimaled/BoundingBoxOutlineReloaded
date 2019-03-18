package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class BoundingBoxDeserializer {
    static BoundingBox deserialize(PayloadReader reader) {
        if (!reader.isReadable(2)) return null;

        char type = reader.readChar();
        switch (type) {
            case 'V':
                return deserializeVillage(reader);
            case 'S':
                return deserializeStructure(reader);
            case 'M':
                return deserializeMobSpawner(reader);
        }
        return null;
    }

    private static BoundingBox deserializeStructure(PayloadReader reader) {
        BoundingBoxType type = BoundingBoxType.getByNameHash(reader.readInt());
        if (type == null) return null;
        Coords minCoords = reader.readCoords();
        Coords maxCoords = reader.readCoords();
        return BoundingBoxStructure.from(minCoords, maxCoords, type);
    }

    private static BoundingBox deserializeVillage(PayloadReader reader) {
        Coords center = reader.readCoords();
        int radius = reader.readVarInt();
        boolean spawnsIronGolems = reader.readBoolean();
        Color color = new Color(reader.readVarInt());
        Set<Coords> doors = new HashSet<>();
        while (reader.isReadable()) {
            doors.add(reader.readCoords());
        }
        return BoundingBoxVillage.from(center, radius, color, spawnsIronGolems, doors);
    }

    private static BoundingBox deserializeMobSpawner(PayloadReader reader) {
        return BoundingBoxMobSpawner.from(reader.readCoords());
    }
}
