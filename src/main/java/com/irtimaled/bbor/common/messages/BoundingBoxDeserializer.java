package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

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
        BlockPos minBlockPos = deserializeBlockPos(buf);
        BlockPos maxBlockPos = deserializeBlockPos(buf);
        return BoundingBoxStructure.from(minBlockPos, maxBlockPos, type);
    }

    private static BoundingBox deserializeVillage(PacketBuffer buf) {
        BlockPos center = deserializeBlockPos(buf);
        int radius = buf.readVarInt();
        boolean spawnsIronGolems = buf.readBoolean();
        Color color = new Color(buf.readVarInt());
        Set<BlockPos> doors = new HashSet<>();
        while (buf.isReadable()) {
            BlockPos door = deserializeBlockPos(buf);
            doors.add(door);
        }
        return BoundingBoxVillage.from(center, radius, color, spawnsIronGolems, doors);
    }

    private static BoundingBox deserializeMobSpawner(PacketBuffer buf) {
        BlockPos center = deserializeBlockPos(buf);
        return BoundingBoxMobSpawner.from(center);
    }

    private static BlockPos deserializeBlockPos(PacketBuffer buf) {
        int x = buf.readVarInt();
        int y = buf.readVarInt();
        int z = buf.readVarInt();
        return new BlockPos(x, y, z);
    }
}
