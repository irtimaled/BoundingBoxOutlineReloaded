package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxStructure;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

class BoundingBoxSerializer {
    static void serialize(BoundingBox boundingBox, PacketBuffer buf) {
        if (boundingBox instanceof BoundingBoxVillage) {
            serializeVillage((BoundingBoxVillage) boundingBox, buf);
        }
        if (boundingBox instanceof BoundingBoxStructure) {
            serializeStructure((BoundingBoxStructure) boundingBox, buf);
        }
        if (boundingBox instanceof BoundingBoxMobSpawner) {
            serializeMobSpawner((BoundingBoxMobSpawner) boundingBox, buf);
        }
    }

    private static void serializeVillage(BoundingBoxVillage boundingBox, PacketBuffer buf) {
        buf.writeChar('V');
        serializeBlockPos(boundingBox.getCenter(), buf);
        buf.writeVarInt(boundingBox.getRadius());
        buf.writeBoolean(boundingBox.getSpawnsIronGolems());
        serializeColor(boundingBox.getColor(), buf);
        for (BlockPos door : boundingBox.getDoors()) {
            serializeBlockPos(door, buf);
        }
    }

    private static void serializeStructure(BoundingBoxStructure boundingBox, PacketBuffer buf) {
        buf.writeChar('S');
        buf.writeInt(boundingBox.getTypeName().hashCode());
        serializeCuboid(boundingBox, buf);
    }

    private static void serializeMobSpawner(BoundingBoxMobSpawner boundingBox, PacketBuffer buf) {
        buf.writeChar('M');
        serializeBlockPos(boundingBox.getCenter(), buf);
    }

    private static void serializeColor(Color color, PacketBuffer buf) {
        buf.writeVarInt(color.getRGB());
    }

    private static void serializeCuboid(BoundingBox boundingBox, PacketBuffer buf) {
        serializeBlockPos(boundingBox.getMinBlockPos(), buf);
        serializeBlockPos(boundingBox.getMaxBlockPos(), buf);
    }

    private static void serializeBlockPos(BlockPos blockPos, PacketBuffer buf) {
        buf.writeVarInt(blockPos.getX());
        buf.writeVarInt(blockPos.getY());
        buf.writeVarInt(blockPos.getZ());
    }
}
