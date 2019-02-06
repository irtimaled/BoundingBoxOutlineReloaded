package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.Coords;

class BoundingBoxDeserializer {
    static AbstractBoundingBox deserialize(PayloadReader reader) {
        if (!reader.isReadable(2)) return null;

        char type = reader.readChar();
        switch (type) {
            case 'S':
                return deserializeStructure(reader);
            case 'M':
                return deserializeMobSpawner(reader);
        }
        return null;
    }

    private static AbstractBoundingBox deserializeStructure(PayloadReader reader) {
        BoundingBoxType type = BoundingBoxType.getByNameHash(reader.readInt());
        if (type == null) return null;
        Coords minCoords = reader.readCoords();
        Coords maxCoords = reader.readCoords();
        return BoundingBoxStructure.from(minCoords, maxCoords, type);
    }

    private static AbstractBoundingBox deserializeMobSpawner(PayloadReader reader) {
        return BoundingBoxMobSpawner.from(reader.readCoords());
    }
}
